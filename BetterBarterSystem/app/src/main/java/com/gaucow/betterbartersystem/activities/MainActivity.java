package com.gaucow.betterbartersystem.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.gaucow.betterbartersystem.R;
import com.gaucow.betterbartersystem.services.TradeBootReceiver;
import com.gaucow.betterbartersystem.services.TradeReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    Button createLists;
    Button viewMyBooks;
    Button viewAllBooks;
    Button showSellers;
    Button notifSettings;
    Button startSearch;
    TextView tradeStarted;
    SharedPreferences prefs;
    long frequency = 0;
    String toastString;
    Button userGuide;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.hometitle));
        setSupportActionBar(toolbar);

        FirebaseApp.initializeApp(this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        viewMyBooks = findViewById(R.id.viewmybooks);
        createLists = findViewById(R.id.createlist);
        viewAllBooks = findViewById(R.id.viewbooks);
        showSellers = findViewById(R.id.showSellers);
        notifSettings = findViewById(R.id.notificationSettings);
        startSearch = findViewById(R.id.startTradeSearch);
        tradeStarted = findViewById(R.id.tradeServiceRunning);
        userGuide = findViewById(R.id.userGuide);
        prefs = getSharedPreferences("notif", Context.MODE_PRIVATE);

        assert mUser != null;

        ifBackFromNotifSettings();

        ifAlarmIsRunning();

        checkIfListsExist();

        createLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BuySellListActivity.class));
            }
        });
        viewMyBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ViewMyBooksActivity.class));
            }
        });
        viewAllBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ViewAllBooksActivity.class));
            }
        });
        notifSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NotificationSettingsActivity.class));
            }
        });
        startSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTradeSearch();
                startSearch.setVisibility(View.GONE);
            }
        });
        showSellers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ViewSellersActivity.class));
            }
        });
        userGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserGuideActivity.class));
            }
        });
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, R.string.signedout_text, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), SignIn.class));
                    }
                });

    }

    public void checkIfListsExist() {
        FirebaseApp.initializeApp(this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .build();
        db.setFirestoreSettings(settings);
        assert mUser != null;
        Query booksQuery = db.collection("allbooks").whereEqualTo("userEmail", mUser.getEmail());
        booksQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                if (list.size() > 0) {
                    viewMyBooks.setVisibility(View.VISIBLE);
                    createLists.setVisibility(View.GONE);
                    showSellers.setVisibility(View.VISIBLE);
                    if (prefs.getString("time", "").equals("") || prefs.getString("complexity", "").equals("")) {
                        notifSettings.setVisibility(View.VISIBLE);
                    }
                } else {
                    notifSettings.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mainactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_label:
                signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void ifBackFromNotifSettings() {
        if (getIntent().getIntExtra("backFromSettings", 0) == 1) {
            notifSettings.setVisibility(View.GONE);
            startSearch.setVisibility(View.VISIBLE);
        }
    }
    private void startTradeSearch() {
        notifSettings.setVisibility(View.GONE);
        Intent intent = new Intent(getApplicationContext(), TradeReceiver.class);
        intent.setAction(TradeReceiver.ACTION);
        String complexOrSimple = prefs.getString("complexity", "");
        intent.putExtra("complexity", complexOrSimple);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        String timeString = getSharedPreferences("notif", Context.MODE_PRIVATE).getString("time", "");
        if (timeString.equals("hour")) {
            frequency = AlarmManager.INTERVAL_HOUR;
            toastString = "once every hour.";
        } else if (timeString.equals("day")) {
            frequency = AlarmManager.INTERVAL_DAY;
            toastString = "once every day.";
        }
        Objects.requireNonNull(alarm).setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() +
                        frequency, frequency, pendingIntent);
        tradeStarted.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Started looking for trades for your wanted books. The app will notify you " + toastString, Toast.LENGTH_LONG).show();
        ComponentName receiver = new ComponentName(this, TradeBootReceiver.class);
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
    public void ifAlarmIsRunning() {
        Intent intent = new Intent(this, TradeReceiver.class);
        intent.setAction(TradeReceiver.ACTION);
        boolean isWorking = (PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_NO_CREATE) != null);
        if(!isWorking) {
            notifSettings.setVisibility(View.VISIBLE);
        } else {
            tradeStarted.setVisibility(View.VISIBLE);
        }
    }
}