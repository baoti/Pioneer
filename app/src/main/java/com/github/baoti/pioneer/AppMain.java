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

package com.github.baoti.pioneer;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import dagger.ObjectGraph;
import timber.log.Timber;

/**
 * Created by liuyedong on 14-12-18.
 */
public class AppMain extends Application {

    private static AppMain app;

    public static AppMain app() {
        return app;
    }

    private ObjectGraph graph;

    @Override
    public void onCreate() {
        app = this;

        super.onCreate();


        graph = ObjectGraph.create(getModules().toArray());

        globalGraph().injectStatics();

        if (BuildConfig.DEBUG) {
            globalGraph().validate(); // validate dagger's object graph
            Timber.plant(new Timber.DebugTree());
        }
    }

    public void inject(Object object) {
        graph.inject(object);
    }

    public ObjectGraph getScopedGraph() {
        return graph;
    }

    public static ObjectGraph globalGraph() {
        if (app == null) {
            throw new IllegalStateException("app is null");
        }
        return app.getScopedGraph();
    }

    protected List<Object> getModules() {
        List<Object> modules = new ArrayList<>();
        modules.add(new AppMainModule(this));
        return modules;
    }
}
