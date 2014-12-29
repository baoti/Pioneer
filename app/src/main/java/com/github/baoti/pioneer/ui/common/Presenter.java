package com.github.baoti.pioneer.ui.common;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.baoti.pioneer.BusProvider;
import com.squareup.otto.Bus;

import timber.log.Timber;

/**
 * Base Presenter.
 *  生命周期:
 *    1. 创建
 *      new - takeView - load(without state) - resume
 *
 *    2. 背光灭/用户不可见, 针对不同系统有两种可能, 不同之处在于先 save 还是 pause
 *      (1) save - pause - 新界面[创建]resume
 *      (2) pause - 新界面[创建]resume - save
 *
 *    3. 背光亮/用户可见
 *      resume
 *
 *    4. 屏幕旋转
 *      (1) Non retained:
 *          OLD> save - pause - dropView - close
 *          NEW> new - takeView - load(with state, reusing is false) - resume
 *
 *      (2) Retained by activity or fragment
 *          save - pause - dropView - takeView - load(with state, reusing is true) - resume
 *
 *    5. 退出
 *      pause - dropView - close
 *
 * Created by liuyedong on 14-12-24.
 */
public abstract class Presenter<V extends IView> {
    private static boolean LOG_LIFECYCLE = false;

    private V view;

    /** Load has been called for the current {@link #view}. */
    private boolean loaded;

    private boolean resumed;

    private boolean reusing;

    protected final Bus uiBus;
    private final boolean subscribe;

    public Presenter() {
        this(false);
    }

    /**
     * @param subscribe 是否在 暂停/恢复 时自动注册/取消到 App EventBus
     */
    public Presenter(boolean subscribe) {
        super();
        this.subscribe = subscribe;
        uiBus = BusProvider.UI_BUS;
        if (LOG_LIFECYCLE) Timber.v("[new]  " + this);
    }

    /**
     * 获得绑定的 view
     * @return view
     */
    protected final V getView() {
        return view;
    }

    /**
     * 是否拥有绑定的 view
     * @return 是否拥有
     */
    protected final boolean hasView() {
        return getView() != null;
    }

    final void takeView(V view) {
        if (LOG_LIFECYCLE) Timber.v("[takeView] " + this);
        if (view == null) throw new NullPointerException("new view must not be null");

        if (this.view != view) {
            if (this.view != null) dropView(this.view);

            this.view = view;
            onTakeView(view);
        }
    }

    /**
     * 获得 view, view 变得可用
     * @param view 关联的 view
     */
    @SuppressWarnings("UnusedParameters")
    protected void onTakeView(V view) {
    }

    final void load(@Nullable Bundle savedInstanceState) {
        if (LOG_LIFECYCLE) Timber.v("[load] " + this);
        if (hasView() && !loaded) {
            loaded = true;
            onLoad(savedInstanceState, reusing);
            reusing = true;
        }
    }

    protected final boolean isLoaded() {
        return loaded;
    }

    /**
     * 加载, 在 onTakeView 之后被调用
     *
     * @param savedInstanceState 保存的状态
     * @param reusing 当前 Presenter 是否处于重用状态
     */
    protected void onLoad(@Nullable Bundle savedInstanceState, boolean reusing) {
    }

    final void resume() {
        if (LOG_LIFECYCLE) Timber.v("[resume] " + this);
        resumed = true;
        if (subscribe) {
            uiBus.register(this);
        }
        onResume();
    }

    /**
     * 恢复用户可见状态
     */
    protected void onResume() {
    }

    /**
     * 是否处理用户可见状态
     * @return
     */
    @SuppressWarnings("UnusedDeclaration")
    protected final boolean isResumed() {
        return resumed;
    }

    final void pause() {
        if (LOG_LIFECYCLE) Timber.v("[pause] " + this);
        resumed = false;
        if (subscribe) {
            uiBus.unregister(this);
        }
        onPause();
    }

    /**
     * 离开用户可见状态
     */
    protected void onPause() {
    }

    final void save(@NonNull Bundle outState) {
        if (LOG_LIFECYCLE) Timber.v("[save] " + this);
        onSave(outState);
    }

    /**
     * 保存状态
     * @param outState 用于储存状态
     */
    @SuppressWarnings("UnusedParameters")
    protected void onSave(@NonNull Bundle outState) {
    }

    final void dropView(V view) {
        if (LOG_LIFECYCLE) Timber.v("[dropView] " + this);
        if (view == null) throw new NullPointerException("dropped view must not be null");
        if (view == this.view) {
            onDropView(view);
            loaded = false;
            this.view = null;
        }
    }

    /**
     * 丢失 view, view 不再可用
     * @param view 当前 view
     */
    @SuppressWarnings("UnusedParameters")
    protected void onDropView(V view) {
    }

    final void close() {
        if (LOG_LIFECYCLE) Timber.v("[close] " + this);
        onClose();
    }

    /**
     * 退出
     */
    protected void onClose() {
    }
}
