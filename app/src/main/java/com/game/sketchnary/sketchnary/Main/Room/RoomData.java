package com.game.sketchnary.sketchnary.Main.Room;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.game.sketchnary.sketchnary.R;


public class RoomData extends AppCompatActivity {
    private String RoomNumber;
    private String RoomCategory;
    private String CurrentPlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // gets the previously created intent

        Intent myIntent = getIntent();
        RoomNumber = myIntent.getStringExtra("RoomNumber");
        RoomCategory = myIntent.getStringExtra("RoomCategory");
        CurrentPlayers = myIntent.getStringExtra("CurrentPlayers");
        setTitle(RoomCategory);
        setContentView(R.layout.activity_room_data);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(this, Play.class);
                startActivity(intent);*/
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
