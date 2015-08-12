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

package com.github.baoti.pioneer.ui.common.page;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.baoti.pioneer.ui.common.holder.LoadingViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by liuyedong on 2015/1/2.
 */
public abstract class PageAdapter<E> extends RecyclerView.Adapter {

    protected final LayoutInflater inflater;
    protected final PagePresenter<?, E> presenter;
    protected final List<E> items = new ArrayList<>();

    public PageAdapter(LayoutInflater inflater, PagePresenter<?, E> presenter) {
        this.inflater = inflater;
        this.presenter = presenter;
    }

    public void changeItems(Collection<E> resources, int start, int before, int count) {
        items.clear();
        items.addAll(resources);
        notifyItemChanged(start, before, count);
    }

    protected void notifyItemChanged(int start, int before, int count) {
        if (before == 0) {
            if (start == 0) {
                // Prevent scrolling to bottom
                notifyDataSetChanged();
            } else {
                notifyItemRangeInserted(start, count);
            }
        } else {
            if (before < count) {
                notifyItemRangeInserted(start + before, count - before);
            }
            if (before > count) {
                notifyItemRangeRemoved(start + count, before - count);
            }
            notifyItemRangeChanged(start, Math.min(before, count));
        }
    }

    public abstract void notifyLoadingChanged();

    protected RecyclerView.ViewHolder createLoadingViewHolder(ViewGroup parent) {
        return LoadingViewHolder.create(inflater, parent);
    }

    protected void bindLoadingViewHolder(LoadingViewHolder viewHolder) {
        viewHolder.indicator.updateStatus(presenter);
    }
}
