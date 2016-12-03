/*
 * Copyright (c) 2014-2016 Sean Liu.
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

package com.github.baoti.pioneer.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.ui.main.MainActivity;
import com.github.baoti.pioneer.ui.splash.SplashActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by liuyedong on 14-12-29.
 */
public class Navigator {
    /**
     * 启动主界面,可在Splash界面或出现异常时使用.
     * @param context
     */
    public static void launchMain(Context context) {
//        context.getPackageManager().resolveActivity(
//                new Intent(Intent.ACTION_MAIN).setPackage(context.getPackageName())
//                        .addCategory(Intent.CATEGORY_DEFAULT),
//                0);
        boolean inActivityContext = context instanceof Activity;
        Intent intent = new Intent(context, MainActivity.class);
        if (!inActivityContext) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (context instanceof MainActivity) {
            ((MainActivity) context).close();
        }
        context.startActivity(intent);
        if (context instanceof Activity) {
            if (context instanceof SplashActivity)
                ((Activity) context).overridePendingTransition(android.R.anim.fade_in, 0);
            ((Activity) context).overridePendingTransition(0, 0);
        }
        if (context instanceof Activity && !(context instanceof MainActivity)) {
            ActivityCompat.finishAffinity((Activity) context);
            ((Activity) context).overridePendingTransition(0, android.R.anim.fade_out);
        }
    }

    /**
     * 回到主界面,可在Splash外的其他界面使用
     * @param activity
     */
    public static void upToMain(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
//        NavUtils.navigateUpTo(activity, intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).close();
        }
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
        if (!(activity instanceof MainActivity)) {
            ActivityCompat.finishAffinity(activity);
            activity.overridePendingTransition(0, android.R.anim.fade_out);
        }
    }

    /**
     * 关闭应用程序
     * @param context
     */
    public static void closeApp(Context context) {
        if (context instanceof MainActivity) {
            ((MainActivity) context).close();
        }
    }

    public static boolean moveToBackIfRoot(Activity activity) {
        return activity.moveTaskToBack(false);
    }

    public static boolean isTaskRoot(Activity activity) {
        return activity.isTaskRoot();
    }

    public static boolean isTaskRoot(Fragment fragment) {
        return isTaskRoot(fragment.getActivity()) && !hasBackStackEntry(fragment.getFragmentManager());
    }

    public static boolean hasBackStackEntry(FragmentManager fragmentManager) {
        int count = fragmentManager.getBackStackEntryCount();
        if (count > 0) {
            return true;
        }
        if (isFragmentInBackStack != null) {
            List<Fragment> fragments = fragmentManager.getFragments();
            if (fragments != null) {
                for (Fragment frag : fragments) {
                    if (frag != null && isInBackStack(frag)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static Method isFragmentInBackStack;

    static {
        try {
            isFragmentInBackStack = Fragment.class.getDeclaredMethod("isInBackStack");
            isFragmentInBackStack.setAccessible(true);
        } catch (NoSuchMethodException ignored) {
        }
    }

    private static boolean isInBackStack(Fragment fragment) {
        try {
            Object result = isFragmentInBackStack.invoke(fragment);
            return (Boolean) result;
        } catch (IllegalAccessException e) {
            return false;
        } catch (InvocationTargetException e) {
            return false;
        }
    }

    public static boolean canNavigateBack(Activity activity) {
        if (!isTaskRoot(activity)) {
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (activity instanceof FragmentActivity) {
            return hasBackStackEntry(
                    ((FragmentActivity) activity).getSupportFragmentManager());
        }
        return false;
    }

    public static boolean canNavigateBack(Fragment fragment) {
        return !isTaskRoot(fragment);
    }

    /**
     * 设置 Toolbar 上的 导航图标与事件
     * @return 返回导航图标是否可见
     */
    public static boolean setupToolbarNavigation(final Activity activity, Toolbar toolbar) {
        if (canNavigateBack(activity)) {
            toolbar.setNavigationIcon(R.drawable.ic_ab_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onBackPressed();
                }
            });
            return true;
        } else {
            toolbar.setNavigationIcon(null);
            return false;
        }
    }

    /**
     * 设置 Toolbar 上的 导航图标与事件
     * @return 返回导航图标是否可见
     */
    public static boolean setupToolbarNavigation(final Fragment fragment, Toolbar toolbar) {
        if (canNavigateBack(fragment)) {
            toolbar.setNavigationIcon(R.drawable.ic_ab_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment.getActivity().onBackPressed();
                }
            });
            return true;
        } else {
            toolbar.setNavigationIcon(null);
            return false;
        }
    }
}
