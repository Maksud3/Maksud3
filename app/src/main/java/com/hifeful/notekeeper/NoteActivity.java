package com.hifeful.notekeeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

public class NoteActivity extends AppCompatActivity {
    // true - update a note, false - create a new note
    public static final String ACTION = "ACTION";
    public static final String TITLE = "TITLE";
    public static final String TEXT = "TEXT";
    public static final String COLOR = "COLOR";

    private Intent intent;

    private boolean action;

    private TextView titleView;
    private TextView noteView;

    private String title;
    private String note;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        intent = getIntent();

        if (intent.getExtras() != null) {
            action = intent.getExtras().getBoolean(ACTION);
        }
        Toolbar toolbar = findViewById(R.id.note_toolbar);
        setSupportActionBar(toolbar);

        titleView = findViewById(R.id.title_note);
        noteView = findViewById(R.id.text_note);

        title = titleView.getText().toString();
        note = noteView.getText().toString();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {

        if (action) {

        }else {
            if (checkChanges()){
                intent.putExtra(TITLE, titleView.getText().toString());
                intent.putExtra(TEXT, noteView.getText().toString());
                intent.putExtra(COLOR, getResources().getColor(R.color.orangeMaterial));

                setResult(RESULT_OK, intent);
            }
        }
        super.onBackPressed();
    }

    private boolean checkChanges() {
        // true if changes exist
        return !title.contentEquals(titleView.getText().toString()) ||
                !note.contentEquals(noteView.getText().toString());
    }
}
