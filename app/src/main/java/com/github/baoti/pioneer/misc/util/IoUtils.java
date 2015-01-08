package com.github.baoti.pioneer.misc.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import com.github.baoti.pioneer.AppMain;
import com.github.baoti.pioneer.misc.config.DataConfig;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Locale;

import timber.log.Timber;

/**
 * IO相关
 *
 * Created by sean on 2014/10/12.
 */
public class IoUtils {

    public static boolean saveFile(File file, byte[] content) {
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Timber.d(e, "[saveFile] Couldn't open output");
            return false;
        }
        boolean saveOk = true;
        try {
            outputStream.write(content);
        } catch (IOException e) {
            Timber.d(e, "[saveFile] Couldn't write");
            return false;
        } finally {
            if (!close(outputStream)) {
                saveOk = false;
            }
        }
        if (!saveOk) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
        return saveOk;
    }

    public static boolean copyAssets(String assetsPath, File dst) {
        InputStream is;
        try {
            is = AppMain.app().getAssets().open(assetsPath);
        } catch (IOException e) {
            Timber.d(e, "[copyAssets] Couldn't open assets");
            return false;
        }
        try {
            return copyStream(is, dst);
        } finally {
            close(is);
        }
    }

    public static boolean copyFile(File src, File dst) {
        FileInputStream fis;
        try {
            fis = new FileInputStream(src);
        } catch (FileNotFoundException e) {
            Timber.d(e, "[copyFile] Couldn't open input");
            return false;
        }
        try {
            return copyStream(fis, dst);
        } finally {
            close(fis);
        }
    }

    public static boolean copyStream(InputStream src, File dst) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(dst);
        } catch (FileNotFoundException e) {
            Timber.d(e, "[copyStream] Couldn't open output");
            return false;
        }
        boolean copyOk = true;
        try {
            copyStream(src, fos);
        } catch (IOException e) {
            copyOk = false;
            Timber.d(e, "[copyStream] Couldn't copy");
        } finally {
            if (!close(fos)) {
                copyOk = false;
            }
        }
        if (!copyOk) {
            Timber.d("[copyStream] Copy failed");
            //noinspection ResultOfMethodCallIgnored
            dst.delete();
        }
        return copyOk;
    }

    public static void copyStream(InputStream src, OutputStream dst) throws IOException {
        byte buffer[] = new byte[1024];
        int len;
        while ((len = src.read(buffer)) != -1) {
            dst.write(buffer, 0, len);
        }
    }

    public static boolean close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                Timber.d("[close] Close failed");
                return false;
            }
        }
        return true;
    }

    /**
     * 从InputStream中读取String，该函数不执行关闭操作
     *
     * @param in InputStream
     * @return String
     * @throws Exception
     */
    public static String readString(InputStream in) throws Exception {
        return new String(toByteArray(in), "UTF-8");
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n;
        while (-1 != (n = input.read(buffer))) {
            byteArrayOutputStream.write(buffer, 0, n);
        }
        return byteArrayOutputStream.toByteArray();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static long getExtStorageAvailableApi18() {
        if (hasExtStorage()) {
            File extStorage = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(extStorage.getAbsolutePath());
            return sf.getAvailableBlocksLong() * sf.getBlockSizeLong();
        }
        return 0;
    }

    @SuppressWarnings("deprecation")
    private static long getExtStorageAvailableSmallerThanApi18() {
        if (hasExtStorage()) {
            File extStorage = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(extStorage.getAbsolutePath());
            return ((long) sf.getAvailableBlocks()) * sf.getBlockSize();
        }
        return 0;
    }

    public static long getExtStorageAvailable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return getExtStorageAvailableApi18();
        } else {
            return getExtStorageAvailableSmallerThanApi18();
        }
    }

    public static boolean hasExtStorage() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /** rm -r */
    public static boolean rmR(File file) {
        if (!file.exists()) {
            return true;
        }
        boolean result = true;
        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                result &= rmR(subFile);
            }
        } else {
            result = file.delete();
        }
        return result;
    }

    /**
     * 保证目录存在
     * @param dir
     * @return
     */
    public static boolean ensureDirsExist(File dir) {
        if (!dir.isDirectory()) {
            if (!dir.mkdirs()) {
                // recheck existence in case of cross-process race
                if (!dir.isDirectory()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 获得在文件存储中的文件
     */
    public static File fromFilesDirCompat(String path) {
        if (hasExtStorage()) {
            // 保持旧版本兼容
            if (path == null) {
                return Environment.getExternalStorageDirectory();
            } else {
                return new File(Environment.getExternalStorageDirectory(), path);
            }
        }
        return fromFilesDir(path);
    }

    /**
     * 获得在文件存储中的文件
     */
    public static File fromFilesDir(String path) {
        if (path != null && path.startsWith(File.separator)) {
            path = path.substring(File.separator.length());
        }
        Context context = AppMain.app();
        File filesDir = context.getExternalFilesDir(null);
        File rollback = filesDir;
        if (filesDir != null && !ensureDirsExist(filesDir)) {
            filesDir = null;
        }
        if (filesDir == null) {
            filesDir = context.getFilesDir();
        }
        if (filesDir == null) {
            filesDir = rollback;
        }
        if (path == null) {
            return filesDir;
        } else {
            return new File(filesDir, path);
        }
    }

    /**
     * 获得在缓存存储中的文件
     * @param path
     * @return
     */
    public static File fromCacheDir(String path) {
        if (path != null && path.startsWith(File.separator)) {
            path = path.substring(File.separator.length());
        }
        Context context = AppMain.app();
        File cacheDir = context.getExternalCacheDir();
        File rollback = cacheDir;
        if (cacheDir != null && !ensureDirsExist(cacheDir)) {
            cacheDir = null;
        }
        if (cacheDir == null) {
            // maybe null
            cacheDir = context.getCacheDir();
        }
        if (cacheDir == null) {
            cacheDir = rollback;
        }
        if (path == null) {
            return cacheDir;
        } else {
            return new File(cacheDir, path);
        }
    }

    /**
     * 是否应用私有的文件
     * @param file
     * @return
     */
    public static boolean isAppPrivate(File file) {
        return isInDataDir(file);
    }

    /**
     * 文件是否在 内部 Data 目录下
     * @param file
     * @return
     */
    public static boolean isInDataDir(File file) {
        if (file == null) {
            return false;
        }
        String dataDir = AppMain.app().getApplicationInfo().dataDir;
        if (dataDir != null) {
            dataDir = new File(dataDir).getAbsolutePath();  // Removing any trailing slash
            String filePath = file.getAbsolutePath();
            return filePath.equals(dataDir) || filePath.startsWith(dataDir + File.separator);
        }
        return false;
    }

    @NonNull
    public static File getDCIMDirectory(String type) {
        File directory = getAvailableExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return new File(directory, type);
    }

    @NonNull
    public static File getAvailableExternalStoragePublicDirectory(String type) {
        File result = Environment.getExternalStoragePublicDirectory(type);
        if (ensureDirsExist(result)) {
            return result;
        }
        File dirs[] = buildExternalStoragePublicDirs(type);
        if (dirs != null) {
            for (File dir : dirs) {
                File d = new File(dir, type);
                if (ensureDirsExist(d)) {
                    return d;
                }
            }
        }
        return result;
    }

    private static Method buildExternalDirsOnUserEnvironment;
    private static Object currentUserEnvironment;
    static {
        try {
            Field field = Environment.class.getDeclaredField("sCurrentUser");
            currentUserEnvironment = field.get(Environment.class);
            Class<?> cls = Class.forName("android.os.Environment.UserEnvironment");
            buildExternalDirsOnUserEnvironment = cls.getDeclaredMethod(
                    "buildExternalStoragePublicDirs", String.class);
            buildExternalDirsOnUserEnvironment.setAccessible(true);
        } catch (Exception ignored) {
        }
    }

    private static File[] buildExternalStoragePublicDirs(String type) {
        if (buildExternalDirsOnUserEnvironment != null && currentUserEnvironment != null) {
            try {
                Object result = buildExternalDirsOnUserEnvironment.invoke(currentUserEnvironment, type);
                return (File[]) result;
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    @NonNull
    public static File generateDatedFile(File directory, String suffix) {
        boolean dirOk = IoUtils.ensureDirsExist(directory);
        if (!dirOk) {
            Timber.v("Fail to create directory: " + directory.getPath());
        }
        String filename = DateFormat.format("yyyyMMdd_hhmmss",
                Calendar.getInstance(Locale.CHINA)) + suffix;
        return new File(directory, filename);
    }

    @NonNull
    public static File createTempFile(String suffix, boolean isPublic) throws IOException {
        File directory;
        if (isPublic) {
            directory = null;
        } else {
            directory = fromCacheDir(DataConfig.TMP_DIRECTORY_NAME);
            if (directory != null && !ensureDirsExist(directory)) {
                Timber.w("Could not create tmp directory in cacheDir");
                directory = null;
            }
        }
        File tmp = File.createTempFile(DataConfig.TMP_FILE_PREFIX, suffix, directory);
        Timber.v("Create tmp file: %s", tmp);
        return tmp;
    }

    public static final String SCHEME_FILE = "file";

    public static boolean isFile(Uri uri) {
        return uri != null && SCHEME_FILE.equals(uri.getScheme());
    }

    public static File toFile(Uri uri) {
        if (isFile(uri)) {
            return new File(uri.getPath());
        }
        return null;
    }
}
