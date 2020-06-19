package com.hifeful.notekeeper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    public static final String TAG = MainActivity.class.getSimpleName();

    // UI
    private RecyclerView recyclerView;
    private RadioGroup sortTypes;
    private RadioGroup sortOrders;

    // Variables
    private ArrayList<Note> notes;
    private NoteDatabase noteDatabase = new NoteDatabase(this);
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        ImageButton sortByButton = findViewById(R.id.sortBy_button);
        sortByButton.setOnClickListener(v -> {
            View popupView = View.inflate(this, R.layout.layout_popup_sort, null);
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;

            PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
            popupWindow.setElevation(24);
            popupWindow.showAsDropDown(sortByButton);

            sortTypes = popupView.findViewById(R.id.sortTypes);
            sortTypes.setOnCheckedChangeListener(this);
            sortOrders = popupView.findViewById(R.id.sortOrders);
            sortOrders.setOnCheckedChangeListener(this);
        });

        recyclerView = findViewById(R.id.note_recycler);

        notes = new ArrayList<>();

//        notes.add(new Note("Dipa", "Zdarova", Calendar.getInstance().getTime(), Color.parseColor("#FF8A80")));
//        notes.add(new Note("Dipa", "Zdarova", Calendar.getInstance().getTime(), Color.parseColor("#FF80AB")));
//        notes.add(new Note("Dipa", "Zdarova", Calendar.getInstance().getTime(), Color.parseColor("#B388FF")));
//        notes.add(new Note("Dipa", "Zdarova", Calendar.getInstance().getTime(), Color.parseColor("#8C9EFF")));
//        notes.add(new Note("Dipa", "Zdarova", Calendar.getInstance().getTime(), Color.parseColor("#B9F6CA")));
//        notes.add(new Note("Dipa", "Zdarova", Calendar.getInstance().getTime(), Color.parseColor("#FFD180")));

        noteAdapter = new NoteAdapter(MainActivity.this, notes, noteDatabase);
        recyclerView.setAdapter(noteAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        SwipeController swipeController = new SwipeController(this, noteAdapter, recyclerView);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        new GetAllNotesTask().execute();

        FloatingActionButton fab = findViewById(R.id.add_button);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
            intent.putExtra(NoteActivity.ACTION, false);

            startActivityForResult(intent, noteAdapter.getItemCount());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        noteDatabase.close();
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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton sortType = sortTypes.findViewById(sortTypes.getCheckedRadioButtonId());
        RadioButton sortOrder = sortOrders.findViewById(sortOrders.getCheckedRadioButtonId());

        if (sortType.getText().equals("Title")) {
            if (sortOrder.getText().equals("Ascending")) {
                Collections.sort(notes, noteAdapter.sortByTitleAscending);
                noteAdapter.notifyDataSetChanged();
            } else if (sortOrder.getText().equals("Descending")) {
                Collections.sort(notes, noteAdapter.sortByTitleDescending);
                noteAdapter.notifyDataSetChanged();
            }
        } else if (sortType.getText().equals("Date")) {
            if (sortOrder.getText().equals("Ascending")) {
                Collections.sort(notes, noteAdapter.sortByDateAscending);
                noteAdapter.notifyDataSetChanged();
            } else if (sortOrder.getText().equals("Descending")) {
                Collections.sort(notes, noteAdapter.sortByDateDescending);
                noteAdapter.notifyDataSetChanged();
            }
        }
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
            if (dbNotes != null) {
                notes.clear();
                notes.addAll(dbNotes);
                noteAdapter.notifyDataSetChanged();
            }
        }
    }
}
