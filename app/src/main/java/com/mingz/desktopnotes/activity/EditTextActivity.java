package com.mingz.desktopnotes.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mingz.desktopnotes.R;
import com.mingz.desktopnotes.receiver.UpdateNotesReceiver;
import com.mingz.desktopnotes.service.MyRemoteViewsService;
import com.mingz.desktopnotes.utils.MyLog;
import com.mingz.desktopnotes.widget.NotesWidget;

public class EditTextActivity extends AppCompatActivity {
    private final MyLog myLog = new MyLog("EditMyTAG");
    private int appWidgetId;
    private MyRemoteViewsService.MyRemoteViewsFactory factory;

    private EditText inputText;
    private Button ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);

        Intent intent = getIntent();
        if (intent != null) {
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId >= 0) {
                Bundle data = intent.getExtras();
                factory = (MyRemoteViewsService.MyRemoteViewsFactory) data.getBinder(String.valueOf(appWidgetId));
                if (factory != null) {
                    initView();
                    myListener();
                    inputText.setText(factory.getNotes());
                    return;
                }
            }
        }
        Toast.makeText(this, "便签创建异常，请重新创建", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void initView() {
        inputText = findViewById(R.id.inputText);
        ok = findViewById(R.id.ok);
    }

    private void myListener() {
        ok.setOnClickListener(v -> {
            String input = String.valueOf(inputText.getText());
            if (TextUtils.isEmpty(input)) {
                input = "桌面便签";
            }
            factory.setNotes(input);
            /*Intent broad = new Intent(UpdateNotesReceiver.ACTION_INPUT);
            broad.setClass(this, UpdateNotesReceiver.class);
            broad.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            sendBroadcast(broad);*/
            AppWidgetManager.getInstance(this).notifyAppWidgetViewDataChanged(appWidgetId, R.id.notesList);
            finish();
        });
    }
}
