package com.github.baoti.pioneer;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by liuyedong on 14-12-18.
 */
public class BusProvider {

    public static final Bus UI_BUS = new Bus(ThreadEnforcer.MAIN);

    public static final Bus APP_BUS = new Bus(ThreadEnforcer.ANY);
}
