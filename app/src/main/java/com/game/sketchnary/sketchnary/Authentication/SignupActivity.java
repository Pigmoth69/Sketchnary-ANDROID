package com.game.sketchnary.sketchnary.Authentication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
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

import com.game.sketchnary.sketchnary.Main.Room.Room;
import com.game.sketchnary.sketchnary.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
            if(status.equals("ok"))
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
        final String password = sha256(_passwordText.getText().toString());
        final String country = spinner.getSelectedItem().toString();

        // Signup logic

        new Thread() {
            public void run() {
                // On complete call either onLoginSuccess or onLoginFailed
                Log.d(TAG,"VAMOS TESTAR!");
                //testRegister(String name,String username,int age,String country,String email,String password)
                mMonth++;//porque o mês tem de ser superior a 0 caso seja janeiro, ele começa em 0
                String birthdate = new String(mYear+"-"+mMonth+"-"+mDay);
                System.out.println("Birthdate: "+birthdate);
                String res = testRegister(name,username,birthdate,country,email,password);
                Message message;
                message = mHandler.obtainMessage(1,res);
                message.sendToTarget();
                progressDialog.dismiss();
            }
        }.start();
    }

    protected String testRegister(String name,String username,String birthdate,String country,String email,String password) {
        String res="Server error...Try again later";
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
            URL url = new URL("https://"+IP_ADRESS+"/api/user/");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(context.getSocketFactory());
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
            osw.write(String.format(
                    "{\"email\":%s,\"name\":%s,\"username\":%s,\"birthdate\":%s,\"country\":%s,\"password\":%s}",email,name,username,birthdate,country,password )
            );
            osw.flush();
            osw.close();

            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader( new InputStreamReader(in )  );
            String line = null;
            StringBuilder sb = new StringBuilder();
            while( ( line = reader.readLine() ) != null )  {
                sb.append(line);
            }

            JSONObject serverAwnser;
            serverAwnser = new JSONObject(sb.toString());
            String status = serverAwnser.getString("status");
            if(status.equals("ok")){
                res=status;
            }else if(status.equals("error")){
                res = serverAwnser.getString("reason");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        Intent i = new Intent();
        i.putExtra("email",_emailText.getText().toString());
        i.putExtra("password",_passwordText.getText().toString());
        setResult(RESULT_OK, i);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    static String sha256(String input) {

        MessageDigest mDigest = null;
        try {
            mDigest = MessageDigest.getInstance("SHA256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String username = _usernameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String confirmpassword = _confirmpasswordText.getText().toString();


        if (name.isEmpty()||username.isEmpty() || name.length() < 3) {
            System.out.println("Entrei1");
            _nameText.setError("at least 3 characters");
            valid = false;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            System.out.println("Entrei2");
            _emailText.setError("enter a valid email address");
            valid = false;
        }

        if (password.isEmpty() ||confirmpassword.isEmpty() || confirmpassword.length() < 4 || password.length() < 4) {
            System.out.println("Entrei3");
            _passwordText.setError("minimum 4 characters required");
            valid = false;
        }
        if(!password.equals(confirmpassword)){
            System.out.println("Entrei4");
            _confirmpasswordText.setError("Passwords Mismatch");
            valid = false;
        }
        Calendar data = Calendar.getInstance();
        int year = data.get(Calendar.YEAR);
        int month = data.get(Calendar.MONTH);
        int day = data.get(Calendar.DAY_OF_MONTH);
        System.out.println("Year: "+year+" Mounth: "+month+" day: "+day);
        Date today = new Date(year,month,day);
        Date birthday = new Date(mYear,mMonth-1,mDay);

        if (today.compareTo(birthday)<=0){
            System.out.println("Entrei5");
            System.out.println("Res: "+today.compareTo(birthday));
            _showMyDate.setError("Invalid date");
            valid = false;
        }

        return valid;
    }
}