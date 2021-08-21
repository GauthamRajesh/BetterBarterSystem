package com.gaucow.betterbartersystem.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gaucow.betterbartersystem.R;
import com.gaucow.betterbartersystem.models.Listing;
import com.gaucow.betterbartersystem.utilities.Utilities;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TradeDetailsActivity extends AppCompatActivity {
    ArrayList<ArrayList<String>> exchanges;
    LinearLayout layout;
    TextView tradesLabel;
    TextView emptyExchange;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tradedetails);
        exchanges = Utilities.getIDs();
        layout = findViewById(R.id.mainContent);
        tradesLabel = findViewById(R.id.tradesLabel);
        emptyExchange = findViewById(R.id.emptyExchange);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Trade Details");
        setSupportActionBar(toolbar);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 15;
        params.rightMargin = 15;
        params.topMargin = 15;
        params.bottomMargin = 15;
        int j = 1;
        if(!exchanges.isEmpty()) {
            for(ArrayList<String> exchange : exchanges) {
                TextView tradeDisplay = new TextView(this);
                tradeDisplay.setText(MessageFormat.format("Book Trade {0}:", j));
                tradeDisplay.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
                tradeDisplay.setTextSize(20);
                layout.addView(tradeDisplay, params);
                StringBuilder initText = new StringBuilder("Trade with Seller ");
                for(int i = 1; i < exchange.size(); i++) {
                    initText.append(i).append(" -> Seller ");
                }
                String finalText = initText.substring(0, initText.length() - 11);
                TextView tv = new TextView(this);
                tv.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
                tv.setText(finalText);
                tv.setTextSize(20);
                layout.addView(tv, params);
                for (int i = 1; i < exchange.size(); i++) {
                    setUpButton(i, exchange);
                }
                j++;
            }
        } else {
            tradesLabel.setVisibility(View.GONE);
            emptyExchange.setVisibility(View.VISIBLE);
        }
    }
    private void setUpButton(int numSeller, ArrayList<String> exchange) {
        FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .build();
        db.setFirestoreSettings(settings);
        Query idQuery = db.collection("allbooks")
                .whereEqualTo("bookID", Long.parseLong(exchange.get(numSeller)));
        new FetchDataAsyncTask().execute(idQuery);
        Button b = new Button(this);
        try {
            Listing idListing = new FetchDataAsyncTask().execute(idQuery).get();
            setUpListener(b, idListing, numSeller, exchange, db);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    private static class FetchDataAsyncTask extends AsyncTask<Query, Void, Listing> {
        @Override
        protected Listing doInBackground(Query... queries) {
            Query q = queries[0];
            QuerySnapshot snapshot = null;
            try {
                 snapshot = Tasks.await(q.get());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            assert snapshot != null;
            final Listing[] listing = new Listing[1];
            for(QueryDocumentSnapshot d : snapshot) {
                listing[0] = d.toObject(Listing.class);
            }
            return listing[0];
        }
    }
    public void setUpListener(Button button, final Listing listing, int numSeller, ArrayList<String> ids, FirebaseFirestore db) throws ExecutionException, InterruptedException {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser mUser = mAuth.getCurrentUser();
        button.setText(MessageFormat.format("Contact Seller {0}", numSeller));
        String id = ids.get(numSeller - 1).trim();
        Query userQuery = db.collection("allbooks").whereEqualTo("bookID", Long.parseLong(id));
        final Listing l = new FetchDataAsyncTask().execute(userQuery).get();
        final String book = l.getBookAndAuthorName().get(0) + " by " + l.getBookAndAuthorName().get(1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setData(Uri.parse("mailto:"));
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{listing.getUserEmail()});
                i.putExtra(Intent.EXTRA_SUBJECT, "Requesting a trade for your book " + listing.getBookAndAuthorName()
                        .get(0)
                        + " by " + listing.getBookAndAuthorName().get(1) + " (isbn = " + listing.getIsbn() + ")");
                String body = "Hello " + listing.getName() + ",\n\n" + "I am interested in your book and I want " +
                        "to trade for it. I am offering this book: " + book + " (isbn = " + l.getIsbn() + ")." + "\n\nThanks,\n" +
                        Objects.requireNonNull(mUser).getDisplayName();
                i.putExtra(Intent.EXTRA_TEXT, body);
                if(i.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                    startActivity(i);
                }
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 15;
        params.rightMargin = 15;
        params.topMargin = 15;
        params.bottomMargin = 15;
        layout.addView(button, params);
    }
}