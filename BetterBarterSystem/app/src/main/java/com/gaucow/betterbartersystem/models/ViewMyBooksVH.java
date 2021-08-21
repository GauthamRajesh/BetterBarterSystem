package com.gaucow.betterbartersystem.models;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gaucow.betterbartersystem.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewMyBooksVH extends RecyclerView.ViewHolder {
    public TextView titleTextView;
    public TextView authorTextView;
    public TextView bookTypeTextView;
    public Button deleteButton;
    public TextView isbn;
    public ViewMyBooksVH(@NonNull View v) {
        super(v);
        titleTextView = v.findViewById(R.id.titleText);
        authorTextView = v.findViewById(R.id.authorText);
        deleteButton = v.findViewById(R.id.delete);
        bookTypeTextView = v.findViewById(R.id.bookType);
        isbn = v.findViewById(R.id.isbn);
    }
}
