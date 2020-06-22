package com.hifeful.notekeeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import petrov.kristiyan.colorpicker.ColorPicker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.ArrayList;


public class NoteActivity extends AppCompatActivity {
    // true - update a note, false - create a new note
    public static final String ACTION = "ACTION";
    public static final String TITLE = "TITLE";
    public static final String TEXT = "TEXT";
    public static final String COLOR = "COLOR";

    private Intent intent;

    private boolean action;
    private boolean isEditing;

    private RelativeLayout noteLayout;
    private EditText titleView;
    private EditText noteView;

    private MenuItem editSaveItem;
    private MenuItem pickColorItem;
    private GradientDrawable gradientDrawable;

    private String title;
    private String note;
    private int mStartColor;
    private int mColor;
    private ArrayList<String> colors;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        noteLayout = findViewById(R.id.note_layout);

        titleView = findViewById(R.id.title_note);
        noteView = findViewById(R.id.text_note);

        Toolbar toolbar = findViewById(R.id.note_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        intent = getIntent();

        if (intent.getExtras() != null) {
            action = intent.getExtras().getBoolean(ACTION);

            if (action) {
                setTitle(R.string.note);

                titleView.setText(intent.getStringExtra(TITLE));
                noteView.setText(intent.getStringExtra(TEXT));

                disableContentInteraction();
            } else {
                setTitle(R.string.new_note);
                titleView.requestFocus();
            }
        }
        mStartColor = intent.getIntExtra(COLOR, Color.parseColor("#FAFAFA"));
        mColor = mStartColor;
        noteLayout.setBackgroundColor(mColor);

        title = titleView.getText().toString();
        note = noteView.getText().toString();

        setUpColors();
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (isEditing) {
            onOptionsItemSelected(editSaveItem);
        } else {
            onBackPressed();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        editSaveItem = menu.findItem(R.id.action_edit_save);
        pickColorItem = menu.findItem(R.id.action_color_picker);
        gradientDrawable = new GradientDrawable();
        gradientDrawable.setSize(100, 100);
        gradientDrawable.setColor(mColor);
        gradientDrawable.setStroke(5, Color.BLACK);
        pickColorItem.setIcon(gradientDrawable);

        if (action) {
            editSaveItem.setIcon(getResources().getDrawable(R.drawable.ic_edit_black_24dp));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_save:
                if (action) {
                    if (isEditing) {
                        disableContentInteraction();
                        hideSoftKeyboard();
                        item.setIcon(getResources().getDrawable(R.drawable.ic_edit_black_24dp));
                        isEditing = false;
                    } else {
                        enableContentInteraction();
                        showSoftKeyboard();
                        item.setIcon(getResources().getDrawable(R.drawable.ic_save_black_24dp));

                        noteView.requestFocus();
                        noteView.setSelection(noteView.length());

                        isEditing = true;
                    }
                } else {
                    onBackPressed();
                }
                break;
            case R.id.action_color_picker:
                ColorPicker colorPicker = new ColorPicker(this);
                colorPicker.setColors(colors);
                colorPicker.setDefaultColorButton(mColor);
                colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        mColor = color;
                        noteLayout.setBackgroundColor(mColor);
                        gradientDrawable.setColor(mColor);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                colorPicker.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (action) {
            if (checkChanges()){
                intent.putExtra(TITLE, titleView.getText().toString());
                intent.putExtra(TEXT, noteView.getText().toString());
                intent.putExtra(COLOR, mColor);

                setResult(RESULT_OK, intent);
            }

        }else {
            if (checkChanges()){
                intent.putExtra(TITLE, titleView.getText().toString());
                intent.putExtra(TEXT, noteView.getText().toString());
                intent.putExtra(COLOR, mColor);

                setResult(RESULT_OK, intent);
            }
        }
        super.onBackPressed();
    }

    private void disableContentInteraction() {
        titleView.setKeyListener(null);
        titleView.setFocusable(false);
        titleView.setFocusableInTouchMode(false);
        titleView.setCursorVisible(false);

        noteView.setKeyListener(null);
        noteView.setFocusable(false);
        noteView.setFocusableInTouchMode(false);
        noteView.setCursorVisible(false);
        noteView.clearFocus();
    }

    private void enableContentInteraction() {
        titleView.setKeyListener(new EditText(this).getKeyListener());
        titleView.setFocusable(true);
        titleView.setFocusableInTouchMode(true);
        titleView.setCursorVisible(true);

        noteView.setKeyListener(new EditText(this).getKeyListener());
        noteView.setFocusable(true);
        noteView.setFocusableInTouchMode(true);
        noteView.setCursorVisible(true);
        noteView.requestFocus();
    }

    private void showSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)
                this.getSystemService(Activity.INPUT_METHOD_SERVICE);

        inputMethodManager.showSoftInput(noteView, InputMethodManager.SHOW_IMPLICIT);
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void setUpColors() {
        colors = new ArrayList<>();

        colors.add("#FAFAFA");
        colors.add("#" + Integer.toHexString(ContextCompat.getColor(this,
                R.color.redMaterial) & 0x00ffffff));
        colors.add("#" + Integer.toHexString(ContextCompat.getColor(this,
                R.color.pinkMaterial) & 0x00ffffff));
        colors.add("#" + Integer.toHexString(ContextCompat.getColor(this,
                R.color.purpleMaterial) & 0x00ffffff));
        colors.add("#" + Integer.toHexString(ContextCompat.getColor(this,
                R.color.blueMaterial) & 0x00ffffff));
        colors.add("#" + Integer.toHexString(ContextCompat.getColor(this,
                R.color.greenMaterial) & 0x00ffffff));
        colors.add("#" + Integer.toHexString(ContextCompat.getColor(this,
                R.color.orangeMaterial) & 0x00ffffff));
        colors.add("#" + Integer.toHexString(ContextCompat.getColor(this,
                R.color.yellowMaterial) & 0x00ffffff));
        colors.add("#" + Integer.toHexString(ContextCompat.getColor(this,
                R.color.cyanMaterial) & 0x00ffffff));
        colors.add("#" + Integer.toHexString(ContextCompat.getColor(this,
                R.color.brownMaterial) & 0x00ffffff));
    }

    private boolean checkChanges() {
        // true if changes exist
        if (action) {
            return !title.contentEquals(titleView.getText().toString()) ||
                    !note.contentEquals(noteView.getText().toString()) ||
                    mStartColor != mColor;
        } else {
            return !title.contentEquals(titleView.getText().toString()) ||
                    !note.contentEquals(noteView.getText().toString());
        }
    }
}
