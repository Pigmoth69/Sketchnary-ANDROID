package com.game.sketchnary.sketchnary.Main.Room;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.game.sketchnary.sketchnary.Main.Room.Draw.DrawingView;
import com.game.sketchnary.sketchnary.R;

import java.util.concurrent.TimeUnit;

public class Play extends AppCompatActivity {
    DrawingView dv ;
    private Paint mPaint= new Paint();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dv = new DrawingView(this,mPaint);
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



        RelativeLayout item = (RelativeLayout)findViewById(R.id.relLayoutID);
        item.addView(dv);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        dv.drawPoint(1,1);
    }
    long timer = 65000;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play, menu);

        //mudar isto... Jesus, que cancro
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
            }
        }.start();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.guess_word:
                showGuessDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showGuessDialog() {

    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ArrayAdapter<CharSequence> adapter = null;
        adapter.add("daniel1");
        adapter.add("daniel2");
        adapter.add("daniel3");
        adapter.add("daniel4");

       /* AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a Country")
                .setItems(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });
        return builder.create();*/
        
    }


}


