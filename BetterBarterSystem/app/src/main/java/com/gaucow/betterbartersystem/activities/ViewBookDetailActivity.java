package com.gaucow.betterbartersystem.activities;

import android.os.Bundle;

import com.gaucow.betterbartersystem.R;
import com.gaucow.betterbartersystem.fragments.ViewBookDetailFragment;
import com.gaucow.betterbartersystem.models.Listing;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ViewBookDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewbookdetail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Book Details");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Listing detailListing = getIntent().getParcelableExtra("listing");
        Bundle b = new Bundle();
        b.putParcelable("listing", detailListing);
        ViewBookDetailFragment fragment = new ViewBookDetailFragment();
        fragment.setArguments(b);
        getSupportFragmentManager().beginTransaction().replace(R.id.viewbookdetail_framelayout, fragment).commit();
    }
}
