package com.game.sketchnary.sketchnary.Connection;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;

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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import static com.game.sketchnary.sketchnary.Authentication.LoginActivity.IP_ADRESS;

/**
 * Created by David on 27/05/2016.
 */

public class Https {

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
    public static String httpJoinServer(SSLContext context, String urlS){
        String res = "Server error...Try again later!";
        try {
            //"https://"+IP_ADRESS+"/api/room/?rooms="+RoomName
            URL url = new URL(urlS);
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
                //resData = serverAwnser;
            }else if(status.equals("error")){
                res = serverAwnser.getString("reason");
            }
            System.out.println("REASON: "+res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static SSLContext httpStart(AssetManager mngr, SSLContext context){
        try{
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);

            keyStore.load(mngr.open("Keys/truststore.bks"), "123456".toCharArray());

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
            System.out.println("Cori");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        System.out.println("VOU RETORNAR O CONTEXT!");
        return context;
    }
}
