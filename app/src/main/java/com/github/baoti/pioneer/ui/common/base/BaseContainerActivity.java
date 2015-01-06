package com.github.baoti.pioneer.ui.common.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by liuyedong on 14-12-18.
 */
public abstract class BaseContainerActivity extends FragmentActivity {
    public static final int TRANSITION_INIT_FRAGMENT = FragmentTransaction.TRANSIT_NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Fragment fragment = contentFragment();
            if (fragment.getArguments() == null) {
                fragment.setArguments(getIntent().getExtras());
            }
            // SHOULD BE TRANSIT_NONE
            getSupportFragmentManager().beginTransaction()
                    .setTransition(TRANSITION_INIT_FRAGMENT)
                    .add(contentContainerId(), fragment)
                    .commit();
        }
    }

    protected int contentContainerId() {
        return android.R.id.content;
    }

    protected abstract Fragment contentFragment();
}
