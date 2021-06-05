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
import com.mingz.desktopnotes.utils.MyLog;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.StandardCharsets;

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
        widgetAttr.toJSON(context, appWidgetId);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, WidgetAttr.getInstance(context, appWidgetId));
        }
    }

    public static class WidgetAttr implements Serializable {
        // 公开属性值
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

        // 内部属性
        private int backgroundResId;// 背景资源id
        private int backgroundColor;// 背景颜色
        private String notes;// 便签内容
        private int textColor;// 文本颜色
        private int textSize;// 文本字体大小（sp）

        // 其它辅助属性
        private static final MyLog myLog = new MyLog("WidgetAttrMyTAG");
        private static final long serialVersionUID = 202106031218L;
        private static final String JSON_BG_RES_ID = "backgroundResId";
        private static final String JSON_BG_COLOR = "backgroundColor";
        private static final String JSON_NOTES = "notes";
        private static final String JSON_TEXT_COLOR = "textColor";
        private static final String JSON_TEXT_SIZE = "textSize";

        // 使用默认值构造
        private WidgetAttr() {
            backgroundResId = R.drawable.bg_widget_half_alpha;
            this.backgroundColor = Color.argb(255, 255, 255, 255);
            this.notes = "桌面便签";
            this.textColor = Color.GRAY;
            this.textSize = SIZE_MIDDLE;
        }

        // 从读取的文件内容构造
        private WidgetAttr(int backgroundResId, int backgroundColor, String notes,
                           int textColor, int textSize) {
            this.backgroundResId = backgroundResId;
            this.backgroundColor = backgroundColor;
            this.notes = notes;
            this.textColor = textColor;
            this.textSize = textSize;
        }

        /**
         * 获取一个实例.<br>
         * 该实例从已保存的文件构造，若无保存的文件
         * 或文件解析异常则使用默认值构造.
         *
         * @param context 上下文
         * @param appWidgetId 视窗id
         * @return 构造的实例
         */
        public static WidgetAttr getInstance(Context context, int appWidgetId) {
            File cache = new File(context.getFilesDir(), "widget_" + appWidgetId);
            if (cache.exists()) {
                try (FileInputStream fis = new FileInputStream(cache)) {
                    byte[] data = new byte[1024];// 1KB
                    int len;// 读取长度
                    StringBuilder input = new StringBuilder();// 读取内容
                    while ((len = fis.read(data)) != -1) {
                        input.append(new String(data, 0, len, StandardCharsets.UTF_8));
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(input.toString());
                        return new WidgetAttr(
                                Integer.parseInt(jsonObject.getString(JSON_BG_RES_ID)),
                                Integer.parseInt(jsonObject.getString(JSON_BG_COLOR)),
                                jsonObject.getString(JSON_NOTES),
                                Integer.parseInt(jsonObject.getString(JSON_TEXT_COLOR)),
                                Integer.parseInt(jsonObject.getString(JSON_TEXT_SIZE))
                        );
                    } catch (Exception e) {
                        myLog.w("错误的文件内容（" + cache.getName() + "): " + input.toString());
                        if (!cache.delete()) {
                            myLog.w("文件删除失败");
                        }
                    }
                } catch (IOException e) {
                    return new WidgetAttr();
                }
            }
            return new WidgetAttr();
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

        /**
         * 将所属对象以json形式存储于文件中.
         *
         * @param context 上下文
         * @param appWidgetId 视窗id
         */
        public void toJSON(Context context, int appWidgetId) {
            File cache = new File(context.getFilesDir(), "widget_" + appWidgetId);
            try {
                if (cache.exists() || cache.createNewFile()) {
                    try (FileOutputStream fos = new FileOutputStream(cache)) {
                        String output = "{\"" + JSON_BG_RES_ID + "\":\"" + backgroundResId +
                                "\",\"" + JSON_BG_COLOR + "\":\"" + backgroundColor +
                                "\",\"" + JSON_NOTES + "\":\"" + notes +
                                "\",\"" + JSON_TEXT_COLOR + "\":\"" + textColor +
                                "\",\"" + JSON_TEXT_SIZE + "\":\"" + textSize +
                                "\"}";// 存储内容
                        fos.write(output.getBytes(StandardCharsets.UTF_8));
                    }
                }
            } catch (IOException e) {
                myLog.w("将WidgetAttr写入文件异常", e);
            }
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