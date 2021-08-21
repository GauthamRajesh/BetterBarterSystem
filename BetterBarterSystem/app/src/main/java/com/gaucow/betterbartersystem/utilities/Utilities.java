package com.gaucow.betterbartersystem.utilities;

import android.content.Context;
import android.os.AsyncTask;

import com.gaucow.betterbartersystem.models.Listing;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Utilities {
    public static final int BUY = 1;
    public static final int SELL = 2;
    private static final long LIMIT = 10000000000L;
    private static long last = 0;
    public static final int USER_FLAG = 12;
    public static final int OTHER_USER = 11;
    private static ArrayList<String> emails = new ArrayList<>();
    private static ArrayList<ArrayList<String>> exchangeIds = new ArrayList<>();
    public static long getID() {
        long id = System.currentTimeMillis() % LIMIT;
        if (id <= last) {
            id = (last + 1) % LIMIT;
        }
        return last = id;
    }
    public static ArrayList<String> getEmails(Context c) {
        FirebaseApp.initializeApp(c);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .build();
        db.setFirestoreSettings(settings);
        Query q = db.collection("emails");
        new getEmailsAsyncTask().execute(q);
        return emails;
    }
    public static void generatePermutations(ArrayList<ArrayList<String>> lists, ArrayList<String> result, int depth, String current) {
        if(depth == lists.size()) {
            result.add(current);
            return;
        }
        for(int i = 0; i < lists.get(depth).size(); ++i) {
            generatePermutations(lists, result, depth + 1, current + lists.get(depth).get(i) + " ");
        }
    }
    public static ArrayList<Listing> getWantedBooks(String email) throws ExecutionException, InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<Listing> listings = new ArrayList<>();
        Query wantedBooksQuery = db.collection("allbooks").whereEqualTo("userEmail", email)
                .whereEqualTo("bookType", 1);
        QuerySnapshot q = Tasks.await(wantedBooksQuery.get());
        for(QueryDocumentSnapshot d : q) {
            Listing l = d.toObject(Listing.class);
            listings.add(l);
        }
        return listings;
    }
    public static void setIDs(ArrayList<ArrayList<String>> lists) {
        exchangeIds = lists;
    }
    public static ArrayList<ArrayList<String>> getIDs() {
        return exchangeIds;
    }
    private static class getEmailsAsyncTask extends AsyncTask<Query, Void, Void> {
        @Override
        protected Void doInBackground(Query... queries) {
            try {
                QuerySnapshot queryDocumentSnapshots = Tasks.await(queries[0].get());
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String email = (String) documentSnapshot.get("email");
                    emails.add(email);
                }
            } catch(ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
