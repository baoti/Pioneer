/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.baoti.pioneer.app.widget;

import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.ListView.FixedViewInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import timber.log.Timber;

/**
 * Utility adapter that supports adding headers and footers
 *
 * @param <E>
 */
public class HeaderFooterListAdapter<E extends BaseAdapter> extends
        HeaderViewListAdapter {

    private final ListView list;

    private final ArrayList<FixedViewInfo> headers;

    private final ArrayList<FixedViewInfo> footers;

    private final E wrapped;

    /**
     * Create header footer adapter
     *
     * @param view
     * @param adapter
     */
    public HeaderFooterListAdapter(ListView view, E adapter) {
        this(new ArrayList<FixedViewInfo>(), new ArrayList<FixedViewInfo>(),
                view, adapter);
    }

    private HeaderFooterListAdapter(ArrayList<FixedViewInfo> headerViewInfos,
                                    ArrayList<FixedViewInfo> footerViewInfos, ListView view, E adapter) {
        super(headerViewInfos, footerViewInfos, adapter);

        headers = headerViewInfos;
        footers = footerViewInfos;
        list = view;
        wrapped = adapter;
    }

    /**
     * Add non-selectable header view with no data
     *
     * @param view
     * @return this adapter
     * @see #addHeader(android.view.View, Object, boolean)
     */
    public HeaderFooterListAdapter<E> addHeader(View view) {
        return addHeader(view, null, false);
    }

    /**
     * Add header
     *
     * @param view
     * @param data
     * @param isSelectable
     * @return this adapter
     */
    public HeaderFooterListAdapter<E> addHeader(View view, Object data,
                                                boolean isSelectable) {
        FixedViewInfo info = list.new FixedViewInfo();
        info.view = view;
        info.data = data;
        info.isSelectable = isSelectable;

        headers.add(info);
        wrapped.notifyDataSetChanged();
        return this;
    }

    /**
     * Add non-selectable footer view with no data
     *
     * @param view
     * @return this adapter
     * @see #addFooter(android.view.View, Object, boolean)
     */
    public HeaderFooterListAdapter<E> addFooter(View view) {
        return addFooter(view, null, false);
    }

    /**
     * Add footer
     *
     * @param view
     * @param data
     * @param isSelectable
     * @return this adapter
     */
    public HeaderFooterListAdapter<E> addFooter(View view, Object data,
                                                boolean isSelectable) {
        FixedViewInfo info = list.new FixedViewInfo();
        info.view = view;
        info.data = data;
        info.isSelectable = isSelectable;

        footers.add(info);
        wrapped.notifyDataSetChanged();
        return this;
    }

    @Override
    public boolean removeHeader(View v) {
        boolean removed = super.removeHeader(v);
        if (removed)
            wrapped.notifyDataSetChanged();
        return removed;
    }

    @Override
    public boolean removeFooter(View v) {
        boolean removed = super.removeFooter(v);
        if (removed)
            wrapped.notifyDataSetChanged();
        return removed;
    }

    /**
     * Remove all headers
     *
     * @return true if headers were removed, false otherwise
     */
    public boolean clearHeaders(boolean dispatchDetach) {
        boolean removed = false;
        if (!headers.isEmpty()) {
            FixedViewInfo[] infos = headers.toArray(new FixedViewInfo[headers
                    .size()]);
            for (FixedViewInfo info : infos) {
                removed = super.removeHeader(info.view) || removed;
                if (dispatchDetach && ViewUtils.isAttachedToWindow(info.view)) {
                    ViewUtils.callDispatchDetachedFromWindow(info.view);
                }
            }
        }
        if (removed)
            wrapped.notifyDataSetChanged();
        return removed;
    }

    /**
     * Remove all footers
     *
     * @return true if headers were removed, false otherwise
     */
    public boolean clearFooters(boolean dispatchDetach) {
        boolean removed = false;
        if (!footers.isEmpty()) {
            FixedViewInfo[] infos = footers.toArray(new FixedViewInfo[footers
                    .size()]);
            for (FixedViewInfo info : infos) {
                removed = super.removeFooter(info.view) || removed;
                if (dispatchDetach && ViewUtils.isAttachedToWindow(info.view)) {
                    ViewUtils.callDispatchDetachedFromWindow(info.view);
                }
            }
        }
        if (removed)
            wrapped.notifyDataSetChanged();
        return removed;
    }

    @Override
    public E getWrappedAdapter() {
        return wrapped;
    }

    @Override
    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return position < getCount() && super.isEnabled(position);
    }

    public void clearRecycledStates() {
        /*
        ListView 只清理 mHeaderViewInfos 和 mFooterViewInfos 中的状态。
        而不法清除此类中的 headers/footers 状态，导致 headers/footers 中的控件 onAttached 不再被调用。
         */
        clearRecycledState(list, headers);
        clearRecycledState(list, footers);
    }

    private static void clearRecycledState(ListView listView, ArrayList<FixedViewInfo> infos) {
        if (infos == null) {
            return;
        }
        if (ListView_clearRecycledState != null) {
            try {
                ListView_clearRecycledState.invoke(listView, infos);
                return;
            } catch (Exception e) {
                Timber.w(e, "Fail to invoke clearRecycledState");
            }
        }

        final int count = infos.size();

        for (int i = 0; i < count; i++) {
            final View child = infos.get(i).view;
            clearRecycledState((AbsListView.LayoutParams) child.getLayoutParams());
        }
    }

    private static void clearRecycledState(AbsListView.LayoutParams params) {
        if (params == null) {
            return;
        }
        // params.recycledHeaderFooter = false;
        if (AbsListView_LayoutParams_recycledHeaderFooter != null) {
            try {
                AbsListView_LayoutParams_recycledHeaderFooter.setBoolean(params, false);
            } catch (Exception e) {
                Timber.w(e, "Fail to set recycledHeaderFooter");
            }
        }
    }

    private static Method ListView_clearRecycledState;

    static {
        try {
            ListView_clearRecycledState = ListView.class.getDeclaredMethod("clearRecycledState");
            ListView_clearRecycledState.setAccessible(true);
        } catch (NoSuchMethodException ignored) {
        }
    }

    private static Field AbsListView_LayoutParams_recycledHeaderFooter;

    static {
        try {
            AbsListView_LayoutParams_recycledHeaderFooter =
                    AbsListView.LayoutParams.class.getDeclaredField("recycledHeaderFooter");
            AbsListView_LayoutParams_recycledHeaderFooter.setAccessible(true);
        } catch (NoSuchFieldException ignored) {
        }
    }
}
