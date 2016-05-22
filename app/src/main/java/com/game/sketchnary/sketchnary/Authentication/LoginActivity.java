package com.game.sketchnary.sketchnary.Authentication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.game.sketchnary.sketchnary.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;


public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static int LOGIN_STATUS = 0;
    private static String IP_ADRESS = "192.168.1.5";//this may change..
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
            _loginButton.setEnabled(true);
        }
    };



    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;


    static {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
        {
            public boolean verify(String hostname, SSLSession session)
            {
                // ip address of the service URL(like.23.28.244.244)
                if (hostname.equals(IP_ADRESS))
                    return true;
                else
                    return false;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _emailText = (EditText)findViewById(R.id.input_email);
        _passwordText = (EditText)findViewById(R.id.input_password);
        _loginButton = (Button)findViewById(R.id.btn_login);
        _signupLink = (TextView)findViewById(R.id.link_signup);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.


        new Thread() {
            public void run() {
                // On complete call either onLoginSuccess or onLoginFailed
                Log.d(TAG,"VAMOS TESTAR!");
                Log.d(TAG,"Email: "+email);
                Log.d(TAG,"Password: "+password);
                LOGIN_STATUS = testLogin(email,password);
                Message message;
                switch(LOGIN_STATUS){
                    case 0:
                        Log.d(TAG,"Server error.... Try again later!!");
                        message = mHandler.obtainMessage();
                        message.sendToTarget();
                        break;
                    case 1:
                        Log.d(TAG,"Invalid Username!");
                        message = mHandler.obtainMessage();
                        message.sendToTarget();
                        break;
                    case 2:
                        Log.d(TAG,"Invalid Password!");
                        message = mHandler.obtainMessage();
                        message.sendToTarget();
                        break;
                    case 3:
                        onLoginSuccess();
                        break;
                    default:
                }
                progressDialog.dismiss();
            }
        }.start();

    }

    protected int testLogin(String email,String password) {
        int res=0;
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

            URL url = new URL("https://"+IP_ADRESS+"/api/event/?username="+email+"&password="+password);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            InputStream in = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader( new InputStreamReader(in )  );
            String line = null;
            StringBuilder sb = new StringBuilder();
            while( ( line = reader.readLine() ) != null )  {
                sb.append(line);
            }
            Log.d(TAG,"ESTOU AUI1");
            String answer =sb.toString();
            Log.d(TAG,answer);
            switch(answer){
                case "Not Found!":
                    Log.d(TAG,"NOTFOUND-1");
                    res =1;
                    break;
                case "Invalid password!":
                    Log.d(TAG,"Invalid-1");
                    res =2;
                    break;
                case "GET request successful!":
                    Log.d(TAG,"SUCCESS-1");
                    break;
                default:
            }
            Log.d(TAG,"ESTOU AUI2");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
