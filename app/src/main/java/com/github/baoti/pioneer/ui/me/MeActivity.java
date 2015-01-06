package com.github.baoti.pioneer.ui.me;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.github.baoti.pioneer.ui.common.base.BaseContainerActivity;

/**
 * Created by liuyedong on 14-12-26.
 */
public class MeActivity extends BaseContainerActivity {
    public static Intent actionLaunch(Context context) {
        return new Intent(context, MeActivity.class);
    }

    @Override
    protected Fragment contentFragment() {
        return MeFragment.newInstance();
    }
}
