package com.mingz.desktopnotes.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.mingz.desktopnotes.R;
import com.mingz.desktopnotes.activity.EditTextActivity;
import com.mingz.desktopnotes.service.MyRemoteViewsService;
import com.mingz.desktopnotes.utils.MyLog;

import java.util.Arrays;

/**
 * App Widget 功能的实现.
 */
public class NotesWidget extends AppWidgetProvider {
    //public static final String KEY_NOTES = "Notes";
    //public static final String KEY_DATA_SOURCE = "DataSource";
    private final MyLog myLog = new MyLog("NoteWidgetMyTAG");

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId) {
        myLog.d("updateAppWidget ==> appWidgetId: " + appWidgetId);
        // 构造 RemoteViews 对象
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_notes);
        MyRemoteViewsService.MyRemoteViewsFactory factory = new MyRemoteViewsService.MyRemoteViewsFactory(context.getApplicationContext());
        factory.setNotes("桌面便签");
        Bundle data = new Bundle();
        //data.putBinder(KEY_DATA_SOURCE, factory);
        data.putBinder(String.valueOf(appWidgetId), factory);

        Intent openEdit = new Intent(context, EditTextActivity.class);
        openEdit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        openEdit.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        openEdit.putExtras(data);
        views.setPendingIntentTemplate(R.id.notesList, PendingIntent.getActivity(context,
                appWidgetId, openEdit, PendingIntent.FLAG_UPDATE_CURRENT));

        Intent startService = new Intent(context, MyRemoteViewsService.class);
        startService.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        startService.putExtras(data);
        views.setRemoteAdapter(R.id.notesList, startService);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.notesList);

        // TODO: 点击编辑文本，返回无历史记录，创建时进入编辑

        // 指示小部件管理器更新小部件
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        myLog.d("onUpdate");
        myLog.d("AllWidgetId: " + Arrays.toString(appWidgetIds));
        //updateAppWidget(context, appWidgetManager, appWidgetIds[0]);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //myLog.d("onReceive");
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        //myLog.d("onEnabled");
        // 输入创建第一个小部件时的相关功能
    }

    @Override
    public void onDisabled(Context context) {
        //myLog.d("onDisabled");
        // 输入有关移除最后一个小部件时的相关功能
    }
}