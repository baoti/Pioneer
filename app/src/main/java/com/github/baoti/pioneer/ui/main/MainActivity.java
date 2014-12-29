package com.github.baoti.pioneer.ui.main;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.github.baoti.pioneer.ui.common.base.BaseContainerActivity;

/**
 * Created by liuyedong on 14-12-18.
 */
public class MainActivity extends BaseContainerActivity {

    private boolean hookFinishToMoveBack = true;

    protected Fragment contentFragment() {
        return MainFragment.newInstance();
    }

    public void close() {
        hookFinishToMoveBack = false;
        ActivityCompat.finishAffinity(this);
        overridePendingTransition(0, android.R.anim.fade_out);
    }

    @Override
    public void finish() {
        if (hookFinishToMoveBack) {
            // no finish, just move to back
            moveTaskToBack(true);
        } else {
            super.finish();
        }
        overridePendingTransition(0, android.R.anim.fade_out);
    }
}
