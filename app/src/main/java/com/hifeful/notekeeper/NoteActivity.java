package com.hifeful.notekeeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import petrov.kristiyan.colorpicker.ColorPicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
    private boolean isNoteField = true;
    private boolean isColorPickerOpened = false;

    private RelativeLayout noteLayout;
    private EditText titleView;
    private EditText noteView;

    private ColorPicker colorPicker;
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

                disableEditing();
            } else {
                setTitle(R.string.new_note);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("mode", isEditing);
        outState.putBoolean("colorPicker", isColorPickerOpened);
        if (isColorPickerOpened) {
            colorPicker.dismissDialog();
        }
        outState.putInt("startColor", mStartColor);
        outState.putInt("color", mColor);

        if (getCurrentFocus() != null)
        {
            int startSelection = 0;
            int endSelection = 0;
            switch (getCurrentFocus().getId()) {
                case R.id.title_note:
                    isNoteField = false;
                    startSelection = ((EditText)getCurrentFocus()).getSelectionStart();
                    endSelection = ((EditText)getCurrentFocus()).getSelectionEnd();
                    break;
                case R.id.text_note:
                    isNoteField = true;
                    startSelection = ((EditText)getCurrentFocus()).getSelectionStart();
                    endSelection = ((EditText)getCurrentFocus()).getSelectionEnd();
                    break;
            }
            outState.putBoolean("isNote", isNoteField);
            outState.putInt("startSelection", startSelection);
            outState.putInt("endSelection", endSelection);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        isEditing = savedInstanceState.getBoolean("mode");
        isColorPickerOpened = savedInstanceState.getBoolean("colorPicker");
        mStartColor = savedInstanceState.getInt("startColor");
        mColor = savedInstanceState.getInt("color");
        noteLayout.setBackgroundColor(mColor);

        boolean isNote = savedInstanceState.getBoolean("isNote", true);
        int startSelection = savedInstanceState.getInt("startSelection", noteView.length());
        int endSelection = savedInstanceState.getInt("endSelection", noteView.length());

        if (isEditing) {
            enableEditing(isNote ? noteView : titleView, startSelection, endSelection);
        }

        if (isColorPickerOpened) {
            showColorPicker();
        }
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
            editSaveItem.setIcon(isEditing
                    ? getResources().getDrawable(R.drawable.ic_save_black_24dp)
                    : getResources().getDrawable(R.drawable.ic_edit_black_24dp));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_save:
                if (action) {
                    if (isEditing) {
                        disableEditing();
                    } else {
                        enableEditing(noteView, noteView.length(), noteView.length());
                    }
                } else {
                    onBackPressed();
                }
                break;
            case R.id.action_color_picker:
                showColorPicker();
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

    private void enableEditing(EditText field, int startSelection, int endSelection) {
        enableContentInteraction();
        if (getCurrentFocus() != null) {
            showSoftKeyboard(getCurrentFocus());
        }

        if (!isEditing) {
            editSaveItem.setIcon(getResources().getDrawable(R.drawable.ic_save_black_24dp));
        }

        field.requestFocus();
        field.setSelection(startSelection, endSelection);

        isEditing = true;
    }

    private void disableEditing() {
        if (getCurrentFocus() != null) {
            hideSoftKeyboard(getCurrentFocus());
        }
        disableContentInteraction();
        if (isEditing) {
            editSaveItem.setIcon(getResources().getDrawable(R.drawable.ic_edit_black_24dp));
        }
        isEditing = false;
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

    private void showSoftKeyboard(View view) {
        if(view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

    private void showColorPicker() {
        colorPicker = new ColorPicker(this);
        colorPicker.setColors(colors);
        colorPicker.setDefaultColorButton(mColor);
        colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
            @Override
            public void setOnFastChooseColorListener(int position, int color) {
                if (color != 0) {
                    mColor = color;
                    noteLayout.setBackgroundColor(mColor);
                    gradientDrawable.setColor(mColor);
                }
                isColorPickerOpened = false;
            }

            @Override
            public void onCancel() {
            }
        });
        colorPicker.show();
        isColorPickerOpened = true;
        colorPicker.getmDialog().setOnDismissListener(dialog -> {
            isColorPickerOpened = false;
        });
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
