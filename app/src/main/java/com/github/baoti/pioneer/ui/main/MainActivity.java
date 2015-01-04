package com.github.baoti.pioneer.ui.main;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.github.baoti.pioneer.ui.Navigator;
import com.github.baoti.pioneer.ui.common.base.BaseContainerActivity;

import timber.log.Timber;

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
        if (hookFinishToMoveBack && Navigator.moveToBackIfRoot(this)) {
            // no finish, just move to back
            Timber.v("Moved to back");
        } else {
            super.finish();
        }
        overridePendingTransition(0, android.R.anim.fade_out);
    }
}
