package com.gaucow.betterbartersystem.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.gaucow.betterbartersystem.R;
import com.gaucow.betterbartersystem.models.Listing;
import com.gaucow.betterbartersystem.utilities.Utilities;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;

public class BuyFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_buylist, container, false);

        final AppCompatEditText titles = v.findViewById(R.id.bookName);
        final AppCompatEditText authors = v.findViewById(R.id.bookAuthor);
        final AppCompatEditText isbn = v.findViewById(R.id.bookISBN);
        final Button addBooks = v.findViewById(R.id.addBooks);

        addBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String originalTitles = titles.getText().toString();
                final String originalAuthors = authors.getText().toString();
                final String originalIsbn = isbn.getText().toString();
                String titlesString = originalTitles.replaceAll(" ", "");
                String authorsString = originalAuthors.replaceAll(" ", "");
                String isbnString = originalIsbn.replaceAll(" ", "");
                if((titlesString.length() == 0 || authorsString.length() == 0 || isbnString.length() == 0)) {
                    Toast.makeText(getContext(), R.string.empty_title, Toast.LENGTH_LONG).show();
                    return;
                }
                String[] isbnArray = originalIsbn.split(", ");
                for (String anIsbn : isbnArray) {
                    if(anIsbn.length() != 13) {
                        Toast.makeText(getContext(), R.string.isbnlength_warning, Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!TextUtils.isDigitsOnly(anIsbn)) {
                        Toast.makeText(getContext(), R.string.isbn_warning, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                int numOfCommasTitle = titlesString.length() - titlesString.replace(",", "").length();
                int numOfCommasAuthor = authorsString.length() - authorsString.replace(",", "").length();
                int numOfCommasIsbn = isbnString.length() - isbnString.replace(",", "").length();
                if(!((numOfCommasTitle == numOfCommasAuthor) && (numOfCommasAuthor == numOfCommasIsbn))) {
                    Toast.makeText(getContext(), R.string.titles_authors_unequal, Toast.LENGTH_LONG).show();
                    return;
                }
                addBooks(originalTitles.split(", "), originalAuthors.split(", "), isbnArray);
                titles.getText().clear();
                authors.getText().clear();
                isbn.getText().clear();
            }
        });
        return v;
    }
    private void addBooks(String[] titles, String[] authors, String[] isbns) {
        FirebaseApp.initializeApp(getContext());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .build();
        db.setFirestoreSettings(settings);
        assert user != null;
        Map<String, Object> emailMap = new HashMap<>();
        ArrayList<String> emails = Utilities.getEmails(getContext());
        if(!emails.contains(user.getEmail())) {
            emailMap.put("email", Objects.requireNonNull(user.getEmail()));
            DocumentReference emailLocation = db.collection("emails").document();
            emailLocation.set(emailMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            });
        }
        assert titles.length == authors.length && authors.length == isbns.length;
        for(int i = 0; i < titles.length; i++) {
            DocumentReference location = db.collection("allbooks").document();
            Listing book = new Listing(user.getDisplayName(), Arrays.asList(titles[i].trim(), authors[i].trim()), new Timestamp(new Date()), user.getEmail(), location.getId(),
                     Utilities.BUY, Utilities.getID(), Long.parseLong(isbns[i]));
            location.set(book).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            });
        }
        Toast.makeText(getContext(), "Added books.", Toast.LENGTH_LONG).show();
    }
}