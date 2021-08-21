package com.gaucow.betterbartersystem.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gaucow.betterbartersystem.R;
import com.gaucow.betterbartersystem.activities.BuySellListActivity;
import com.gaucow.betterbartersystem.models.Listing;
import com.gaucow.betterbartersystem.models.ViewMyBooksVH;
import com.gaucow.betterbartersystem.utilities.Utilities;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ViewMyBooksFragment extends Fragment {
    private FirestoreRecyclerAdapter adapter;
    private TextView noBooks;
    private Button addBooks;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_viewmybooks, container, false);
        final RecyclerView rv = v.findViewById(R.id.rv_viewbooks);
        noBooks = v.findViewById(R.id.noBooks);
        addBooks = v.findViewById(R.id.addBooks);
        addBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), BuySellListActivity.class));
            }
        });
        FirebaseApp.initializeApp(getContext());
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser mUser = mAuth.getCurrentUser();
        if(mUser == null) {
            Toast.makeText(getContext(), "Not authorized!", Toast.LENGTH_SHORT).show();
            return null;
        }
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .build();
        db.setFirestoreSettings(settings);
        final Query booksQuery = db.collection("allbooks").whereEqualTo("userEmail", mUser.getEmail());
        FirestoreRecyclerOptions<Listing> options = new FirestoreRecyclerOptions.Builder<Listing>()
                .setQuery(booksQuery, Listing.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<Listing, ViewMyBooksVH>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewMyBooksVH viewHolder, int i, @NonNull final Listing o) {
                String titleName = o.getBookAndAuthorName().get(0);
                String authorName = o.getBookAndAuthorName().get(1);
                viewHolder.titleTextView.setText(String.format("Title: %s", titleName));
                viewHolder.authorTextView.setText(String.format("Author: %s", authorName));
                String text;
                if(o.getBookType() == Utilities.SELL) {
                    text = "For Sale";
                } else {
                    text = "To Buy";
                }
                viewHolder.bookTypeTextView.setText(text);
                viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delete(db, o);
                    }
                });
                viewHolder.isbn.setText(new StringBuilder().append("ISBN: ").append(o.getIsbn()).toString());
            }
            @NonNull
            @Override
            public ViewMyBooksVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewmybooks_list_item, parent, false);
                return new ViewMyBooksVH(view);
            }
            @Override
            public void onDataChanged() {
                booksQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.getDocuments().size() == 0) {
                            noBooks.setVisibility(View.VISIBLE);
                        } else {
                            noBooks.setVisibility(View.GONE);
                        }
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    private void delete(FirebaseFirestore db, Listing o) {
        db.collection("allbooks").document(o.getDocID())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Successfully deleted", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
