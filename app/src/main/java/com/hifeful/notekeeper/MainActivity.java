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
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private ArrayList<Note> notes;
    private NoteDatabase noteDatabase = new NoteDatabase(this);
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

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
    protected void onResume() {
        super.onResume();
        noteDatabase.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
