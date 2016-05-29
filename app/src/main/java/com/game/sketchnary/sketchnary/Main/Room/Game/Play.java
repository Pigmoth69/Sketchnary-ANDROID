package com.game.sketchnary.sketchnary.Main.Room.Game;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import com.game.sketchnary.sketchnary.Main.Room.Draw.DrawingView;
import com.game.sketchnary.sketchnary.R;

import java.util.concurrent.TimeUnit;

public class Play extends AppCompatActivity {
    private DrawingView dv ;
    private Paint mPaint= new Paint();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dv = new DrawingView(this, mPaint);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
        setContentView(R.layout.activity_play);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        final RelativeLayout item = (RelativeLayout) findViewById(R.id.relLayoutID);
        item.addView(dv);

    }
    long timer = 60000;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_guess, menu);

        final MenuItem  counter = menu.findItem(R.id.counter);
        new CountDownTimer(timer, 1000) {

            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                String  hms =  ((TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)))+":"+ (TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));

                counter.setTitle("Time left: "+hms);
                timer = millis;

            }

            public void onFinish() {
                counter.setTitle("done!");
                Intent i = new Intent();
                setResult(RESULT_OK, i);
                finish();
            }
        }.start();

        final MenuItem  wordItem = menu.findItem(R.id.drawingword);
        String word = getIntent().getStringExtra("word");
        wordItem.setTitle(word);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.guess_word:
                //showGuessDialog();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}


