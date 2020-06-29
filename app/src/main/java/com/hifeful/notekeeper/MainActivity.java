package com.hifeful.notekeeper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String SORT_TYPE = "sortType";
    public static final String SORT_ORDER = "sortOrder";

    // UI
    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private View popupView;
    private PopupWindow popupWindow;
    private RadioGroup sortTypes;
    private RadioGroup sortOrders;

    private ImageButton sortBy;

    // Variables
    private ArrayList<Note> notes;
    private NoteDatabase noteDatabase = new NoteDatabase(this);
    private NoteAdapter noteAdapter;

    private boolean isSortByOpened = false;
    private boolean isRestored = false;

    public static String titleName;
    public static String dateName;
    public static String ascendingName;
    public static String descendingName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        popupView = View.inflate(this, R.layout.layout_popup_sort, null);
        sortTypes = popupView.findViewById(R.id.sortTypes);
        sortOrders = popupView.findViewById(R.id.sortOrders);

        titleName = getResources().getString(R.string.by_title);
        dateName = getResources().getString(R.string.by_date);
        ascendingName = getResources().getString(R.string.ascending);
        descendingName = getResources().getString(R.string.descending);

        recyclerView = findViewById(R.id.note_recycler);

        notes = new ArrayList<>();

        noteAdapter = new NoteAdapter(MainActivity.this, notes, noteDatabase);
        recyclerView.setAdapter(noteAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        SwipeController swipeController = new SwipeController(this, noteAdapter, recyclerView);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        if (!isRestored) {
            new GetAllNotesTask().execute();
        }

        FloatingActionButton fab = findViewById(R.id.add_button);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
            intent.putExtra(NoteActivity.ACTION, false);
            intent.putExtra(NoteActivity.COLOR, Color.parseColor("#FAFAFA"));

            startActivityForResult(intent, noteAdapter.getItemCount());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isRestored) {
            new GetAllNotesTask().execute();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        noteDatabase.close();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (isSortByOpened) {
            popupWindow.dismiss();
            isSortByOpened = true;
        }
        outState.putBoolean("sortBy", isSortByOpened);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        isSortByOpened = savedInstanceState.getBoolean("sortBy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        sortBy = (ImageButton) menu.findItem(R.id.action_sortBy).getActionView();

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                noteAdapter.getNotesFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sortBy) {
            showSortBy();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String title;
        String text;
        int color;

        if (requestCode == noteAdapter.getItemCount()) {
            if (resultCode == RESULT_OK) {
                if (data != null){
                    title = data.getStringExtra(NoteActivity.TITLE);
                    text = data.getStringExtra(NoteActivity.TEXT);
                    color = data.getIntExtra(NoteActivity.COLOR, getResources()
                                        .getColor(android.R.color.background_light));

                    noteAdapter.addNote(title, text, Calendar.getInstance().getTime(), color);
                }
            }
        } else {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    title = data.getStringExtra(NoteActivity.TITLE);
                    text = data.getStringExtra(NoteActivity.TEXT);
                    color = data.getIntExtra(NoteActivity.COLOR, getResources()
                                            .getColor(android.R.color.background_light));

                    Note note = new Note(-1, title, text, Calendar.getInstance().getTime(), color);
                    note.setListPosition(requestCode);

                    noteAdapter.updateNote(note);
                }
            }
        }
    }

    private void showSortBy() {
        Log.i(TAG, "showSortBy: Tell me");
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.setElevation(24);

        popupWindow.showAtLocation(popupView, Gravity.END | Gravity.TOP, 0,
                locateView(toolbar).bottom);
        isSortByOpened = true;
        popupWindow.setOnDismissListener(() -> isSortByOpened = false);

        sortTypes.setOnCheckedChangeListener(this);
        sortOrders.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        sortNotes();
        saveSortStates();
    }

    private void loadSortStates() {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        ((RadioButton)sortTypes.getChildAt(sharedPreferences.getInt(SORT_TYPE, 1)))
                .toggle();
        ((RadioButton)sortOrders.getChildAt(sharedPreferences.getInt(SORT_ORDER, 1)))
                .toggle();
        sortNotes();
    }

    private void saveSortStates() {
        RadioButton sortType = sortTypes.findViewById(sortTypes.getCheckedRadioButtonId());
        RadioButton sortOrder = sortOrders.findViewById(sortOrders.getCheckedRadioButtonId());

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SORT_TYPE, sortType.getText().equals(titleName) ? 0 : 1);
        editor.putInt(SORT_ORDER, sortOrder.getText().equals(ascendingName) ? 0 : 1);
        editor.apply();
    }

    private void sortNotes() {
        RadioButton sortType = popupView.findViewById(sortTypes.getCheckedRadioButtonId());
        RadioButton sortOrder = popupView.findViewById(sortOrders.getCheckedRadioButtonId());

        if (sortType.getText().equals(titleName)) {
            if (sortOrder.getText().equals(ascendingName)) {
                noteAdapter.sortBy(titleName, ascendingName);
            } else if (sortOrder.getText().equals(descendingName)) {
                noteAdapter.sortBy(titleName, descendingName);
            }
        } else if (sortType.getText().equals(dateName)) {
            if (sortOrder.getText().equals(ascendingName)) {
                noteAdapter.sortBy(dateName, ascendingName);
            } else if (sortOrder.getText().equals(descendingName)) {
                noteAdapter.sortBy(dateName, descendingName);
            }
        }
    }

    public static Rect locateView(View v) {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
    }

    @SuppressLint("StaticFieldLeak")
    private class GetAllNotesTask extends AsyncTask<Void, Void, ArrayList<Note>> {

        @Override
        protected ArrayList<Note> doInBackground(Void... voids) {
            noteDatabase.open();
            return noteDatabase.getAll();
        }

        @Override
        protected void onPostExecute(ArrayList<Note> dbNotes) {
            Log.i(TAG, "onPostExecute: ");
            if (dbNotes != null) {
                notes.clear();
                notes.addAll(dbNotes);
                noteAdapter.notesForFilter.addAll(dbNotes);
                noteAdapter.notifyDataSetChanged();

                loadSortStates();
                if (isSortByOpened) {
                    showSortBy();
                }
            }
        }
    }
}
