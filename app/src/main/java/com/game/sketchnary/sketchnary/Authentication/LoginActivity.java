package com.game.sketchnary.sketchnary.Authentication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.game.sketchnary.sketchnary.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.sql.Connection;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;


public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static int LOGIN_STATUS = 0;
    private ProgressDialog progressDialog;/* = new ProgressDialog(LoginActivity.this,
            R.style.AppTheme_Dark_Dialog);*/


    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;

    public LoginActivity() {
        progressDialog = null;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");

        _emailText = (EditText)findViewById(R.id.input_email);
        _passwordText = (EditText)findViewById(R.id.input_password);
        _loginButton = (Button)findViewById(R.id.btn_login);
        _signupLink = (TextView)findViewById(R.id.link_signup);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progressDialog.show();
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
        progressDialog.show();
        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.


        new android.os.Handler().postDelayed(
                new Thread() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        Log.d(TAG,"VAMOS TESTAR!");
                        Log.d(TAG,"Email: "+email);
                        Log.d(TAG,"Password: "+password);
                        LOGIN_STATUS = testLogin(email,password);
                        switch(LOGIN_STATUS){
                            case 0:
                                Log.d(TAG,"NOTHING HAPPENED!");
                                onLoginFailed();
                                break;
                            case 1:
                                Log.d(TAG,"Invalid Username!");
                                onLoginFailed();
                                break;
                            case 2:
                                Log.d(TAG,"Invalid Password!");
                                onLoginFailed();
                                break;
                            case 3:
                                onLoginSuccess();
                                break;
                            default:
                        }
                        progressDialog.dismiss();
                    }
                }, 1000);

    }

    protected int testLogin(String email,String password) {
        /*HttpsConnection client = new HttpsConnection("https://172.30.28.240/api/event/?username=player1&password=pass", "GET");
        client.setContext();

        AssetManager am = getAssets();
        try {
            Log.d(TAG,"TENTAR");
            InputStream clientKeys = am.open("Keys/client.keys");
            InputStream trustStone = am.open("Keys/truststore");

                Log.d(TAG,"nãotem: ");
            Log.d(TAG,"FIM");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String urlParameters = "?username=player1&password=pass";

        client.setConnection(null);*/

        AssetManager assetManager = getAssets();
        InputStream keyStoreInputStream = null;
        try {
            keyStoreInputStream = assetManager.open("Keys/truststore.bks");
        } catch (IOException e) {
            e.printStackTrace();
        }
        KeyStore trustStore = null;
        try {
            trustStore = KeyStore.getInstance("BKS");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        try {
            trustStore.load(keyStoreInputStream, "123456".toCharArray());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }


        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = null;
        try {
            tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            tmf.init(trustStore);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, tmf.getTrustManagers(), null);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        URL url = null;
        try {
            url = new URL("https://192.168.1.5/api/events/?username=d");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpsURLConnection urlConnection = null;
        try {
            urlConnection = (HttpsURLConnection)url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

        try {
            InputStream in = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader( new InputStreamReader( in )  );
            String line = null;
            StringBuilder sb = new StringBuilder();
            while( ( line = reader.readLine() ) != null )  {
                sb.append(line);
            }
            Log.d(TAG,"RESPOSTA!");
            String response = sb.toString();
            Log.d(TAG,response);
            Log.d(TAG,"FIM-RESPOSTA!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 2;
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
