package com.gaucow.betterbartersystem.activities;

import android.os.Bundle;

import com.gaucow.betterbartersystem.R;
import com.gaucow.betterbartersystem.fragments.BuyFragment;
import com.gaucow.betterbartersystem.fragments.SellFragment;
import com.gaucow.betterbartersystem.adapters.BuySellAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

public class BuySellListActivity extends AppCompatActivity {
    BuySellAdapter buySellAdapter;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbedlists);

        ViewPager v = findViewById(R.id.viewpager);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.createlist_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buySellAdapter = new BuySellAdapter(getSupportFragmentManager());

        setupViewPager(v);
    }
    private void setupViewPager(ViewPager viewPager) {
        BuySellAdapter adapter = new BuySellAdapter(getSupportFragmentManager());
        adapter.addFragment(new BuyFragment(), "Buy");
        adapter.addFragment(new SellFragment(), "Sell");
        viewPager.setAdapter(adapter);
    }
}
