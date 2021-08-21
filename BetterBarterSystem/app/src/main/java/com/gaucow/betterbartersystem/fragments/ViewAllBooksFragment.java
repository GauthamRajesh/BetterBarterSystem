package com.gaucow.betterbartersystem.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gaucow.betterbartersystem.R;
import com.gaucow.betterbartersystem.adapters.ViewAllBooksAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ViewAllBooksFragment extends Fragment {
    private ArrayList<QuerySnapshot> allData;
    private TextView noBooksAvailable;
    private boolean empty = false;
    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_viewallbooks, container, false);
        final RecyclerView rv = v.findViewById(R.id.rv_viewallbooks);
        noBooksAvailable = v.findViewById(R.id.noBooksAvailable);
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

        allData = new ArrayList<>();

        final Query allBooksQueryLess = db.collection("allbooks").whereLessThan("userEmail", Objects.requireNonNull(mUser.getEmail()))
                .whereEqualTo("bookType", 2);
        final Query allBooksQueryGreater = db.collection("allbooks").whereGreaterThan("userEmail", Objects.requireNonNull(mUser.getEmail()))
                .whereEqualTo("bookType", 2);
        Task<List<QuerySnapshot>> t = Tasks.whenAllSuccess(allBooksQueryLess.get(), allBooksQueryGreater.get());
        t.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                int numZero = 0;
                for(int i = 0; i < querySnapshots.size(); i++) {
                    if(querySnapshots.get(i).getDocuments().size() != 0) {
                        allData.add(querySnapshots.get(i));
                    }
                    else {
                        numZero++;
                    }
                }
                if(numZero == querySnapshots.size()) {
                    empty = true;
                }
                if(!empty) {
                    rv.setLayoutManager(new LinearLayoutManager(getContext()));
                    rv.setAdapter(new ViewAllBooksAdapter(allData, getContext()));
                } else {
                   noBooksAvailable.setVisibility(View.VISIBLE);
                }
            }
        });
        return v;
    }
}
