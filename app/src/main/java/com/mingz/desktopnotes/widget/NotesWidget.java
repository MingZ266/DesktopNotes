package com.mingz.desktopnotes.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.RemoteViews;

import androidx.annotation.IntDef;

import com.mingz.desktopnotes.R;
import com.mingz.desktopnotes.activity.EditTextActivity;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * App Widget 功能的实现.
 */
public class NotesWidget extends AppWidgetProvider {
    //private final MyLog myLog = new MyLog("NoteWidgetMyTAG");

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId, WidgetAttr widgetAttr) {
        // 构造 RemoteViews 对象
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_notes);
        views.setImageViewResource(R.id.background, widgetAttr.backgroundResId);
        views.setInt(R.id.background, "setColorFilter", widgetAttr.backgroundColor);
        views.setTextViewText(R.id.notes, widgetAttr.notes);
        views.setTextColor(R.id.notes, widgetAttr.textColor);
        views.setTextViewTextSize(R.id.notes, TypedValue.COMPLEX_UNIT_SP, widgetAttr.textSize);
        // 添加点击事件
        Intent openEdit = new Intent(context, EditTextActivity.class);
        openEdit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        openEdit.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        openEdit.putExtra(String.valueOf(appWidgetId), widgetAttr);
        views.setOnClickPendingIntent(R.id.notes, PendingIntent.getActivity(context,
                appWidgetId, openEdit, PendingIntent.FLAG_UPDATE_CURRENT));
        // 指示小部件管理器更新小部件
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        WidgetAttr widgetAttr = new WidgetAttr();
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, widgetAttr);
        }
    }

    public static class WidgetAttr implements Serializable {
        //private final MyLog myLog = new MyLog("WidgetAttrMyTAG");
        /**
         * 透明.
         */
        public static final int ALPHA_ZERO = 0x00FFFFFF;

        /**
         * 半透明.
         */
        public static final int ALPHA_HALF = 0x7FFFFFFF;

        /**
         * 不透明.
         */
        public static final int ALPHA_MAX = 0xFFFFFFFF;

        /**
         * 小号字体（15sp）.
         */
        public static final int SIZE_SMALL = 15;

        /**
         * 中号字体（25sp）.
         */
        public static final int SIZE_MIDDLE = 25;

        /**
         * 大号字体（35sp）.
         */
        public static final int SIZE_LARGE = 35;

        /**
         * 特大号字体（45sp）.
         */
        public static final int SIZE_MAX = 45;

        private static final long serialVersionUID = 202106031218L;
        private int backgroundResId;// 背景资源id
        private int backgroundColor;// 背景颜色
        private String notes;// 便签内容
        private int textColor;// 文本颜色
        private int textSize;// 文本字体大小（sp）

        public WidgetAttr() {
            backgroundResId = R.drawable.bg_widget_half_alpha;
            this.backgroundColor = Color.argb(255, 255, 255, 255);
            this.notes = "桌面便签";
            this.textColor = Color.GRAY;
            this.textSize = SIZE_MIDDLE;
        }

        /**
         * 更新属性值.
         */
        public void updateAttr(@AlphaLevel int alphaLevel, int backgroundColor, String notes,
                               int textColor, @TextSize int textSize) {
            backgroundColor |= 0xFF000000;
            switch (alphaLevel) {
                case ALPHA_ZERO:
                    backgroundResId = R.drawable.bg_widget_zero_alpha;
                    break;
                case ALPHA_MAX:
                    backgroundResId = R.drawable.bg_widget_max_alpha;
                    break;
                case ALPHA_HALF:
                default:
                    backgroundResId = R.drawable.bg_widget_half_alpha;
            }
            this.backgroundColor = backgroundColor;
            this.notes = notes;
            this.textColor = textColor;
            this.textSize = textSize;
        }

        /**
         * 判断是否有可更新项.
         *
         * @return 若存在可更新项则返回true
         */
        public boolean needUpdate(@AlphaLevel int alphaLevel, int backgroundColor, String notes,
                                  int textColor, @TextSize int textSize) {
            backgroundColor |= 0xFF000000;
            int resId;
            switch (alphaLevel) {
                case ALPHA_ZERO:
                    resId = R.drawable.bg_widget_zero_alpha;
                    break;
                case ALPHA_MAX:
                    resId = R.drawable.bg_widget_max_alpha;
                    break;
                case ALPHA_HALF:
                default:
                    resId = R.drawable.bg_widget_half_alpha;
            }
            return resId != backgroundResId ||
                    this.backgroundColor != backgroundColor ||
                    (!this.notes.equals(notes)) ||
                    this.textColor != textColor ||
                    this.textSize != textSize;
        }

        public int getAlphaLevel() {
            if (backgroundResId == R.drawable.bg_widget_zero_alpha) {
                return ALPHA_ZERO;
            } else if (backgroundResId == R.drawable.bg_widget_max_alpha) {
                return ALPHA_MAX;
            } else {
                return ALPHA_HALF;
            }
        }

        public int getBackgroundColor() {
            return backgroundColor;
        }

        public String getNotes() {
            return notes;
        }

        public int getTextColor() {
            return textColor;
        }

        public int getTextSize() {
            return textSize;
        }

        @IntDef({ALPHA_ZERO, ALPHA_HALF, ALPHA_MAX})
        @Retention(RetentionPolicy.SOURCE)
        public @interface AlphaLevel {}

        @IntDef({SIZE_SMALL, SIZE_MIDDLE, SIZE_LARGE, SIZE_MAX})
        @Retention(RetentionPolicy.SOURCE)
        public @interface TextSize {}
    }
}