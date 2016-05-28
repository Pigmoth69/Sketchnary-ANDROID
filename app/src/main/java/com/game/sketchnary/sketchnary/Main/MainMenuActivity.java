package com.game.sketchnary.sketchnary.Main;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.game.sketchnary.sketchnary.Connection.Https;
import com.game.sketchnary.sketchnary.Main.Room.Room;
import com.game.sketchnary.sketchnary.Main.Room.RoomLobby;
import com.game.sketchnary.sketchnary.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import static com.game.sketchnary.sketchnary.Authentication.LoginActivity.IP_ADRESS;

public class MainMenuActivity extends AppCompatActivity
implements NavigationView.OnNavigationItemSelectedListener, FindGameFragment.OnHeadlineSelectedListener{
    public static ArrayList<Room> rooms = new ArrayList<Room>();
    private SSLContext context;


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {

            if(message.obj instanceof String){
                String status = (String)message.obj;
                if(status.equals("Refresh")){
                    Fragment fragment = new FindGameFragment();
                    // Insert the fragment by replacing any existing fragment
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.contentFragment, fragment)
                            .commit();
                }else{
                    String awnser = (String)message.obj;
                    Toast.makeText(getBaseContext(), awnser, Toast.LENGTH_LONG).show();
                }
            }else if(message.obj instanceof Room){
                Intent intent = new Intent(MainMenuActivity.this, RoomLobby.class);
                intent.putExtra("RoomName",((Room)message.obj).getRoomName());
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //setNavHeaderMainMenu();
        //Making login on the game

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadGameRooms();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        if (savedInstanceState == null){
            loadGameRooms();
        }

    }

    /*Esta função não funciona não sei porquê*/
    private void setNavHeaderMainMenu() {
        TextView name = (TextView)findViewById(R.id.nameID);
        TextView username = (TextView)findViewById(R.id.usernameID);
        TextView email = (TextView)findViewById(R.id.emailID);
        TextView birthday = (TextView)findViewById(R.id.birthdayID);
        TextView points = (TextView)findViewById(R.id.pointsID);

        name.setText("Welcome, "+this.getIntent().getStringExtra("name")+"!");
        username.setText("Username: "+this.getIntent().getStringExtra("username"));
        email.setText("Email: "+this.getIntent().getStringExtra("email"));
        birthday.setText("Birthdate: "+this.getIntent().getStringExtra("birthday"));
        points.setText("Total points: "+this.getIntent().getStringExtra("points"));

    }

    private void loadGameRooms() {
        final ProgressDialog progressDialog = new ProgressDialog(MainMenuActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Rooms");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Thread(){
            public void run(){
                rooms = makeRequest();
                Message message = mHandler.obtainMessage(1,"Refresh");
                message.sendToTarget();
                progressDialog.dismiss();
            }
        }.start();
    }

    private ArrayList<Room> makeRequest() {
        ArrayList<Room> res = new ArrayList<Room>();
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

            URL url = new URL("https://" + IP_ADRESS + "/api/room/?rooms=all");
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setConnectTimeout(5000);
            InputStream in = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject rooms;


            rooms = new JSONObject(sb.toString());

            JSONArray array = rooms.getJSONArray("rooms");
            for (int i = 0; i < array.length(); i++) {
                String room = array.getJSONObject(i).getString("room");
                ArrayList<String> playersEmail = new ArrayList<String>();
                JSONArray players = array.getJSONObject(i).getJSONArray("players");
                for (int x = 0; x < players.length(); x++) {
                    String player_email = players.getJSONObject(x).getString(new Integer(x).toString());
                    playersEmail.add(player_email);
                }
                res.add(new Room(room,playersEmail));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return res;

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_NewGame) {
            // Handle the camera action
            Fragment fragment = new FindGameFragment();
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.contentFragment, fragment)
                    .commit();
        } else if (id == R.id.nav_FindGame) {

        } else if (id == R.id.nav_Friends) {

        } else if (id == R.id.nav_GameOptions) {

        } else if (id == R.id.nav_Share) {

        } else if (id == R.id.nav_Settings) {

        }else if (id == R.id.nav_Logout) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void onArticleSelected(String roomName){
        System.out.println("Room name: "+roomName);
        //mandar pedido de entrada na sala ao servidor!
        enterRoom(roomName);

    }

    private void enterRoom(String roomName) {
        final ProgressDialog progressDialog = new ProgressDialog(MainMenuActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Entering room!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        final String RoomName = roomName;
        new Thread() {
            public void run() {
                context = Https.httpStart(getAssets(), context);
                String res = Https.httpJoinServerGET(context, "https://" + IP_ADRESS + "/api/room/?room=" + RoomName);
                System.out.println("res: " + res);
                try {
                    JSONObject o = new JSONObject(res);
                    String status = o.getString("status");

                    if(status.equals("ok")){
                        Message message = mHandler.obtainMessage(1,new Room(RoomName,null));
                        message.sendToTarget();
                    }else{
                        Message message = mHandler.obtainMessage(1,"Cannot connect room!");
                        message.sendToTarget();
                    }

                    progressDialog.dismiss();

                } catch (JSONException e) {
                    //e.printStackTrace();
                    Message message = mHandler.obtainMessage(1,"Cannot connect room!");
                    message.sendToTarget();
                }
            }
        }.start();
    }
}
