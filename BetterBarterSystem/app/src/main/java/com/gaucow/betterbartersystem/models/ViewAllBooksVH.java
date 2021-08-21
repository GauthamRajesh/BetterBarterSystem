package com.gaucow.betterbartersystem.models;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gaucow.betterbartersystem.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewAllBooksVH extends RecyclerView.ViewHolder {
    public TextView titleTextView;
    public TextView authorTextView;
    public TextView offeredByTextView;
    public Button viewDetailsButton;
    public TextView isbn;
    public ViewAllBooksVH(@NonNull View itemView) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.titleText);
        authorTextView = itemView.findViewById(R.id.authorText);
        offeredByTextView = itemView.findViewById(R.id.offeredByText);
        viewDetailsButton = itemView.findViewById(R.id.viewdetails);
        isbn = itemView.findViewById(R.id.isbn);
    }
}
