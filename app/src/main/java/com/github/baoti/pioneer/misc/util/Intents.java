package com.github.baoti.pioneer.misc.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * Created by liuyedong on 15-1-6.
 */
public class Intents {

    public static boolean hasResolvedActivity(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
//        List<ResolveInfo> resolveInfo =
//                packageManager.queryIntentActivities(intent,
//                        PackageManager.MATCH_DEFAULT_ONLY);
//        return resolveInfo.size() > 0;
        return intent.resolveActivity(packageManager) != null;
    }

}
