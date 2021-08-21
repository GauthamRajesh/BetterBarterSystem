package com.gaucow.betterbartersystem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.gaucow.betterbartersystem.R;
import com.gaucow.betterbartersystem.fragments.ViewMyBooksFragment;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ViewMyBooksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewmybooks);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.mybooks_title));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ViewMyBooksFragment fragment = new ViewMyBooksFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.viewmybooks_framelayout, fragment).commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_viewbooks, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.add_label:
                startActivity(new Intent(this, BuySellListActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
