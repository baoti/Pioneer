package com.github.baoti.pioneer.ui.news;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.baoti.pioneer.entity.News;
import com.github.baoti.pioneer.ui.common.holder.LoadingViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static butterknife.ButterKnife.findById;

/**
 * Created by Administrator on 2015/1/2.
 */
public class NewsListAdapter extends RecyclerView.Adapter {
    private final int TYPE_MERCHANT = 0;
    private final int TYPE_LOAD_MORE = 1;

    private final LayoutInflater inflater;
    private final NewsListPresenter presenter;
    private final List<News> items = new ArrayList<>();

    public NewsListAdapter(LayoutInflater inflater, NewsListPresenter presenter) {
        this.inflater = inflater;
        this.presenter = presenter;
    }

    public void changeItems(Collection<News> resources) {
        items.clear();
        items.addAll(resources);
        notifyDataSetChanged();
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
            return LoadingViewHolder.create(inflater, parent);
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
            LoadingViewHolder viewHolder = (LoadingViewHolder) holder;
            if (presenter.isLoadingNextPage()) {
                viewHolder.progressBar.setVisibility(View.VISIBLE);
                viewHolder.textView.setVisibility(View.VISIBLE);
                viewHolder.textView.setText("Loading");
            } else if (!presenter.hasNextPage()) {
                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.textView.setVisibility(View.VISIBLE);
                viewHolder.textView.setText("No more");
            } else {
                viewHolder.progressBar.setVisibility(View.INVISIBLE);
                viewHolder.textView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return presenter == null ? items.size() : items.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView text1;
        final TextView text2;

        public ViewHolder(View itemView) {
            super(itemView);
            text1 = findById(itemView, android.R.id.text1);
            text2 = findById(itemView, android.R.id.text2);
        }
    }
}
