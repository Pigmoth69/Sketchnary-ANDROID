package com.game.sketchnary.sketchnary.Authentication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.game.sketchnary.sketchnary.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import static com.game.sketchnary.sketchnary.Authentication.LoginActivity.IP_ADRESS;


public class SignupActivity extends Activity {

    private int mYear;
    private int mMonth;
    private int mDay;
    private TextView mDateDisplay;
    private Button mPickDate;
    static final int DATE_DIALOG_ID = 0;

    private static final String TAG = "SignupActivity";
    private static int SIGNUP_STATUS = 0;

    EditText _nameText;
    EditText _usernameText;
    EditText _emailText;
    EditText _passwordText;
    EditText _confirmpasswordText;
    TextView _showMyDate;
    Spinner spinner;

    Button _signupButton;
    TextView _loginLink;



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
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            String status = (String)message.obj;
            if(status.equals("Signup successful!"))
                onSignupSuccess();
            else {
                Toast.makeText(getBaseContext(), status, Toast.LENGTH_LONG).show();
                _signupButton.setEnabled(true);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        _nameText = (EditText) findViewById(R.id.input_name);
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _confirmpasswordText = (EditText)findViewById(R.id.input_confirmpassword);
        _usernameText = (EditText)findViewById(R.id.input_username);
        spinner = (Spinner) findViewById(R.id.countries_spinner);
        _showMyDate = (TextView)findViewById(R.id.showMyDate);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.countries_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        _signupButton = (Button) findViewById(R.id.btn_signup);
        _loginLink = (TextView) findViewById(R.id.link_login);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
        //Age Display
        mDateDisplay = (TextView) findViewById(R.id.showMyDate);
        mPickDate = (Button) findViewById(R.id.myDatePickerButton);

        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        // get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // display the current date
        updateDisplay();
    }

    private void updateDisplay() {
        this.mDateDisplay.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(mMonth + 1).append("-")
                        .append(mDay).append("-")
                        .append(mYear).append(" "));
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
            };
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
        }
        return null;
    }

    public void signup() {
        Log.d(TAG, "Signup");


        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Registering");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final String name = _nameText.getText().toString();
        final String username = _usernameText.getText().toString();
        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();
        final String country = spinner.getSelectedItem().toString();

        // Signup logic

        new Thread() {
            public void run() {
                // On complete call either onLoginSuccess or onLoginFailed
                Log.d(TAG,"VAMOS TESTAR!");
                //testRegister(String name,String username,int age,String country,String email,String password)
                SIGNUP_STATUS = testRegister(name,username,mDay,mMonth,mYear,country,email,password);
                Message message;
                switch(SIGNUP_STATUS){
                    case 0:
                        Log.d(TAG,"Server error.... Try again later!!");
                        message = mHandler.obtainMessage(1,"Server error.... Try again later!!");
                        message.sendToTarget();
                        break;
                    case 1:
                        Log.d(TAG,"Email already in use!");
                        message = mHandler.obtainMessage(2,"Email already in use!");
                        message.sendToTarget();
                        break;
                    case 2:
                        Log.d(TAG,"Username already in use!");
                        message = mHandler.obtainMessage(3,"Username already in use!");
                        message.sendToTarget();
                        break;
                    case 3:
                        Log.d(TAG,"Signup successful!");
                        message = mHandler.obtainMessage(4,"Signup successful!");
                        message.sendToTarget();
                        break;
                    default:
                }
                progressDialog.dismiss();
            }
        }.start();
    }

    protected int testRegister(String name,String username,int mDay,int mMonth,int mYear,String country,String email,String password) {
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

            //FAZER O PUT
            URL url = new URL("https://"+IP_ADRESS+"/api/event/?email="+email+"&password="+password);
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

            String answer =sb.toString();
            Log.d(TAG,answer);

            //ESTOU AQUI, FALTA TRATAR DAS MENSAGENS E RECEBER O JSON/FAZER O PUT
            if(answer.equals("Invalid email!")){
                Log.d(TAG,"NOTFOUND-1");
                res =1;
            }else if(answer.equals("Invalid password!")){
                Log.d(TAG,"Invalid-1");
                res =2;
            }else if(answer.equals("Login successful!")){
                Log.d(TAG,"SUCCESS-1");
                res =3;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String username = _usernameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String confirmpassword = _confirmpasswordText.getText().toString();


        if (name.isEmpty()||username.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() ||confirmpassword.isEmpty() || confirmpassword.length() < 4 || password.length() < 4) {
            _passwordText.setError("minimum 4 characters required");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        if(!password.equals(confirmpassword)){
            _confirmpasswordText.setError("Passwords Mismatch");
            valid = false;
        }
        Calendar data = Calendar.getInstance();
        int year = data.get(Calendar.YEAR);
        int month = data.get(Calendar.MONTH);
        int day = data.get(Calendar.DAY_OF_MONTH);
        Date today = new Date(year,month,day);
        Date birthday = new Date(mYear,mMonth-1,mDay);

        if (today.compareTo(birthday)<-8){
            _showMyDate.setError("You should be older than 8 years old");
            valid=false;
        }
        else {
            _showMyDate.setError("Invalid date");
            valid = false;
        }

        return valid;
    }
}