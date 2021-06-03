package com.mingz.desktopnotes.receiver;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mingz.desktopnotes.R;
import com.mingz.desktopnotes.utils.MyLog;

public class UpdateNotesReceiver extends BroadcastReceiver {
    private final MyLog myLog = new MyLog("UpdateReceiveMyTAG");
    public static final String ACTION_INPUT = "ACTION_INPUT_NOTES";

    @Override
    public void onReceive(Context context, Intent intent) {
        myLog.d("广播接收");
        if (ACTION_INPUT.equals(intent.getAction())) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId >= 0) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.notesList);
            }
        }
    }
}