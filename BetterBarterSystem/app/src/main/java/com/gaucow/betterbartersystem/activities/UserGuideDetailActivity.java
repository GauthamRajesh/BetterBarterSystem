package com.gaucow.betterbartersystem.activities;

import android.os.Bundle;

import com.gaucow.betterbartersystem.R;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class UserGuideDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userguidedetail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Detailed User Guide");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }
}
