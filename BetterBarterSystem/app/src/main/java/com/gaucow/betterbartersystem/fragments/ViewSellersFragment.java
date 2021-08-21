package com.gaucow.betterbartersystem.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gaucow.betterbartersystem.R;
import com.gaucow.betterbartersystem.adapters.ViewSellersAdapter;
import com.gaucow.betterbartersystem.models.Listing;
import com.gaucow.betterbartersystem.utilities.Utilities;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ViewSellersFragment extends Fragment {
    private ArrayList<QuerySnapshot> data;
    private static ArrayList<Listing> wantedBooks;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_viewsellers, container, false);
        final RecyclerView rv = v.findViewById(R.id.rv_viewsellers);
        final TextView noBooksAvailable = v.findViewById(R.id.noBooksAvailable);
        //assert getArguments() != null;
        data = new ArrayList<>();
        wantedBooks = new ArrayList<>();
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
        try {
            wantedBooks = new getWantedAsyncTask().execute(mUser.getEmail()).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < wantedBooks.size(); i++) {
            Query sellersQuery = db.collection("allbooks").whereEqualTo("bookType", 2).whereEqualTo("isbn", wantedBooks.get(i).getIsbn());
            final int finalI = i;
            sellersQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.getDocuments().size() != 0) {
                        data.add(queryDocumentSnapshots);
                    }
                    if(finalI == wantedBooks.size() - 1 && data.size() != 0) {
                        rv.setLayoutManager(new LinearLayoutManager(getContext()));
                        ViewSellersAdapter v = new ViewSellersAdapter(data, getContext());
                        v.notifyDataSetChanged();
                        rv.setAdapter(v);

                    } else {
                        rv.setVisibility(View.GONE);
                        noBooksAvailable.setVisibility(View.VISIBLE);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
        return v;
    }
    private static class getWantedAsyncTask extends AsyncTask<String, Void, ArrayList<Listing>> {
        @Override
        protected ArrayList<Listing> doInBackground(String... s) {
            try {
                return Utilities.getWantedBooks(s[0]);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }
    }
}
