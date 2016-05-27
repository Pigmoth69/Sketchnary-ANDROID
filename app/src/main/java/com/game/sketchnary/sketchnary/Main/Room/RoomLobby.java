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

import com.game.sketchnary.sketchnary.Connection.TCPClient;
import com.game.sketchnary.sketchnary.Main.FindGameFragment;
import com.game.sketchnary.sketchnary.Main.GameData;
import com.game.sketchnary.sketchnary.Main.Room.Game.Expectate;
import com.game.sketchnary.sketchnary.Main.Room.Game.Play;
import com.game.sketchnary.sketchnary.R;
import com.game.sketchnary.sketchnary.Connection.Https;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import javax.net.ssl.SSLContext;



public class RoomLobby extends AppCompatActivity {
    private String RoomName;
    private SSLContext context;
    private JSONObject resData;
    private static final int ENDGAME_STATUS = 0;
    private static TCPClient client=null;

    public static TCPClient getClient() {
        return client;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {

            //tratar de dados com Strings!
            if(message.obj instanceof String){
                String status = (String)message.obj;
                Toast.makeText(getBaseContext(), status, Toast.LENGTH_LONG).show();
            }else if(message.obj instanceof GameData){
                GameData gamedata = (GameData)message.obj;
                joinServers(gamedata);
            }

        }
    };

    private void joinServers(final GameData gamedata) {
        final ProgressDialog progressDialog = new ProgressDialog(RoomLobby.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Joining game!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final GameData threadData = gamedata;
        new Thread(){
            public void run(){
                try {
                    client = new TCPClient(threadData.getHost(),threadData.getPort());
                    String status = client.receive();
                    if(status.equals("start") && gamedata.getWords()==null) {
                        System.out.println("ESTOU AQUI1");
                        Intent intent = new Intent(RoomLobby.this, Play.class);
                        //MANDAR AS PALAVRAS E OS DADOS NECESSÀRIOS PARA A NOVA ATIVIDADE
                        startActivity(intent);
                    }else if(status.equals("start") && gamedata.getWords() !=null) {
                        System.out.println("ESTOU AQUI2");
                        Intent intent = new Intent(RoomLobby.this, Expectate.class);
                        //MANDAR AS PALAVRAS E OS DADOS NECESSÀRIOS PARA A NOVA ATIVIDADE
                        intent.putExtra("nWords",gamedata.getWords().size());
                        intent.putExtra("roomName",RoomName);
                        for(int i = 0;i< gamedata.getWords().size();i++){
                            intent.putExtra("word"+i,gamedata.getWords().get(i));
                        }
                        startActivityForResult(intent,ENDGAME_STATUS);
                    }else{
                        System.out.println("ESTOU AQUI3");
                        Message message = mHandler.obtainMessage(1,status);
                        message.sendToTarget();
                    }
                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("ENTREI NO ONACTIVITYREULT!1");
        if (requestCode == ENDGAME_STATUS) {
            System.out.println("ENTREI NO ONACTIVITYREULT!2");
            if (resultCode == RESULT_OK) {
                System.out.println("ENTREI NO ONACTIVITYREULT!3");
                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                RefreshGameRoom();
            }
        }
    }

    private void RefreshGameRoom() {
        final ProgressDialog progressDialog = new ProgressDialog(RoomLobby.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Waiting for results!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

       /* new Thread() {
            public void run() {
                context = Https.httpStart(getAssets(), context);
                String res = Https.httpJoinServerGET(context, "https://" + IP_ADRESS + "/api/room/?rooms=" + RoomName);
                System.out.println("res: " + res);
                try {
                    JSONObject o = new JSONObject(res);
                    String role = o.getString("role");
                    String host = o.getString("host");
                    int port = o.getInt("port");
                    GameData gamedata;
                    if(role.equals("drawer")){
                        String word = o.getString("word");
                        gamedata = new GameData(role,host,port,word);
                    }else{
                        JSONArray jwords = o.getJSONArray("words");
                        ArrayList<String> words = new ArrayList<String>();
                        for(int i = 0; i < jwords.length();i++){
                            JSONObject w = jwords.getJSONObject(i);
                            words.add(w.getString(new Integer(i+1).toString()));
                        }
                        gamedata= new GameData(role,host,port,words);
                    }
                    Message message = mHandler.obtainMessage(1,gamedata);
                    message.sendToTarget();
                    progressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(this, Play.class);
                startActivity(intent);
            }
        }.start();*/
    }

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
                loadServerData();

            }
        });
    }

    private void loadServerData() {
        final ProgressDialog progressDialog = new ProgressDialog(RoomLobby.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Getting game!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        new Thread() {
            public void run() {
                System.out.println("Carreguei no play!");
                context = Https.httpStart(getAssets(), context);
                String res = Https.httpJoinServerGET(context, "https://" + IP_ADRESS + "/api/room/?rooms=" + RoomName);
                System.out.println("res: " + res);
                try {
                    JSONObject o = new JSONObject(res);
                    String role = o.getString("role");
                    String host = o.getString("host");
                    int port = o.getInt("port");
                    GameData gamedata;
                    if(role.equals("drawer")){
                        String word = o.getString("word");
                        gamedata = new GameData(role,host,port,word);
                    }else{
                        JSONArray jwords = o.getJSONArray("words");
                        ArrayList<String> words = new ArrayList<String>();
                        for(int i = 0; i < jwords.length();i++){
                            JSONObject w = jwords.getJSONObject(i);
                            words.add(w.getString(new Integer(i+1).toString()));
                        }
                        gamedata= new GameData(role,host,port,words);
                    }
                    Message message = mHandler.obtainMessage(1,gamedata);
                    message.sendToTarget();
                    progressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /*Intent intent = new Intent(this, Play.class);
                startActivity(intent);*/
            }
        }.start();

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
