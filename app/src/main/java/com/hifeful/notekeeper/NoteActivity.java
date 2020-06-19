package com.hifeful.notekeeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class NoteActivity extends AppCompatActivity {
    // true - update a note, false - create a new note
    public static final String ACTION = "ACTION";
    public static final String TITLE = "TITLE";
    public static final String TEXT = "TEXT";
    public static final String COLOR = "COLOR";

    private Intent intent;

    private boolean action;
    private boolean isEditing;

    private Toolbar toolbar;
    private EditText titleView;
    private EditText noteView;
    private ImageButton saveButton;

    private String title;
    private String note;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        RelativeLayout layout = findViewById(R.id.note_layout);

        titleView = findViewById(R.id.title_note);
        noteView = findViewById(R.id.text_note);
        saveButton = findViewById(R.id.save_button);

        toolbar = findViewById(R.id.note_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        intent = getIntent();

        if (intent.getExtras() != null) {
            action = intent.getExtras().getBoolean(ACTION);

            if (action) {
                TextView title = toolbar.findViewById(R.id.title_toolbar);
                title.setText(R.string.note);

                titleView.setText(intent.getStringExtra(TITLE));
                noteView.setText(intent.getStringExtra(TEXT));

                int color = intent.getIntExtra(COLOR, getResources()
                        .getColor(android.R.color.background_light));

                layout.setBackgroundColor(color);

                titleView.setInputType(InputType.TYPE_NULL);
                noteView.setInputType(InputType.TYPE_NULL);
                noteView.setSingleLine(false);
                isEditing = false;

                Drawable drawable = getResources().getDrawable(R.drawable.ic_edit_black_24dp);

                saveButton.setImageDrawable(drawable);

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isEditing) {
                            titleView.setInputType(InputType.TYPE_NULL);
                            noteView.setInputType(InputType.TYPE_NULL);
                            noteView.setSingleLine(false);

                            Drawable drawable = getResources()
                                    .getDrawable(R.drawable.ic_edit_black_24dp);
                            saveButton.setImageDrawable(drawable);

                            isEditing = false;
                        } else {
                            titleView.setInputType(InputType.TYPE_CLASS_TEXT);
                            noteView.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);

                            Drawable drawable = getResources()
                                    .getDrawable(R.drawable.ic_save_black_24dp);
                            saveButton.setImageDrawable(drawable);

                            isEditing = true;
                        }
                    }
                });
            } else {
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            }
        }

        title = titleView.getText().toString();
        note = noteView.getText().toString();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {

        if (action) {
            if (checkChanges()){
                intent.putExtra(TITLE, titleView.getText().toString());
                intent.putExtra(TEXT, noteView.getText().toString());
                intent.putExtra(COLOR, getResources().getColor(R.color.orangeMaterial));

                setResult(RESULT_OK, intent);
            }

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
