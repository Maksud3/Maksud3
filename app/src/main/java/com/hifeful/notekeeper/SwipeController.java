package com.hifeful.notekeeper;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SwipeController extends ItemTouchHelper.Callback {
    private Context context;
    private NoteAdapter noteAdapter;
    private View recycler;

    private boolean swipeBack = false;
    private Note deletedNote = null;

    public SwipeController(Context context, NoteAdapter adapter, View recycler) {
        this.context = context;
        this.noteAdapter = adapter;
        this.recycler = recycler;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();

        if (direction == ItemTouchHelper.LEFT) {
            deletedNote = noteAdapter.notes.get(position);

            noteAdapter.notes.remove(position);
            noteAdapter.notifyItemRemoved(position);

            Snackbar.make(recycler, "Shit", Snackbar.LENGTH_SHORT)
                    .setAction(context.getResources().getText(R.string.undo), v -> {
                        noteAdapter.notes.add(position, deletedNote);
                        noteAdapter.notifyItemInserted(position);
                    })
                    .addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                                noteAdapter.notesForFilter.remove(position);
                                noteAdapter.deleteNote(deletedNote.getId());
                            }
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                .addSwipeLeftBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light))
                .create()
                .decorate();
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (swipeBack) {
            swipeBack = false;
            return 0;
        }

        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }
}
