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

package com.github.baoti.pioneer.misc.util;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 文本工具类, 补充 android.text.TextUtils
 * <p/>
 * Created by liuyedong on 14-12-19.
 */
public class Texts {
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    private Texts() {
    }

    /**
     * null 安全版的 toString()
     */
    public static String str(CharSequence s) {
        return str(s, "");
    }

    /**
     * null 安全 的 toString()
     */
    public static String str(Object o, String ifNull) {
        return o == null ? ifNull : o.toString();
    }

    public static String strNotEmpty(CharSequence s, String ifEmpty) {
        return TextUtils.isEmpty(s) ? ifEmpty : s.toString();
    }

    public static <T> Func.Transformer<T, String> strTransformer(final String ifNull) {
        return new Func.Transformer<T, String>() {
            @Override
            public String transform(T t) {
                return t == null ? ifNull : t.toString();
            }
        };
    }

    public static <T> Func.Transformer<T, CharSequence> textTransformer(final String ifNull) {
        return new Func.Transformer<T, CharSequence>() {
            @Override
            public CharSequence transform(T t) {
                if (t == null)
                    return ifNull;
                if (t instanceof CharSequence)
                    return (CharSequence) t;
                return t.toString();
            }
        };
    }

    /**
     * null 安全版的 移除首尾空白
     */
    public static String trim(CharSequence s) {
        if (s == null) {
            return "";
        }
        return s.toString().trim();
    }

    /**
     * 移除首尾空白后是否为空串
     */
    public static boolean isTrimmedEmpty(CharSequence s) {
        return trimmedLength(s) <= 0;
    }

    /**
     * null 安全版的 TextUtils.getTrimmedLength
     */
    public static int trimmedLength(CharSequence s) {
        if (s == null) {
            return 0;
        }
        return TextUtils.getTrimmedLength(s);
    }

    /**
     * null 安全版的 String.length
     */
    public static int length(CharSequence s) {
        if (s == null) {
            return 0;
        }
        return s.length();
    }

    /**
     * Return a copy of the string S with leading and trailing
     * whitespace removed.
     */
    public static String trip(CharSequence s, String chars) {
        if (s == null) {
            return "";
        }
        int start = 0;
        int length = s.length();
        while (start < length && chars.contains(s.subSequence(start, start + 1))) {
            start++;
        }
        int end = length;
        while (end > start && chars.contains(s.subSequence(end - 1, end))) {
            end--;
        }
        return s.subSequence(start, end).toString();
    }

    /**
     * 使用指定字符填充字符串
     */
    public static String filledStr(int length, char filledChar) {
        char[] array = new char[length];
        Arrays.fill(array, filledChar);
        return new String(array);
    }

    /**
     * 首字符组合
     */
    public static String initials(String[] words) {
        StringBuilder builder = new StringBuilder(words.length);
        for (String word : words) {
            if (word.length() > 0) {
                builder.append(word.charAt(0));
            }
        }
        return builder.toString();
    }

    public static <T> String join(CharSequence sep, T[] tokens,
                                  boolean skipNull, Func.Transformer<T, String> transformer) {
        if (tokens == null) {
            return "";
        }
        if (transformer == null) {
            transformer = strTransformer(null);
        }
        StringBuilder result = new StringBuilder();
        for (T item : tokens) {
            if (skipNull && item == null) {
                continue;
            }
            String strToken = transformer.transform(item);
            if (skipNull && strToken == null) {
                continue;
            }
            if (result.length() > 0) {
                result.append(sep);
            }
            result.append(strToken);
        }
        return result.toString();
    }

    public static <T> String join(CharSequence sep, Iterable<T> tokens,
                                  boolean skipNull, Func.Transformer<T, String> transformer) {
        if (tokens == null) {
            return "";
        }
        if (transformer == null) {
            transformer = strTransformer(null);
        }
        StringBuilder result = new StringBuilder();
        for (T item : tokens) {
            if (skipNull && item == null) {
                continue;
            }
            String strToken = transformer.transform(item);
            if (skipNull && strToken == null) {
                continue;
            }
            if (result.length() > 0) {
                result.append(sep);
            }
            result.append(strToken);
        }
        return result.toString();
    }

    public static String base64(String s) {
        return Base64.encodeToString(s.getBytes(UTF_8), Base64.DEFAULT);
    }

    public static String md5(boolean upperCase, String s) {
        return bytesToHexString(md5(s.getBytes(UTF_8)), upperCase);
    }

    public static byte[] md5(byte[]... bytes) {
        return digest("MD5", bytes);
    }

    public static byte[] digest(String algorithm, byte[]... bytes) {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            for (byte[] item : bytes) {
                messageDigest.update(item);
            }
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm: " + algorithm);
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
     * <p/>
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
