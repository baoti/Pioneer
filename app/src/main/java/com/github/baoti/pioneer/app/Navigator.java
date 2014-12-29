package com.github.baoti.pioneer.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;

import com.github.baoti.pioneer.ui.main.MainActivity;
import com.github.baoti.pioneer.ui.splash.SplashActivity;

/**
 * Created by liuyedong on 14-12-29.
 */
public class Navigator {
    /**
     * 启动主界面,可在Splash界面或出现异常时使用.
     * @param context
     */
    public static void launchMain(Context context) {
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
}
