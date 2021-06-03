package com.mingz.desktopnotes.activity;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.mingz.desktopnotes.R;
import com.mingz.desktopnotes.widget.NotesWidget;

import top.defaults.colorpicker.ColorPickerView;

public class EditTextActivity extends AppCompatActivity {
    //private final MyLog myLog = new MyLog("EditMyTAG");
    private final AppCompatActivity activity = this;
    private GradientDrawable background;// 预览背景
    private int appWidgetId;
    private NotesWidget.WidgetAttr widgetAttr;
    private AlertDialog exitDialog = null;// 退出弹窗
    // 编辑结果
    private String notesInput;
    private int resultTextColor;
    private int resultBgColor;
    private int sizesIndex = 0;// 字号选中的索引
    private int alphasIndex = 0;// 透明度选中的索引
    // 所有 字号/透明度 的集合
    private final int[] allTextSize = new int[] {
            NotesWidget.WidgetAttr.SIZE_SMALL,
            NotesWidget.WidgetAttr.SIZE_MIDDLE,
            NotesWidget.WidgetAttr.SIZE_LARGE,
            NotesWidget.WidgetAttr.SIZE_MAX
    };
    private final int[] allAlpha = new int[] {
            NotesWidget.WidgetAttr.ALPHA_ZERO,
            NotesWidget.WidgetAttr.ALPHA_HALF,
            NotesWidget.WidgetAttr.ALPHA_MAX
    };

    private TextView inputTip;
    // 设置颜色
    private View selectTextColor;
    private View showTextColor;
    private View selectBgColor;
    private View showBgColor;
    // 设置字体大小
    private View[] sizes;
    private View[] sizesSelect;
    // 设置透明度
    private View[] alphas;
    private View[] alphasSelect;
    // 确定
    private Button ok;
    // 预览
    private TextView preview;

    // 输入框
    private AlertDialog editDialog;
    private EditText editNotes;

    // 颜色选择器
    private AlertDialog colorDialog;
    private ColorPickerView colorPicker;
    private View colorOk;
    private View.OnClickListener selectTextColorListener;
    private View.OnClickListener selectBgColorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);
        // 设置状态栏字体黑色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        Intent intent = getIntent();
        if (intent != null) {
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId >= 0) {
                widgetAttr = (NotesWidget.WidgetAttr) intent.getSerializableExtra(String.valueOf(appWidgetId));
                if (widgetAttr != null) {
                    background = new GradientDrawable();
                    background.setShape(GradientDrawable.RECTANGLE);
                    background.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            8.0f, getResources().getDisplayMetrics()));
                    notesInput = widgetAttr.getNotes();
                    initView();
                    initEditNotes();
                    initColorPicker();
                    myListener();
                    initWindows();
                    return;
                }
            }
        }
        Toast.makeText(activity, "便签创建异常，请重新创建", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void initView() {
        inputTip = findViewById(R.id.inputTip);
        // 设置颜色
        selectTextColor = findViewById(R.id.selectTextColor);
        showTextColor = findViewById(R.id.showTextColor);
        selectBgColor = findViewById(R.id.selectBgColor);
        showBgColor = findViewById(R.id.showBgColor);
        // 设置字体大小
        sizes = new View[] {
                findViewById(R.id.smallSize),
                findViewById(R.id.middleSize),
                findViewById(R.id.largeSize),
                findViewById(R.id.maxSize)
        };
        sizesSelect = new View[] {
                findViewById(R.id.smallSelect),
                findViewById(R.id.middleSelect),
                findViewById(R.id.largeSelect),
                findViewById(R.id.maxSSelect)
        };
        // 设置透明度
        alphas = new View[] {
                findViewById(R.id.zeroAlpha),
                findViewById(R.id.halfAlpha),
                findViewById(R.id.maxAlpha)
        };
        alphasSelect = new View[] {
                findViewById(R.id.zeroSelect),
                findViewById(R.id.halfSelect),
                findViewById(R.id.maxASelect)
        };
        // 确定
        ok = findViewById(R.id.ok);
        // 预览
        preview = findViewById(R.id.preview);
    }

    private void initEditNotes() {
        View editView = View.inflate(activity, R.layout.dialog_edit_notes, null);
        editDialog = new AlertDialog.Builder(activity/*, R.style.CircleCornerAlertDialog*/)
                .setView(editView)
                .create();
        editNotes = editView.findViewById(R.id.inputNotes);
        editDialog.setCanceledOnTouchOutside(false);
        editView.findViewById(R.id.dialogOk).setOnClickListener(v -> {
            editDialog.dismiss();
            notesInput = String.valueOf(editNotes.getText());
            if (TextUtils.isEmpty(notesInput)) {
                notesInput = "桌面便签";
            }
        });
    }

    private void initColorPicker() {
        View colorView = View.inflate(activity, R.layout.dialog_color_picker, null);
        colorDialog = new AlertDialog.Builder(activity, R.style.CircleCornerAlertDialog)
                .setView(colorView)
                .create();
        colorPicker = colorView.findViewById(R.id.colorPicker);
        colorOk = colorView.findViewById(R.id.dialogOk);
        colorDialog.setCanceledOnTouchOutside(false);
        selectTextColorListener = v -> {
            colorDialog.dismiss();
            resultTextColor = colorPicker.getColor();
            showTextColor.setBackgroundColor(resultTextColor);
            preview.setTextColor(resultTextColor);
        };
        selectBgColorListener = v -> {
            colorDialog.dismiss();
            resultBgColor = colorPicker.getColor();
            showBgColor.setBackgroundColor(resultBgColor);
            background.setColor(allAlpha[alphasIndex] & resultBgColor);
            preview.setBackground(background);
        };
    }

    private void initWindows() {
        resultBgColor = widgetAttr.getBackgroundColor();
        resultTextColor = widgetAttr.getTextColor();
        showTextColor.setBackgroundColor(resultTextColor);
        showBgColor.setBackgroundColor(resultBgColor);
        preview.setTextColor(resultTextColor);
        switch (widgetAttr.getTextSize()) {
            case NotesWidget.WidgetAttr.SIZE_SMALL:
                sizesIndex = 0;
                sizesSelect[0].setEnabled(false);
                preview.setTextSize(TypedValue.COMPLEX_UNIT_SP, NotesWidget.WidgetAttr.SIZE_SMALL);
                break;
            case NotesWidget.WidgetAttr.SIZE_LARGE:
                sizesIndex = 2;
                sizesSelect[2].setEnabled(false);
                preview.setTextSize(TypedValue.COMPLEX_UNIT_SP, NotesWidget.WidgetAttr.SIZE_LARGE);
                break;
            case NotesWidget.WidgetAttr.SIZE_MAX:
                sizesIndex = 3;
                sizesSelect[3].setEnabled(false);
                preview.setTextSize(TypedValue.COMPLEX_UNIT_SP, NotesWidget.WidgetAttr.SIZE_MAX);
                break;
            case NotesWidget.WidgetAttr.SIZE_MIDDLE:
            default:
                sizesIndex = 1;
                sizesSelect[1].setEnabled(false);
                preview.setTextSize(TypedValue.COMPLEX_UNIT_SP, NotesWidget.WidgetAttr.SIZE_MIDDLE);
                break;
        }
        int alphaLevel = widgetAttr.getAlphaLevel();
        switch (alphaLevel) {
            case NotesWidget.WidgetAttr.ALPHA_ZERO:
                alphasIndex = 0;
                alphasSelect[0].setEnabled(false);
                break;
            case NotesWidget.WidgetAttr.ALPHA_MAX:
                alphasIndex = 2;
                alphasSelect[2].setEnabled(false);
                break;
            case NotesWidget.WidgetAttr.ALPHA_HALF:
            default:
                alphasIndex = 1;
                alphasSelect[1].setEnabled(false);
                break;
        }
        background.setColor(alphaLevel & resultBgColor);
        preview.setBackground(background);
    }

    private void myListener() {
        inputTip.setOnClickListener(v -> {
            editNotes.setText(notesInput);
            editDialog.show();
        });
        // 字体颜色选择
        selectTextColor.setOnClickListener(v -> {
            colorPicker.setInitialColor(resultTextColor);
            colorDialog.show();
            colorOk.setOnClickListener(selectTextColorListener);
        });
        // 背景颜色选择
        selectBgColor.setOnClickListener(v -> {
            colorPicker.setInitialColor(resultBgColor);
            colorDialog.show();
            colorOk.setOnClickListener(selectBgColorListener);
        });
        // 字号选择
        for (int i = 0; i < sizes.length; i++) {
            int finalI = i;
            sizes[i].setOnClickListener(v -> {
                sizesSelect[sizesIndex].setEnabled(true);
                sizesSelect[finalI].setEnabled(false);
                sizesIndex = finalI;
                preview.setTextSize(TypedValue.COMPLEX_UNIT_SP, allTextSize[sizesIndex]);
            });
        }
        // 透明度选择
        for (int i = 0; i < alphas.length; i++) {
            int finalI = i;
            alphas[i].setOnClickListener(v -> {
                alphasSelect[alphasIndex].setEnabled(true);
                alphasSelect[finalI].setEnabled(false);
                alphasIndex = finalI;
                background.setColor(allAlpha[alphasIndex] & resultBgColor);
            });
        }
        // 确定按钮
        ok.setOnClickListener(v -> {
            widgetAttr.updateAttr(allAlpha[alphasIndex], resultBgColor, notesInput, resultTextColor, allTextSize[sizesIndex]);
            NotesWidget.updateAppWidget(activity, AppWidgetManager.getInstance(this), appWidgetId, widgetAttr);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        if (widgetAttr.needUpdate(allAlpha[alphasIndex], resultBgColor, notesInput, resultTextColor, allTextSize[sizesIndex])) {
            if (exitDialog == null) {
                View dialogView = View.inflate(activity, R.layout.dialog_exit_tip, null);
                exitDialog = new AlertDialog.Builder(activity, R.style.CircleCornerAlertDialog)
                        .setView(dialogView)
                        .create();
                dialogView.findViewById(R.id.dialogCancel).setOnClickListener(v -> exitDialog.dismiss());
                dialogView.findViewById(R.id.dialogOk).setOnClickListener(v -> activity.finish());
            }
            exitDialog.show();
        } else {
            super.onBackPressed();
        }
    }
}
