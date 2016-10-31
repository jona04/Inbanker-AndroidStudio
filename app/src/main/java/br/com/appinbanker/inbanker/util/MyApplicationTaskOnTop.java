package br.com.appinbanker.inbanker.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Jonatas on 31/10/2016.
 */

public class MyApplicationTaskOnTop {
    public static boolean isMyApplicationTaskOnTop(Context context) {
        String packageName = context.getPackageName();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
        if(recentTasks != null && recentTasks.size() > 0) {
            ActivityManager.RunningTaskInfo t = recentTasks.get(0);
            String pack = t.baseActivity.getPackageName();
            if(pack.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
