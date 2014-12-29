package com.github.baoti.pioneer.event.app;

/**
 * 应用程序初始化事件
 *
 * Created by liuyedong on 14-12-19.
 */
public class AppInitializeReportEvent {
    public final String progress;

    public AppInitializeReportEvent(String progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return progress;
    }
}
