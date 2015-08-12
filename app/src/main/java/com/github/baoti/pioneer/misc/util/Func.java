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

import java.util.ArrayList;
import java.util.List;

public class Func {
    public static <Src, Dst> List<Dst> map(List<Src> src, Transformer<Src, Dst> transformer) {
        if (src == null) {
            return null;
        }
        List<Dst> dst = new ArrayList<Dst>(src.size());
        for (Src item : src) {
            dst.add(transformer.transform(item));
        }
        return dst;
    }

    public interface Transformer<Src, Dst> {
        Dst transform(Src src);
    }
}
