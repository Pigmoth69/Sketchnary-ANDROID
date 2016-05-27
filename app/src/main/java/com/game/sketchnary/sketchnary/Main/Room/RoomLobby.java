package com.game.sketchnary.sketchnary.Main.Room;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.game.sketchnary.sketchnary.Main.FindGameFragment;
import com.game.sketchnary.sketchnary.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;


public class RoomLobby extends AppCompatActivity {
    private String RoomName;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            String status = (String)message.obj;
            if(status.equals("Refresh")){
                /*Fragment fragment = new FindGameFragment();
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.contentFragment, fragment)
                        .commit();*/
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
                System.out.println("Carreguei no play!");
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

    private void joinServer() {
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
    }

    public void httpJoinServer(){
            String res = "Server error...Try again later!";
            try {
                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(getAssets().open("Keys/truststore.bks"), "123456".toCharArray());

                // Create a TrustManager that trusts the CAs in our KeyStore
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);

                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, tmf.getTrustManagers(), null);

                URL url = new URL("https://"+IP_ADRESS+"/api/room/?rooms="+RoomName);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                urlConnection.setConnectTimeout(15000);
                InputStream in = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader( new InputStreamReader(in )  );
                String line = null;
                StringBuilder sb = new StringBuilder();
                while( ( line = reader.readLine() ) != null )  {
                    sb.append(line);
                }

                JSONObject serverAwnser;
                System.out.println("String: "+sb.toString());
                serverAwnser = new JSONObject(sb.toString());
                String status = serverAwnser.getString("status");
                if(status.equals("ok")){
                    res=status;
                    resData = serverAwnser;
                }else if(status.equals("error")){
                    res = serverAwnser.getString("reason");
                }
                System.out.println("REASON: "+res);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }
    }
}
