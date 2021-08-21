package com.gaucow.betterbartersystem.services;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import com.gaucow.betterbartersystem.R;
import com.gaucow.betterbartersystem.activities.TradeDetailsActivity;
import com.gaucow.betterbartersystem.activities.ViewSellersActivity;
import com.gaucow.betterbartersystem.models.Listing;
import com.gaucow.betterbartersystem.utilities.Person;
import com.gaucow.betterbartersystem.utilities.Utilities;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

public class TradeService extends IntentService {
    ArrayList<String> allISBNs;
    ArrayList<String> totalUserCombos;
    ArrayList<String> otherUserCombos;
    ArrayList<String> possibleExchanges;
    String complexOrSimple;
    PendingIntent pendingIntent;
    FirebaseUser user;
    ArrayList<String> emails;
    ArrayList<ArrayList<String>> exchangeIds;
    public TradeService() {
        super("TradeService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        FirebaseApp.initializeApp(getApplicationContext());
        complexOrSimple = Objects.requireNonNull(intent).getStringExtra("complexity");
        allISBNs = new ArrayList<>();
        totalUserCombos = new ArrayList<>();
        otherUserCombos = new ArrayList<>();
        possibleExchanges = new ArrayList<>();
        exchangeIds = new ArrayList<>();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        assert user != null;
        addToList(user.getEmail(), Utilities.USER_FLAG);
        emails = Utilities.getEmails(this);
        for(String email : emails) {
            if(!(email.equals(user.getEmail()))) {
                addToList(email, Utilities.OTHER_USER);
            }
        }
        ArrayList<Person> people = convertToPerson(otherUserCombos);
        for(int i = 0; i < totalUserCombos.size(); i++) {
            String trimmed = totalUserCombos.get(i).trim();
            String[] sellBuy = trimmed.split(" ");
            people.add(0, new Person(sellBuy[0], sellBuy[1]));
            bfs(people);
            people.remove(0);
        }
        createAndShowNotification();
    }
    private void bfs(ArrayList<Person> personList) {
        ArrayList<Integer>[] adj = new ArrayList[allISBNs.size()];
        for(int i = 0; i< allISBNs.size(); i++) {
            adj[i] = new ArrayList<>();
        }
        for(Person person : personList) {
            adj[allISBNs.indexOf(person.getWants())].add(allISBNs.indexOf(person.getSelling()));
        }
        int start = allISBNs.indexOf(personList.get(0).getSelling());
        int dest = allISBNs.indexOf(personList.get(0).getWants());
        HashSet<Integer> visited = new HashSet<>();
        ArrayDeque<ArrayList<Integer>> queue = new ArrayDeque<>();
        ArrayList<Integer> queueStart = new ArrayList<>();
        queueStart.add(start);
        queue.addLast(queueStart);
        ArrayList<Integer> answer = new ArrayList<>();
        while(!queue.isEmpty()) {
            ArrayList<Integer> nextList = queue.removeFirst();
            int next = nextList.get(nextList.size() - 1);
            if(visited.contains(next)) {
                continue;
            }
            visited.add(next);
            if(next == dest) {
                answer = nextList;
                break;
            }
            for(int i=0; i<adj[next].size(); i++) {
                ArrayList<Integer> adjList = new ArrayList<>(nextList);
                adjList.add(adj[next].get(i));
                queue.addLast(adjList);
            }
        }
        StringBuilder exchange = new StringBuilder();
        for(int num : answer) {
            exchange.append(allISBNs.get(num)).append(" -> ");
        }
        if(!exchange.toString().equals("")) {
            exchange = new StringBuilder(exchange.substring(0, exchange.length() - 3));
            try {
                exchangeIds.add(convertToIDs(exchange));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            String[] split = exchange.toString().split(" -> ");
            if (complexOrSimple.equals("complex") && split.length <= 4) {
                possibleExchanges.add(exchange.toString());
            } else if (complexOrSimple.equals("simple") && split.length == 2) {
                possibleExchanges.add(exchange.toString());
            }
        }
    }
    private ArrayList<Person> convertToPerson(ArrayList<String> combos) {
        ArrayList<Person> p = new ArrayList<>();
        for(String s : combos) {
            String trimmed = s.trim();
            String[] sellBuy = trimmed.split(" ");
            p.add(new Person(sellBuy[0], sellBuy[1]));
        }
        return p;
    }
    private void createAndShowNotification() {
        String textContent = getNotificationString();
        createNotificationChannel();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, String.valueOf(12))
                .setSmallIcon(R.drawable.trade_icon)
                .setContentTitle("Trade Status")
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_MAX);
        Intent intent;
        if(getNumberSelling() > 0 && possibleExchanges.isEmpty()) {
            intent = new Intent(this, ViewSellersActivity.class);
            try {
                intent.putParcelableArrayListExtra("wantedBooks", Utilities.getWantedBooks(user.getEmail()));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(intent);
            pendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            intent = new Intent(this, TradeDetailsActivity.class);
            Utilities.setIDs(exchangeIds);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        }
        mBuilder.setContentIntent(pendingIntent).setAutoCancel(true);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(12, mBuilder.build());
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Trade Notifications";
            String description = "The notification that tells you if you have books available to trade with.";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(String.valueOf(12), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
    }
    private int getNumberSelling() {
        final int[] number = {0};
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<Listing> listings = null;
        try {
            listings = Utilities.getWantedBooks(user.getEmail());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < Objects.requireNonNull(listings).size(); i++) {
            Query sellersQuery = db.collection("allbooks").whereEqualTo("bookType", 2)
                    .whereArrayContains("bookAndAuthorName", listings.get(i).getBookAndAuthorName().get(0));
            sellersQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots.getDocuments().size() != 0) {
                    number[0] += queryDocumentSnapshots.getDocuments().size();
                }

            });
        }
        return number[0];
    }
    private String getNotificationString() {
        String notifString;
        if(possibleExchanges.isEmpty() && getNumberSelling() == 0) {
            notifString = "There are no trades available, and unfortunately, no one is selling any of the books you want.";
        } else if(possibleExchanges.isEmpty() && getNumberSelling() == 1) {
            notifString = "There are no trades available, but 1 person is selling some or all of the books you want.";
        } else if(possibleExchanges.isEmpty() && getNumberSelling() > 1) {
            notifString = "There are no trades available, but " + getNumberSelling() + " people are selling some or all of the books you want.";
        } else {
            if (possibleExchanges.size() < 2) {
                notifString = "There is " + possibleExchanges.size() + " trade available to get some or all of the books you want.";
            } else {
                notifString = "There are " + possibleExchanges.size() + " trades available to get some or all of the books you want.";
            }
        }
        return notifString;
    }
    private void addToList(String email, final int flag) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .build();
        db.setFirestoreSettings(settings);
        Query userBuysQuery = db.collection("allbooks")
                .whereEqualTo("userEmail", email)
                .whereEqualTo("bookType", 1);
        Query userSellsQuery = db.collection("allbooks")
                .whereEqualTo("userEmail", email)
                .whereEqualTo("bookType", 2);
        ArrayList<String> allUserBuys = new ArrayList<>();
        ArrayList<String> allUserSells = new ArrayList<>();
        try {
            QuerySnapshot buys = Tasks.await(userBuysQuery.get());
            QuerySnapshot sells = Tasks.await(userSellsQuery.get());
            for(QueryDocumentSnapshot d : buys) {
                Listing s = d.toObject(Listing.class);
                String isbn = String.valueOf(s.getIsbn());
                if(!allISBNs.contains(isbn)) {
                    allISBNs.add(isbn);
                }
                allUserBuys.add(isbn);
            }
            for(QueryDocumentSnapshot d : sells) {
                Listing s = d.toObject(Listing.class);
                String isbn = String.valueOf(s.getIsbn());
                if(!allISBNs.contains(isbn)) {
                    allISBNs.add(isbn);
                }
                allUserSells.add(isbn);
            }
        } catch(ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<ArrayList<String>> lists = new ArrayList<>(Arrays.asList(allUserSells, allUserBuys));
        if(flag == Utilities.USER_FLAG) {
            Utilities.generatePermutations(lists, totalUserCombos, 0, "");
        } else {
            Utilities.generatePermutations(lists, otherUserCombos, 0, "");
        }
    }
    private ArrayList<String> convertToIDs(StringBuilder s) throws ExecutionException, InterruptedException {
        ArrayList<String> ids = new ArrayList<>();
        String[] exchanges = s.toString().trim().split(" -> ");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for(int i = 0; i < exchanges.length; i++) {
            if(i == 0) {
                Query q = db.collection("allbooks")
                        .whereEqualTo("userEmail", user.getEmail())
                        .whereEqualTo("bookType", 2)
                        .whereEqualTo("isbn", Long.parseLong(exchanges[i].trim()));
                QuerySnapshot snapshot = Tasks.await(q.get());
                for(QueryDocumentSnapshot d : snapshot) {
                    Listing l = d.toObject(Listing.class);
                    ids.add(String.valueOf(l.getBookID()));
                }
            } else {
                Query q = db.collection("allbooks")
                        .whereEqualTo("bookType", 2)
                        .whereEqualTo("isbn", Long.parseLong(exchanges[i].trim()));
                QuerySnapshot snapshot = Tasks.await(q.get());
                for(QueryDocumentSnapshot d : snapshot) {
                    if(snapshot.size() > 1) {
                        Listing l = d.toObject(Listing.class);
                        String email = l.getUserEmail();
                        ArrayList<Listing> listings = Utilities.getWantedBooks(email);
                        for(Listing listing : listings) {
                            if(String.valueOf(listing.getIsbn()).equals(exchanges[i])) {
                                ids.add(String.valueOf(listing.getBookID()));
                            }
                        }
                    } else if(snapshot.size() == 1) {
                        ids.add(String.valueOf(d.toObject(Listing.class).getBookID()));
                    }
                }
            }
        }
        assert ids.size() == exchanges.length;
        return ids;
    }
}