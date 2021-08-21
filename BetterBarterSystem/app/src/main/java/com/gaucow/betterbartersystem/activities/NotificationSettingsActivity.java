package com.gaucow.betterbartersystem.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gaucow.betterbartersystem.R;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class NotificationSettingsActivity extends AppCompatActivity {
    SharedPreferences preferences;
    private String time;
    private String complexity;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificationsettings);
        preferences = getSharedPreferences("notif", Context.MODE_PRIVATE);
        button = findViewById(R.id.saveButton);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Notification Settings");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setListener();
    }
    public void onSelected(View v) {
        switch(v.getId()) {
            case R.id.perHr:
                time = "hour";
                break;
            case R.id.perDay:
                time = "day";
                break;
            case R.id.complex:
                complexity = "complex";
                break;
            case R.id.simple:
                complexity = "simple";
                break;
        }
    }
    private void setListener() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(time == null || complexity == null) {
                    Toast.makeText(NotificationSettingsActivity.this, "You must answer the questions!", Toast.LENGTH_LONG).show();
                } else {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("time", time);
                    editor.putString("complexity", complexity);
                    editor.apply();
                    Toast.makeText(NotificationSettingsActivity.this, "Successful", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(NotificationSettingsActivity.this, MainActivity.class);
                    i.putExtra("backFromSettings", 1);
                    startActivity(i);
                }
            }
        });
    }
}
