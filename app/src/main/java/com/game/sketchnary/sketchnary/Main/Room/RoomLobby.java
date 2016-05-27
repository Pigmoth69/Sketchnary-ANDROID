package com.game.sketchnary.sketchnary.Main.Room;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import static com.game.sketchnary.sketchnary.Authentication.LoginActivity.IP_ADRESS;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.game.sketchnary.sketchnary.Main.FindGameFragment;
import com.game.sketchnary.sketchnary.R;
import com.game.sketchnary.sketchnary.Connection.Https;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;



public class RoomLobby extends AppCompatActivity {
    private String RoomName;
    private SSLContext context;
    private JSONObject resData;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            String status = (String)message.obj;
            if(status.equals("Refresh")){
                Fragment fragment = new FindGameFragment();
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.contentFragment, fragment)
                        .commit();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // gets the previously created intent

        Intent myIntent = getIntent();
        RoomName = myIntent.getStringExtra("RoomName");
        setTitle(RoomName);
        setContentView(R.layout.activity_room_data);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        System.out.println("Carreguei no play!");
                        context = Https.httpStart(getAssets(), context);
                        String res = Https.httpJoinServer(context, "https://" + IP_ADRESS + "/api/room/?rooms=" + RoomName);
                        System.out.println("res: " + res);
                        try {
                            JSONObject o = new JSONObject(res);
                            String role = o.getString("role");
                            System.out.println("ROLE: "+role);
                            String hostname = o.getString("host");
                            System.out.println("HOST: "+hostname);
                            int port = o.getInt("port");
                            System.out.println("PORT: "+port);
                            if(role.equals("drawer")){
                                String word = o.getString("word");
                                System.out.println("WORD: "+word);
                            }else{
                                JSONArray jwords = o.getJSONArray("words");
                                ArrayList<String> words = new ArrayList<String>();
                                for(int i = 0; i < jwords.length();i++){
                                    JSONObject w = jwords.getJSONObject(i);
                                    words.add(w.getString(new Integer(i+1).toString()));
                                }
                                System.out.println("WRODS: "+words);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                /*Intent intent = new Intent(this, Play.class);
                startActivity(intent);*/
                    }
                }.start();
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

    /*private void joinServer() {
        final ProgressDialog progressDialog = new ProgressDialog(MainMenuActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Rooms");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Thread(){
            public void run(){
                rooms = httpJoinServer();
                Message message = mHandler.obtainMessage(1,"Refresh");
                message.sendToTarget();
                progressDialog.dismiss();
            }
        }.start();
    }*/
}
