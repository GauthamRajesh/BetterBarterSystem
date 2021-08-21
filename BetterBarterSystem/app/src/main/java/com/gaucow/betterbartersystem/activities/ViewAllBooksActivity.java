package com.gaucow.betterbartersystem.activities;

import android.os.Bundle;

import com.gaucow.betterbartersystem.R;
import com.gaucow.betterbartersystem.fragments.ViewAllBooksFragment;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ViewAllBooksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewallbooks);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.allbooks_label));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ViewAllBooksFragment fragment = new ViewAllBooksFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.viewallbooks_framelayout, fragment).commit();
    }
}
