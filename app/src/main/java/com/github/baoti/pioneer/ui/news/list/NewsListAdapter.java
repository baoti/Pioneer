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

package com.github.baoti.pioneer.ui.news.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.baoti.pioneer.entity.News;
import com.github.baoti.pioneer.ui.common.holder.LoadingViewHolder;
import com.github.baoti.pioneer.ui.common.holder.OnViewHolderClickListener;
import com.github.baoti.pioneer.ui.common.page.PageAdapter;
import com.github.baoti.pioneer.ui.common.page.PagePresenter;

import static butterknife.ButterKnife.findById;

/**
 * Created by liuyedong on 2015/1/2.
 */
public class NewsListAdapter extends PageAdapter<News> {
    private final int TYPE_MERCHANT = 0;
    private final int TYPE_LOAD_MORE = 1;

    private final OnViewHolderClickListener<News> listener;

    public NewsListAdapter(LayoutInflater inflater, PagePresenter<?, News> presenter,
                           OnViewHolderClickListener<News> listener) {
        super(inflater, presenter);
        this.listener = listener;
    }

    @Override
    public void notifyLoadingChanged() {
        notifyItemChanged(getItemCount() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (presenter != null && position == getItemCount() - 1) {
            return TYPE_LOAD_MORE;
        } else {
            return TYPE_MERCHANT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOAD_MORE) {
            return createLoadingViewHolder(parent);
        }
        return new ViewHolder(inflater.inflate(android.R.layout.simple_list_item_2, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).text1.setText(items.get(position).getTitle());
            ((ViewHolder) holder).text2.setText(items.get(position).getContent());
        }
        if (holder instanceof LoadingViewHolder) {
            bindLoadingViewHolder((LoadingViewHolder) holder);
        }
    }

    @Override
    public int getItemCount() {
        return presenter == null ? items.size() : items.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView text1;
        final TextView text2;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            text1 = findById(itemView, android.R.id.text1);
            text2 = findById(itemView, android.R.id.text2);
        }

        @Override
        public void onClick(View v) {
            listener.onViewHolderClick(this, items.get(getAdapterPosition()));
        }
    }
}
