package com.example.keepnotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepnotes.databases.NotesEntry;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    private List<NotesEntry> mNotesEntries;
    private Context mContext;
    private final ItemClickListener mItemClickListener;

    public NotesAdapter(Context mContext, ItemClickListener listener) {
        this.mContext = mContext;
        mItemClickListener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.note_layout,parent, false);
        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull NotesViewHolder holder, int position) {
        NotesEntry noteEntry = mNotesEntries.get(position);

        String title = noteEntry.getTitle();
        String note = noteEntry.getNote();
        Date date = noteEntry.getEditedAt();

        SimpleDateFormat formatter = new SimpleDateFormat("EEE HH:mm MMM d, yyyy");
        String dateFormat = formatter.format(date);
//        System.out.println("date-" + dateFormat);
        try {
            dateFormat = formatToYesterdayOrToday(dateFormat);
//            System.out.println("date- try" + dateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
//            System.out.println("date- exception " + e.getMessage());
        }

        holder.titleTextView.setText(title);
        holder.noteTextView.setText(note);
        holder.dateTimeTextView.setText(dateFormat);
    }

    @Override
    public int getItemCount() {
        if(mNotesEntries == null) {
            System.out.println("mNotes entries are null");
            return 0;
        }
        return mNotesEntries.size();
    }

    public void filterList(ArrayList<NotesEntry> filteredNotes) {
        System.out.println("in filter List Notes adapter");
        mNotesEntries = filteredNotes;
        notifyDataSetChanged();
    }

    public void setNotes(List<NotesEntry> notesEntries) {
        mNotesEntries = notesEntries;
        notifyDataSetChanged();
    }

//    public List<NotesEntry> getTasks() {
//        return mNotesEntries;
//    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    public static String formatToYesterdayOrToday(String date) throws ParseException {
//        System.out.println("date- func() " + date);
        Date dateTime = new SimpleDateFormat("EEE HH:mm MMM d, yyyy").parse(date);
//        System.out.println("date-" + dateTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        DateFormat timeFormatter = new SimpleDateFormat("HH:mm");

        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            return "Today " + timeFormatter.format(dateTime);
        } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
            return "Yesterday " + timeFormatter.format(dateTime);
        } else {
            return date;
        }
    }


    class NotesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleTextView;
        TextView noteTextView;
        TextView dateTimeTextView;

        public NotesViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_view);
            noteTextView = itemView.findViewById(R.id.note_view);
            dateTimeTextView = itemView.findViewById(R.id.date_time_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int elementId = mNotesEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }

}
