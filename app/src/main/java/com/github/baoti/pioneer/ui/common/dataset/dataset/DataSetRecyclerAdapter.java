/*
 * Copyright (c) 2015 Sean Liu.
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

package com.github.baoti.pioneer.ui.common.dataset.dataset;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.baoti.pioneer.app.widget.ResourceLoadingIndicator;
import com.github.baoti.pioneer.ui.common.holder.LoadingViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by liuyedong on 2015/1/2.
 */
public abstract class DataSetRecyclerAdapter<E> extends RecyclerView.Adapter {

    protected final Context context;
    protected final LayoutInflater inflater;
    protected final List<E> items = new ArrayList<>();

    private ResourceLoadingIndicator loadingIndicator;

    public DataSetRecyclerAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void setItems(Collection<E> resources) {
        this.items.clear();
        this.items.addAll(resources);
        notifyDataSetChanged();
    }

    public E getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(RecyclerView.ViewHolder holder, int position);

    public void setLoadingIndicator(ResourceLoadingIndicator loadingIndicator) {
        this.loadingIndicator = loadingIndicator;
    }

    protected RecyclerView.ViewHolder createLoadingViewHolder(ViewGroup parent) {
        if (loadingIndicator != null) {
            return new LoadingViewHolder(loadingIndicator);
        }
        return LoadingViewHolder.create(inflater, parent);
    }
}
