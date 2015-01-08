package com.github.baoti.pioneer.misc.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

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
        return Intents.hasResolvedActivity(
                context, new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
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

        private static Intent actionCaptureImage(Uri output) {
            return new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    .putExtra(MediaStore.EXTRA_OUTPUT, output);
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
    }
}
