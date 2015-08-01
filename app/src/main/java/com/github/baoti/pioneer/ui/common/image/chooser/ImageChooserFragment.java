/*
 * Copyright (c) 2014-2015 Sean Liu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.baoti.pioneer.ui.common.image.chooser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.misc.util.ActivityRequestState;
import com.github.baoti.pioneer.misc.util.ImageActions;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by liuyedong on 15-1-6.
 */
public class ImageChooserFragment extends DialogFragment {

    private static final String TAG_FRAG_IMAGE_CHOOSER = "frag_image_chooser";
    private static final String ARG_CROP = "crop";  // Type: boolean

    public static ImageChooserFragment showDialog(FragmentManager fragmentManager, boolean crop) {
        ImageChooserFragment fragment = (ImageChooserFragment) fragmentManager.findFragmentByTag(
                TAG_FRAG_IMAGE_CHOOSER);
        if (fragment == null) {
            fragment = new ImageChooserFragment();
            fragment.setStyle(STYLE_NO_FRAME, R.style.AppTheme_Dialog);
        }
        Bundle args = new Bundle();
        args.putBoolean(ARG_CROP, crop);
        fragment.setArguments(args);
        fragment.show(fragmentManager, TAG_FRAG_IMAGE_CHOOSER);
        return fragment;
    }

    private static final int REQUEST_CAPTURE_IMAGE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private static final int REQUEST_CROP_IMAGE = 3;

    private final ActivityRequestState requestState = new ActivityRequestState();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            requestState.onLoad(savedInstanceState);
        }
    }

    @Override
    public void setTargetFragment(Fragment fragment, int requestCode) {
        if (!(fragment instanceof OnImageChooserListener)) {
            throw new IllegalArgumentException("targetFragment must " +
                    "implements OnImageChooserListener");
        }
        super.setTargetFragment(fragment, requestCode);
    }

    public void close() {
        if (getShowsDialog()) {
            dismiss();
        } else {
            getActivity().onBackPressed();
        }
    }

    public <T extends Fragment & OnImageChooserListener> void setOnChooseListener(
            T listener, int requestCode) {
        setTargetFragment(listener, requestCode);
    }

    protected OnImageChooserListener getListener() {
        return (OnImageChooserListener) getTargetFragment();
    }

    protected void onImageChose(int requestCode, Uri image) {
        if (getArguments().getBoolean(ARG_CROP)
                && ImageActions.isImageCropAvailable(getActivity())) {
            switch (requestCode) {
                case REQUEST_CAPTURE_IMAGE:
                case REQUEST_PICK_IMAGE:
                    if (cropImage(image)) {
                        return;
                    }
            }
        }
        close();
        OnImageChooserListener listener = getListener();
        if (listener != null) {
            listener.onImageChose(getTargetRequestCode(), image);
        }
    }

    protected void onImageCancelled(int requestCode) {
        close();
        OnImageChooserListener listener = getListener();
        if (listener != null) {
            listener.onImageCancelled(getTargetRequestCode());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_image_chooser, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        if (getShowsDialog()) {
            setupDialog();
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        requestState.onSave(outState);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        requestState.setRequestState(intent, null, requestCode);
    }

    private void setupDialog() {
        getDialog().setCanceledOnTouchOutside(true);

        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);

        getDialog().getWindow().setWindowAnimations(R.style.Animation_Window_SlideBottom);
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
//        params.dimAmount = 0.5f;
//        params.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CAPTURE_IMAGE:
                if (resultCode != Activity.RESULT_CANCELED) {
                    Uri image = ImageActions.capturedImage(getActivity(), requestState, resultCode, data);
                    verbose("Captured: %s", image);
                    onImageChose(requestCode, image);
                } else {
                    verbose("Capture cancelled");
                    onImageCancelled(requestCode);
                }
                break;
            case REQUEST_PICK_IMAGE:
                if (resultCode != Activity.RESULT_CANCELED) {
                    Uri image = ImageActions.pickedImage(resultCode, data);
                    verbose("Picked: %s", image);
                    onImageChose(requestCode, image);
                } else {
                    verbose("Pick cancelled");
                    onImageCancelled(requestCode);
                }
                break;
            case REQUEST_CROP_IMAGE:
                if (resultCode != Activity.RESULT_CANCELED) {
                    Uri image = ImageActions.croppedImage(getActivity(), requestState, resultCode, data);
                    verbose("Cropped: %s", image);
                    onImageChose(requestCode, image);
                } else {
                    verbose("Crop cancelled");
                    onImageCancelled(requestCode);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void alert(CharSequence msg) {
        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @OnClick(R.id.item_capture_image) void onCaptureImageClicked() {
        if (!ImageActions.isImageCaptureAvailable(getActivity())) {
            alert("您的手机不支持拍照功能");
            return;
        }
        Intent intent;
        try {
            intent = ImageActions.actionCapture(getActivity());
        } catch (IOException e) {
            Timber.w(e, "Could not capture image");
            alert("存储空间不可用, 无法使用拍照功能");
            return;
        }
        startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
    }

    @OnClick(R.id.item_pick_image) void onPickImageClicked() {
        Intent intent = ImageActions.actionPickImage();
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    private boolean cropImage(Uri image) {
        try {
            Intent intent = ImageActions.actionCrop(getActivity(), image, 200);
            startActivityForResult(intent, REQUEST_CROP_IMAGE);
            return true;
        } catch (ActivityNotFoundException e) {
            verbose("您的手机不支持图片裁剪功能");
            return false;
        }
    }

    private void verbose(String msg, Object... args) {
        if (getView() == null) {
            return;
        }
        String text = String.format(msg, args);
        Snackbar.make(getActivity().findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG)
                .show();
    }

    public interface OnImageChooserListener {
        void onImageChose(int requestCode, @Nullable Uri image);
        void onImageCancelled(int requestCode);
    }
}
