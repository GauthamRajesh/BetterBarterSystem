package com.gaucow.betterbartersystem.activities;

import android.os.Bundle;

import com.gaucow.betterbartersystem.R;
import com.gaucow.betterbartersystem.fragments.ViewSellerDetailFragment;
import com.gaucow.betterbartersystem.models.Listing;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ViewSellerDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewsellerdetail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Seller Details");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Listing detailListing = getIntent().getParcelableExtra("listing");
        Bundle b = new Bundle();
        b.putParcelable("listing", detailListing);
        ViewSellerDetailFragment fragment = new ViewSellerDetailFragment();
        fragment.setArguments(b);
        getSupportFragmentManager().beginTransaction().replace(R.id.viewsellerdetail_framelayout, fragment).commit();
    }
}
