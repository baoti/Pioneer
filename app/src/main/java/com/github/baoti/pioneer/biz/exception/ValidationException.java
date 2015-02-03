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

package com.github.baoti.pioneer.biz.exception;

/**
 * Created by liuyedong on 14-12-25.
 */
public class ValidationException extends BizException {
    public final Kind kind;
    public final String target;
    public final String reason;

    public ValidationException(Kind kind) {
        this(kind, kind.target, kind.reason);
    }

    public ValidationException(String reason) {
        this("", reason);
    }

    public ValidationException(String target, String reason) {
        this(Kind.DEFAULT, target, reason);
    }

    public ValidationException(Kind kind, String target, String reason) {
        super(target + reason);
        this.kind = kind;
        this.target = target;
        this.reason = reason;
    }

    public ValidationException withTarget(String target) {
        return new ValidationException(target, reason);
    }

    public boolean isTargetIn(String ...targets) {
        for (String t : targets) {
            if (target.equals(t)) {
                return true;
            }
        }
        return false;
    }

    public static enum Kind {
        DEFAULT,
        ACCOUNT_EMPTY,
        PASSWORD_EMPTY;

        private final String target;
        private final String reason;

        private Kind() {
            target = getTarget(name());
            reason = getReason(name());
        }

        private Kind(String target, String reason) {
            this.target = target;
            this.reason = reason;
        }

        private static String getTarget(String name) {
            int index = name.indexOf('_');
            if (index == -1) {
                return name.toLowerCase();
            } else {
                return name.toLowerCase().substring(0, index);
            }
        }

        private static String getReason(String name) {
            int index = name.indexOf('_');
            if (index == -1) {
                return name.toLowerCase();
            } else {
                return name.toLowerCase().substring(index + 1).replace('_', ' ');
            }
        }
    }
}
