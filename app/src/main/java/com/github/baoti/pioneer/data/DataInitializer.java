package com.github.baoti.pioneer.data;

import com.github.baoti.pioneer.AppMain;
import com.github.baoti.pioneer.event.app.AppInitializeReportEvent;
import com.github.baoti.pioneer.event.app.AppInitializeRequestEvent;
import com.github.baoti.pioneer.misc.util.IoUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import timber.log.Timber;

/**
 * Created by liuyedong on 14-12-25.
 */
public class DataInitializer {

    private boolean initialized;

    private final Bus appBus;

    @Inject
    @Named("cacheDir")
    File cacheDir;

    public DataInitializer(Bus appBus) {
        this.appBus = appBus;
    }

    private void initialize() {
        if (!initialized) {
            initialized = true;
            AppMain.globalGraph().inject(this);
        }
    }

    @Subscribe
    public void onAppInitializeRequest(AppInitializeRequestEvent event) {
        // 构造的时候无法执行注入, 推迟到需要的时候再注入
        initialize();

        File dbInCache = new File(cacheDir, "db/store.db");
        if (cacheDir != null && !dbInCache.exists()) {
            appBus.post(new AppInitializeReportEvent("正在缓存数据库"));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return;
            }
            if (IoUtils.ensureDirsExist(dbInCache.getParentFile())) {
                IoUtils.copyAssets(Assets.STORE_DB, dbInCache);
            } else {
                Timber.w("Can't ensure cache dir");
            }
        }
    }
}
