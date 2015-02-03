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
