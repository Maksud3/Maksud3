package com.hifeful.notekeeper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Note> notes;
    private NoteDatabase noteDatabase;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView title = cardView.findViewById(R.id.title_text);
        TextView note = cardView.findViewById(R.id.inside_text);
        TextView textDate = cardView.findViewById(R.id.date_text);

        title.setText(notes.get(position).getTitle());
        note.setText(notes.get(position).getText());

        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a dd MMM ''yy", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        textDate.setText(dateFormat.format(notes.get(position).getDate()));

        cardView.setCardBackgroundColor(notes.get(position).getColor());
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
}
