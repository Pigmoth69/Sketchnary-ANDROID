package com.game.sketchnary.sketchnary.Main.Room.Game;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.widget.Toast;

import com.game.sketchnary.sketchnary.Connection.Https;
import com.game.sketchnary.sketchnary.Main.GameData;
import com.game.sketchnary.sketchnary.Main.Room.Draw.DrawingView;
import com.game.sketchnary.sketchnary.Main.Room.Draw.SpectatingView;
import com.game.sketchnary.sketchnary.Main.Room.RoomLobby;
import com.game.sketchnary.sketchnary.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import static com.game.sketchnary.sketchnary.Authentication.LoginActivity.IP_ADRESS;

public class Expectate extends AppCompatActivity {
    SpectatingView dv ;
    private Paint mPaint= new Paint();
    private SSLContext context;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {

            if(message.obj instanceof String){
                String word = (String)message.obj;
                Toast.makeText(getBaseContext(), word, Toast.LENGTH_LONG).show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dv = new SpectatingView(this,mPaint);
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

        new Thread(){
            public void run(){
                try {
                    while(true) {
                        String res = RoomLobby.getClient().receive();
                        new GetPointToDraw(res).execute();
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                }

            }
        }.start();

    }

    private class GetPointToDraw extends AsyncTask<Void, Void, Void> {
        private String res;

        public GetPointToDraw(String res) {
            this.res = res;
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
        protected void onPostExecute(Void result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dv.drawOnScreen(res);
                }
            });

        }
    }


    long timer = 60000;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play, menu);

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
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.ic_brush_24dp);
        builderSingle.setTitle("Select the word:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.select_dialog_singlechoice);
        //As palabras que se têm de adicionar!
        int n = this.getIntent().getIntExtra("nWords",0);
        ArrayList<String> w = new ArrayList<String>();
        for(int i = 0; i < n; i++)
            w.add(this.getIntent().getStringExtra("word"+i));

        long seed = System.nanoTime();
        Collections.shuffle(w, new Random(seed));

        for(int i = 0;i< n;i++)
            arrayAdapter.add(w.get(i));

        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        final Object context = this;
        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String strName = arrayAdapter.getItem(which);
                        final AlertDialog.Builder builderInner = new AlertDialog.Builder((Context) context);
                        builderInner.setMessage(strName);
                        builderInner.setTitle("Your Selected Item is");
                        builderInner.setPositiveButton(
                                "Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        sendAwnser(strName);
                                        dialog.dismiss();
                                    }
                                });
                        builderInner.setNegativeButton("CANCEL",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builderInner.show();
                    }
                });
        builderSingle.show();
    }

    private void sendAwnser(final String strName) {
        final ProgressDialog progressDialog = new ProgressDialog(Expectate.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Saving awnser!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        new Thread() {
            public void run() {
                context = Https.httpStart(getAssets(), context);
                String s = new String("{\"room\":"+getIntent().getStringExtra("roomName")+",\"word\":"+strName+"}");
                String res = Https.httpJoinServerPOST(context, "https://" + IP_ADRESS + "/api/game",s);

                try {
                    JSONObject o = new JSONObject(res);
                    String status = o.getString("status");
                    if(status.equals("ok")){
                        Message message = mHandler.obtainMessage(1,"Word saved!");
                        message.sendToTarget();
                    }else{
                        Message message = mHandler.obtainMessage(1,"Try again! Word not saved");
                        message.sendToTarget();
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}


