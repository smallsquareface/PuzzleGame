package com.qinkai.puzzlegame.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.qinkai.puzzlegame.activity.PuzzleGame;

public class ScreenUtil {
    /**
     * 获取屏幕相关参数
     *
     * @param context context
     * @return DisplayMetrics 屏幕宽高
     */
    public static float getDeviceDensity(Context context) {
        float density = 0;
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            Display display;
            display = wm.getDefaultDisplay();
            display.getMetrics(metrics);
            density = metrics.density;
        }
        return density;
    }

    public static DisplayMetrics getScreenSize(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager =
                (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            display.getMetrics(metrics);
        }
        return metrics;
    }
}
