package com.github.baoti.pioneer.misc.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.github.baoti.pioneer.Constants;
import com.github.baoti.pioneer.data.DataConstants;

import java.io.File;
import java.io.IOException;

import timber.log.Timber;

/**
 * Created by liuyedong on 15-1-6.
 */
public class ImageActions {

    public static Intent actionPickImage() {
        return new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    public static Intent actionPickImageFromInternal() {
        return new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
    }

    public static Uri pickedImage(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return null;
        }
        return data.getData();
    }


    public static boolean isImageCaptureAvailable(Context context) {
        return Intents.hasResolvedActivity(context, CaptureCompat.baseAction());
    }

    public static boolean isImageCropAvailable(Context context) {
        return Intents.hasResolvedActivity(context, Crop.baseAction());
    }

    public static class Crop {
        private static final String ACTION_CROP = "com.android.camera.action.CROP";

        private static final String RETURN_DATA = "return-data";

        private Intent intent;
        public Crop() {

        }

        public Intent action(Uri image) {
            Intent actionIntent = actionCropImage(image);
            intent = actionIntent;
            return actionIntent;
        }

        public Uri croppedImage(int resultCode, Intent data) {
            if (resultCode != Activity.RESULT_OK || intent == null) {
                return null;
            }
            Intent actionIntent = intent;
            intent = null;
            boolean returnData = actionIntent.getBooleanExtra(RETURN_DATA, false);
            if (returnData && data == null) {
                return null;
            }
            Uri output = actionIntent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
            if (output != null) {
                return output;
            }
            Bitmap bm = data.getParcelableExtra("data");
            if (bm == null) {
                return null;
            }
            try {
                File imagesDir = IoUtils.fromFilesDir(DataConstants.Files.IMAGES);
                if (!IoUtils.ensureDirsExist(imagesDir)) {
                    return null;
                }
                File dst = IoUtils.generateDatedFile(imagesDir, ".jpg", true);
                if (!IoUtils.saveBitmap(dst, bm, 50)) {
                    return null;
                }
                return Uri.fromFile(dst);
            } finally {
                bm.recycle();
            }
        }

        private static Intent baseAction() {
            return new Intent(ACTION_CROP).setType("image/*");
        }

        private static Intent actionCropImage(Uri image) {
            Intent intent = baseAction();
            if (image != null) {
                intent.setData(image);
            } else {
                // NOT SUPPORTED!!!
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            }
            intent.putExtra("crop", "true");

            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);

            // outputX,outputY 是剪裁图片的宽高
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);

            intent.putExtra("noFaceDetection", true);

            intent.putExtra("scale", true);
            intent.putExtra("scaleUpIfNeeded", true); //剪裁区域太小去除黑边
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cropOutput));
//        intent.putExtra("return-data", false);
            intent.putExtra(RETURN_DATA, true);
            return intent;
        }

        public void onLoad(Bundle savedInstanceState) {
            intent = savedInstanceState.getParcelable(Constants.Saved.SAVED_CROP_IMAGE_INTENT);
        }

        public void onSave(Bundle outState) {
            outState.putParcelable(Constants.Saved.SAVED_CROP_IMAGE_INTENT, intent);
        }
    }

    public static class CaptureCompat {

        private final Context context;
        private File outputImage;
        private Uri capturedUri;

        public CaptureCompat(Context context) {
            this.context = context.getApplicationContext();
        }

        public Intent action() throws IOException {
            capturedUri = null;
            outputImage = IoUtils.createTempFile(null, true);
            return actionCaptureImage(Uri.fromFile(outputImage));
        }

        public Uri capturedImage(int resultCode, Intent data) {
            if (capturedUri == null && outputImage != null) {
                File file = outputImage;
                outputImage = null;
                if (file.length() == 0) {
                    Timber.w("Image file is empty");
                    return null;
                }

                try {
                    if (resultCode == Activity.RESULT_OK) {
                        Timber.v("Image file's length: %s", file.length());
                        capturedUri = insertToMediaImages(context, file);
                        if (capturedUri == null) {
                            capturedUri = insertToMediaImagesCompat(context, file);
                        }
                    }
                } finally {
                    if (!file.delete()) {
                        Timber.w("Could not delete tmp image file: %s", file);
                    }
                }
            }
            return capturedUri;
        }

        public File getTmpOutputFile() {
            return outputImage;
        }

        public void setTmpOutputFile(File file) {
            outputImage = file;
        }

        private static Intent baseAction() {
            return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        }

        private static Intent actionCaptureImage(Uri output) {
            return baseAction().putExtra(MediaStore.EXTRA_OUTPUT, output);
        }

        private static Uri insertToMediaImages(Context context, File file) {
            Bitmap bm = null;
            try {
                bm = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (bm == null) {
                    Timber.w("Could not decode image: %s", file);
                    return null;
                }
                return Uri.parse(MediaStore.Images.Media.insertImage(
                        context.getContentResolver(),
                        bm,
                        "",
                        ""));
            } catch (Throwable e) {
                Timber.w("Could not insert to media: %s", file);
                return null;
            } finally {
                if (bm != null) {
                    bm.recycle();
                }
            }
        }

        private static Uri insertToMediaImagesCompat(Context context, File file) {
            File cameraDirectory = IoUtils.getDCIMDirectory("Camera");
            File dst = IoUtils.generateDatedFile(cameraDirectory, ".jpg");
            if (IoUtils.copyFile(file, dst)) {
                Uri uri = Uri.fromFile(dst);
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(uri);
                context.sendBroadcast(mediaScanIntent);
                return uri;
            }
            return null;
        }

        public void onLoad(Bundle savedInstanceState) {
            setTmpOutputFile(
                    (File) savedInstanceState.getSerializable(Constants.Saved.SAVED_CAPTURE_IMAGE_OUTPUT));
        }

        public void onSave(Bundle outState) {
            outState.putSerializable(Constants.Saved.SAVED_CAPTURE_IMAGE_OUTPUT, getTmpOutputFile());
        }
    }
}
