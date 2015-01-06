package com.github.baoti.pioneer.ui.common.holder;

import android.support.v7.widget.RecyclerView;

/**
 * Created by liuyedong on 15-1-6.
 */
public interface OnViewHolderClickListener<E> {

    void onViewHolderClick(RecyclerView.ViewHolder viewHolder, E item);
}
