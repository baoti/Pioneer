package com.github.baoti.pioneer.app.task;

import com.github.baoti.pioneer.biz.exception.BizException;
import com.github.baoti.pioneer.biz.interactor.DeferredInteractor;

/**
 * 后台交互任务
 *
 * Created by liuyedong on 14-12-25.
 */
public class InteractorTask<Progress, Result> extends SafeAsyncTask<Void, Progress, Result> {
    protected final DeferredInteractor<Result> deferredInteractor;

    public InteractorTask(DeferredInteractor<Result> interactor) {
        super();
        if (interactor == null) {
            throw new NullPointerException("interactor is null");
        }
        this.deferredInteractor = interactor;
    }

    public InteractorTask(DeferredInteractor<Result> interactor,
                          boolean subscribe) {
        super(subscribe);
        this.deferredInteractor = interactor;
    }

    @Override
    protected Result doTask(Void... params) throws BizException {
        return deferredInteractor.interact();
    }
}
