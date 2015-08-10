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

package com.github.baoti.pioneer.app.widget;

import android.view.View;
import android.view.ViewGroup;

public class ViewUtils {
    public static <V extends View> V setGone(V view, boolean gone) {
        if (view != null) {
            if (gone) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
        }
        return view;
    }

    public static <V extends View> V setInvisible(V view, boolean invisible) {
        if (view != null) {
            if (invisible) {
                view.setVisibility(View.INVISIBLE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
        }
        return view;
    }

    /**
     * 调整 parent 的 child views 到指定的数量，并存入 target
     */
    @SuppressWarnings("unchecked")
    public static <V extends View> void fitChildViews(ViewGroup parent, V[] target, ViewPool<V> pool) {
        int childCount = parent.getChildCount();
        // 移除多出的项
        while (target.length < childCount) {
            V view = (V) parent.getChildAt(0);
            parent.removeViewAt(0);
            pool.recycle(view);
            childCount--;
        }
        // 收集已有的项
        for (int i = 0; i < childCount; i++) {
            target[i] = (V) parent.getChildAt(i);
        }
        // 添加并收集新增的项
        while (target.length > childCount) {
            target[childCount] = pool.obtain(parent);
            parent.addView(target[childCount]);
            childCount++;
        }
    }

    public interface ViewPool<V extends View> {
        V obtain(ViewGroup parent);

        void recycle(V view);
    }
}
