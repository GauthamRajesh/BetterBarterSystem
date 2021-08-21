package com.gaucow.betterbartersystem.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gaucow.betterbartersystem.R;
import com.gaucow.betterbartersystem.activities.ViewBookDetailActivity;
import com.gaucow.betterbartersystem.models.Listing;
import com.gaucow.betterbartersystem.models.ViewAllBooksVH;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewAllBooksAdapter extends RecyclerView.Adapter<ViewAllBooksVH> {
    private ArrayList<Listing> listings;
    private Context c;
    public ViewAllBooksAdapter(ArrayList<QuerySnapshot> data, Context c) {
        listings = new ArrayList<>();
        for(QuerySnapshot s : data) {
            for(QueryDocumentSnapshot document : s) {
                Listing l = document.toObject(Listing.class);
                listings.add(l);
            }
        }
        this.c = c;
    }
    @NonNull
    @Override
    public ViewAllBooksVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewAllBooksVH(LayoutInflater.from(c).inflate(R.layout.viewallbooks_list_item, parent, false));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewAllBooksVH holder, int position) {
        final Listing docListing = listings.get(position);
        String title = docListing.getBookAndAuthorName().get(0);
        String author = docListing.getBookAndAuthorName().get(1);
        String offeredByText =  docListing.getName();
        holder.titleTextView.setText(String.format("Title: %s", title));
        holder.authorTextView.setText(String.format("Author: %s", author));
        holder.offeredByTextView.setText(String.format("Offered by: %s", offeredByText));
        holder.viewDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c, ViewBookDetailActivity.class);
                i.putExtra("listing", docListing);
                c.startActivity(i);
            }
        });
        holder.isbn.setText(String.format("ISBN: %d", docListing.getIsbn()));
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

}
