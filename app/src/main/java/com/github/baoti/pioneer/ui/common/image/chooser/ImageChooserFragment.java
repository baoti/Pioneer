package com.github.baoti.pioneer.ui.common.image.chooser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.misc.util.ImageActions;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

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

    private ImageActions.CaptureCompat imageCapture;
    private ImageActions.Crop imageCrop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageCapture = new ImageActions.CaptureCompat(getActivity());
        imageCrop = new ImageActions.Crop();

        if (savedInstanceState != null) {
            imageCapture.onLoad(savedInstanceState);
            imageCrop.onLoad(savedInstanceState);
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
        ButterKnife.inject(this, view);
        if (getShowsDialog()) {
            setupDialog();
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (imageCapture != null) {
            imageCapture.onSave(outState);
        }
        if (imageCrop != null) {
            imageCrop.onSave(outState);
        }
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
                if (imageCapture != null && resultCode != Activity.RESULT_CANCELED) {
                    Uri image = imageCapture.capturedImage(resultCode, data);
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
                if (imageCrop != null && resultCode != Activity.RESULT_CANCELED) {
                    Uri image = imageCrop.croppedImage(resultCode, data);
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
            intent = imageCapture.action();
        } catch (IOException e) {
            Timber.w(e, "Could not capture image");
            alert("存储空间不可用, 无法使用拍照功能");
            return;
        }
        startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
    }

    @OnClick(R.id.item_pick_image) void onPickImageClicked() {
        startActivityForResult(ImageActions.actionPickImage(), REQUEST_PICK_IMAGE);
    }

    private boolean cropImage(Uri image) {
        try {
            startActivityForResult(imageCrop.action(image), REQUEST_CROP_IMAGE);
            return true;
        } catch (ActivityNotFoundException e) {
            verbose("您的手机不支持图片裁剪功能");
            return false;
        }
    }

    private void verbose(String msg, Object... args) {
        String text = String.format(msg, args);
        SnackbarManager.show(Snackbar.with(getActivity())
                .type(SnackbarType.MULTI_LINE)
                .text(text));
    }

    public interface OnImageChooserListener {
        void onImageChose(int requestCode, @Nullable Uri image);
        void onImageCancelled(int requestCode);
    }
}
