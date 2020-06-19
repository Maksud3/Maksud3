package com.hifeful.notekeeper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private Context context;
    public ArrayList<Note> notes;
    private NoteDatabase noteDatabase;

    public Comparator<Note> sortByDateDescending = (note, n1) -> Long.compare(n1.getDate().getTime(), note.getDate().getTime());
    public Comparator<Note> sortByDateAscending = (note, n1) -> Long.compare(note.getDate().getTime(), n1.getDate().getTime());

    public Comparator<Note> sortByTitleDescending = (o1, o2) -> o2.getTitle().compareTo(o1.getText());
    public Comparator<Note> sortByTitleAscending = (o1, o2) -> o1.getTitle().compareTo(o2.getText());

    public NoteAdapter(Context context, ArrayList<Note> notes, NoteDatabase noteDatabase) {
        this.context = context;
        this.notes = notes;
        this.noteDatabase = noteDatabase;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }
    }

    @NonNull
    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.card_note, parent, false);

        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        TextView title = cardView.findViewById(R.id.title_text);
        final TextView note = cardView.findViewById(R.id.inside_text);
        TextView textDate = cardView.findViewById(R.id.date_text);

        title.setText(notes.get(position).getTitle());

        if (notes.get(position).getText().length() > 40)
            note.setText(notes.get(position).getText().substring(0, 35));
        else
            note.setText(notes.get(position).getText());

        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a dd MMM ''yy", Locale.US);
        textDate.setText(dateFormat.format(notes.get(position).getDate()));

        cardView.setCardBackgroundColor(notes.get(position).getColor());

        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NoteActivity.class);

            intent.putExtra(NoteActivity.ACTION, true);
            intent.putExtra(NoteActivity.TITLE, notes.get(position).getTitle());
            intent.putExtra(NoteActivity.TEXT, notes.get(position).getText());
            intent.putExtra(NoteActivity.COLOR, notes.get(position).getColor());

            ((AppCompatActivity) context).startActivityForResult(intent, position);
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void addNote(String title, String text, Date date, int color) {
        Note note = new Note();
        note.setTitle(title);
        note.setText(text);
        note.setDate(date);
        note.setColor(color);

        new CreateNoteTask().execute(note);
    }

    public void updateNote(Note note) {
        note.setId(notes.get(note.getListPosition()).getId());
        notes.set(note.getListPosition(), note);

        new UpdateNoteTask().execute(notes.get(note.getListPosition()));
    }

    public void deleteNote(long id) {
        new DeleteNoteTask().execute(id);
    }

    @SuppressLint("StaticFieldLeak")
    private class CreateNoteTask extends AsyncTask<Note, Void, Note> {
        @Override
        protected Note doInBackground(Note... noteses) {
            noteDatabase.create(noteses[0]);
            notes.add(noteses[0]);

            return noteses[0];
        }

        @Override
        protected void onPostExecute(Note note) {
            super.onPostExecute(note);

            notifyItemInserted(getItemCount());
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class UpdateNoteTask extends AsyncTask<Note, Void, Note> {
        @Override
        protected Note doInBackground(Note... notes) {
            noteDatabase.update(notes[0]);
            return notes[0];
        }

        @Override
        protected void onPostExecute(Note note) {
            super.onPostExecute(note);

            notifyItemChanged(note.getListPosition());
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DeleteNoteTask extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... id) {
            noteDatabase.delete(id[0]);
            return null;
        }
    }
}
