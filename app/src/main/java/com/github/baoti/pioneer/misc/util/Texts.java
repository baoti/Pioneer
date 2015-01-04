package com.github.baoti.pioneer.misc.util;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 文本相关处理
 *
 * Created by liuyedong on 14-12-19.
 */
public class Texts {
    public static final String CHARSET = "UTF-8";

    private Texts() {
    }

    /** null 安全版的 toString() */
    public static String str(CharSequence s) {
        return str(s, "");
    }

    /** null 安全 的 toString() */
    public static String str(Object o, String defVal) {
        return o == null ? defVal : o.toString();
    }

    /** null 安全版的 移除首尾空白 */
    public static String trim(CharSequence s) {
        if (s == null) {
            return "";
        }
        return s.toString().trim();
    }

    /** 移除首尾空白后是否为空串 */
    public static boolean isTrimmedEmpty(CharSequence s) {
        return trimmedLength(s) <= 0;
    }

    /** null 安全版的 TextUtils.getTrimmedLength */
    public static int trimmedLength(CharSequence s) {
        if (s == null) {
            return 0;
        }
        return TextUtils.getTrimmedLength(s);
    }

    /** null 安全版的 String.length */
    public static int length(CharSequence s) {
        if (s == null) {
            return 0;
        }
        return s.length();
    }

    /** 使用指定字符填充字符串 */
    public static String filledStr(int length, char filledChar) {
        char[] array = new char[length];
        Arrays.fill(array, filledChar);
        return new String(array);
    }

    public static String base64(String s) {
        byte[] sourceBytes = s.getBytes(Charset.forName(CHARSET));
        return Base64.encodeToString(sourceBytes, Base64.DEFAULT);
    }

    public static String md5Hex(boolean upperCase, String... strings) {
        return bytesToHexString(md5Bytes(strings), upperCase);
    }

    public static byte[] md5Bytes(String... strings) {
        try {
            boolean hasData = false;
            final MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            for (String s : strings) {
                if (!TextUtils.isEmpty(s)) {
                    md5Digest.update(s.getBytes(CHARSET));
                    hasData = true;
                }
            }
            if (!hasData) {
                return new byte[0];
            }
            return md5Digest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("NO MD5");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("NO UTF-8");
        }
    }

    /**
     * The digits for every supported radix.
     */
    private static final char[] DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z'
    };

    private static final char[] UPPER_CASE_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    /**
     * 字节数组转十六进制字符串
     *
     * Copy from java.lang.IntegralToString.
     */
    public static String bytesToHexString(byte[] bytes, boolean upperCase) {
        char[] digits = upperCase ? UPPER_CASE_DIGITS : DIGITS;
        char[] buf = new char[bytes.length * 2];
        int c = 0;
        for (byte b : bytes) {
            buf[c++] = digits[(b >> 4) & 0xf];
            buf[c++] = digits[b & 0xf];
        }
        return new String(buf);
    }

    public static CharSequence withColor(CharSequence text, int color) {
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        ssb.setSpan(colorSpan, 0, text.length(), 0);
        return ssb;
    }
}
