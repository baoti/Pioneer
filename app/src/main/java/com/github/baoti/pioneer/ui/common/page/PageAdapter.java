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
    protected final PagePresenter<E> presenter;
    protected final List<E> items = new ArrayList<>();

    public PageAdapter(LayoutInflater inflater, PagePresenter<E> presenter) {
        this.inflater = inflater;
        this.presenter = presenter;
    }

    public void changeItems(Collection<E> resources) {
        items.clear();
        items.addAll(resources);
        notifyDataSetChanged();
    }

    protected RecyclerView.ViewHolder createLoadingViewHolder(ViewGroup parent) {
        return LoadingViewHolder.create(inflater, parent);
    }

    protected void bindLoadingViewHolder(LoadingViewHolder viewHolder) {
        if (presenter.isLoadingNextPage()) {
            viewHolder.progressBar.setVisibility(View.VISIBLE);
            viewHolder.textView.setVisibility(View.VISIBLE);
            viewHolder.textView.setText("Loading");
        } else if (!presenter.hasNextPage()) {
            viewHolder.progressBar.setVisibility(View.GONE);
            viewHolder.textView.setVisibility(View.VISIBLE);
            viewHolder.textView.setText("No more");
        } else if (presenter.isFailedToLoadNextPage()) {
            viewHolder.progressBar.setVisibility(View.GONE);
            viewHolder.textView.setVisibility(View.VISIBLE);
            viewHolder.textView.setText("Failed to load");
        } else {
            viewHolder.progressBar.setVisibility(View.INVISIBLE);
            viewHolder.textView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return presenter == null ? items.size() : items.size() + 1;
    }
}
