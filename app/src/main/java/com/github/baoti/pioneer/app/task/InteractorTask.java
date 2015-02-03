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
        if (interactor == null) {
            throw new NullPointerException("interactor is null");
        }
        this.deferredInteractor = interactor;
    }

    @Override
    protected Result doTask(Void... params) throws BizException {
        return deferredInteractor.interact();
    }
}
