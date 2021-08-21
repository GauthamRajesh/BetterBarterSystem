package com.gaucow.betterbartersystem.activities;

import android.os.Bundle;

import com.gaucow.betterbartersystem.R;
import com.gaucow.betterbartersystem.fragments.ViewSellersFragment;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ViewSellersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewsellers);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("View Sellers");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ViewSellersFragment fragment = new ViewSellersFragment();
        FirebaseApp.initializeApp(this);
        FirebaseAuth a = FirebaseAuth.getInstance();
        FirebaseUser u = a.getCurrentUser();
        assert u != null;
        getSupportFragmentManager().beginTransaction().replace(R.id.viewsellers_framelayout, fragment).commit();
    }
}
