package com.gaucow.betterbartersystem.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gaucow.betterbartersystem.R;
import com.gaucow.betterbartersystem.models.Listing;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ViewBookDetailFragment extends Fragment {
    private TextView titleWanted;
    private TextView authorWanted;
    private StringBuilder titlesWanted;
    private StringBuilder authorsWanted;
    private Listing listing;
    private FirebaseUser mUser;
    private StringBuilder titlesUserHas;
    private StringBuilder authorsUserHas;
    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        assert getArguments() != null;
        listing = getArguments().getParcelable("listing");
        View v = inflater.inflate(R.layout.fragment_viewbookdetail, container, false);
        assert listing != null;
        titleWanted = v.findViewById(R.id.titleWanted);
        authorWanted = v.findViewById(R.id.authorWanted);
        TextView titleOffering = v.findViewById(R.id.titleOffering);
        TextView authorOffering = v.findViewById(R.id.authorOffering);
        Button contactButton = v.findViewById(R.id.contact);
        titleOffering.setText(String.format("Title of Book Offering: %s (ISBN = %d)", listing.getBookAndAuthorName().get(0), listing.getIsbn()));
        authorOffering.setText(String.format("Author of Book Offering: %s", listing.getBookAndAuthorName().get(1)));
        FirebaseApp.initializeApp(getContext());
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if(mUser == null) {
            Toast.makeText(getContext(), "Not authorized!", Toast.LENGTH_SHORT).show();
            return v;
        }
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .build();
        db.setFirestoreSettings(settings);
        Query detailQuery = db.collection("allbooks").whereEqualTo("bookType", 1).whereEqualTo("userEmail", listing.getUserEmail());
        Query whatUserHasQuery = db.collection("allbooks").whereEqualTo("bookType", 2).whereEqualTo("userEmail", mUser.getEmail());
        detailQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                titlesWanted = new StringBuilder();
                authorsWanted = new StringBuilder();
                for(QueryDocumentSnapshot d : queryDocumentSnapshots) {
                    Listing l = d.toObject(Listing.class);
                    titlesWanted.append(l.getBookAndAuthorName().get(0)).append(" (ISBN = ").append(l.getIsbn())
                            .append(")").append(", ");
                    authorsWanted.append(l.getBookAndAuthorName().get(1)).append(", ");
                }
                if (titlesWanted.length() >= 2 && authorsWanted.length() >= 2) {
                    titlesWanted = new StringBuilder(titlesWanted.substring(0, titlesWanted.length() - 2));
                    authorsWanted = new StringBuilder(authorsWanted.substring(0, authorsWanted.length() - 2));
                    titleWanted.setText(String.format("Titles of Books Wanted: %s", titlesWanted.toString()));
                    authorWanted.setText(String.format("Authors of Books Wanted: %s", authorsWanted.toString()));
                }
            }
        });
        whatUserHasQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                titlesUserHas = new StringBuilder();
                authorsUserHas = new StringBuilder();
                for(QueryDocumentSnapshot d : queryDocumentSnapshots) {
                    Listing l = d.toObject(Listing.class);
                    titlesUserHas.append(l.getBookAndAuthorName().get(0)).append(" (ISBN =").append(l.getIsbn())
                            .append(")").append(", ");
                    authorsUserHas.append(l.getBookAndAuthorName().get(1)).append(", ");
                }
            }
        });
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeEmail(titlesUserHas, authorsUserHas);
            }
        });
        return v;
    }
    private void composeEmail(StringBuilder titles, StringBuilder authors) {
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setData(Uri.parse("mailto:"));
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{listing.getUserEmail()});
        i.putExtra(Intent.EXTRA_SUBJECT, "Requesting a trade for your book: " + listing.getBookAndAuthorName()
                .get(0)
         + " (ISBN = " + listing.getIsbn() + ") " + " by " + listing.getBookAndAuthorName().get(1));
        StringBuilder booksOffering = new StringBuilder();
        String[] titlesOffering = titles.toString().trim().split(",");
        String[] authorsOffering = authors.toString().trim().split(",");
        assert titlesOffering.length == authorsOffering.length;
        int j = 0;
        for (String title : titlesOffering) {
            String author = authorsOffering[j];
            if(!(titlesOffering.length < 2)) {
                if(j == titlesOffering.length - 1) {
                    booksOffering.append("and ").append(title).append(" by ").append(author).append(".");
                } else {
                    booksOffering.append(title).append(" by ").append(author).append(", ");
                }
                j++;
            } else {
                booksOffering.append(title).append(" by ").append(author).append(".");
            }
        }
        booksOffering = new StringBuilder(booksOffering.substring(0, booksOffering.length() - 2))
            .append(".");
        String body = "Hello " + listing.getName() + ",\n\n" + "I am interested in your book and I want " +
                "to trade for it. I am offering these book(s): \n\n" + booksOffering.toString() + "\n\nThanks,\n" +
                mUser.getDisplayName();
        i.putExtra(Intent.EXTRA_TEXT, body);
        if(i.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivity(i);
        }
    }
}
