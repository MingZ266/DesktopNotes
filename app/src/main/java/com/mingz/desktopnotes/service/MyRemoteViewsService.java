package com.mingz.desktopnotes.service;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.mingz.desktopnotes.R;
import com.mingz.desktopnotes.utils.MyLog;
import com.mingz.desktopnotes.widget.NotesWidget;

public class MyRemoteViewsService extends RemoteViewsService {
    private static final MyLog myLog = new MyLog("RemoteServiceMyTAG");

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        MyRemoteViewsFactory factory;
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        if (appWidgetId >= 0) {
            myLog.d("服务 ==> appWidgetId: " + appWidgetId);
            Bundle data = intent.getExtras();
            if (data != null) {
                myLog.d("data不为空");
                factory = (MyRemoteViewsFactory) data.getBinder(String.valueOf(appWidgetId));
                if (factory != null) {
                    myLog.d("获取 ==> factory: " + factory);
                    return factory;
                }
            }
        }
        factory = new MyRemoteViewsFactory(getApplicationContext());
        factory.setNotes("桌面便签");
        myLog.d("生成 ==> factory: " + factory);
        return factory;
    }

    public static class MyRemoteViewsFactory extends Binder implements RemoteViewsFactory {
        private final Context context;
        private String text;

        public MyRemoteViewsFactory(Context context) {
            this.context = context;
        }

        public void setNotes(String text) {
            this.text = text;
        }

        public String getNotes() {
            return text;
        }

        @Override
        public void onCreate() {}

        @Override
        public void onDataSetChanged() {}

        @Override
        public void onDestroy() {}

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            //myLog.d("getViewAt");
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.item_list_notes);
            if (text != null) {
                remoteViews.setTextViewText(R.id.notes, text);
            }
            remoteViews.setOnClickFillInIntent(R.id.notes, new Intent());
            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}