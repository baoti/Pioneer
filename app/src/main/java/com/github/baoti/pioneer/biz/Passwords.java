package com.github.baoti.pioneer.biz;

import com.github.baoti.pioneer.misc.util.Texts;

/**
 * Created by liuyedong on 14-12-25.
 */
public class Passwords {
    /** 生成账号的最终密码 */
    public static String forAccount(String account, String password) {
        return Texts.md5Hex(false, "pioneer:", account, ":", password);
    }
}
