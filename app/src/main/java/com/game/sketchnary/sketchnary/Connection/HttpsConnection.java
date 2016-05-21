package com.game.sketchnary.sketchnary.Connection;


import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class HttpsConnection {

    private String urlString;
    private String requestMethod;
    private HttpsURLConnection connection;
    private URL url;

    public HttpsConnection(String urlString, String requestMethod) {
        this.urlString = urlString;
        this.requestMethod = requestMethod;
    }

    static {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
        {
            @Override
            public boolean verify(String hostname, SSLSession session)
            {
                // ip address of the service URL(like.23.28.244.244)
                if (hostname.equals("172.30.28.240"))
                    return true;
                return false;
            }
        });
    }

    public String getUrl(){
        return urlString;
    }

    public String getRequestMethod(){
        return requestMethod;
    }

    public HttpsURLConnection getConnection(){
        return connection;
    }

    public URL getURL(){
        return url;
    }

    public void setConnection(HttpsURLConnection connection){
        this.connection=connection;
    }
    public void setContext(){


        //File f = new File();
        /*Log.d("S","cenas: "+f.getAbsolutePath());
        if(f.exists()&& !f.isDirectory())
            Log.d("S","Exists");
        else
            Log.d("S","NO Exists");*/


        System.setProperty("javax.net.ssl.keyStore", "keys/client.keys");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");
        System.setProperty("javax.net.ssl.trustStore", "keys/truststore");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
    }

    public static void main(String args[]) throws IOException {
        HttpsConnection client = new HttpsConnection("https://172.30.28.240/api/event/?username=player1&password=pass", "GET");
        client.setContext();

        String urlParameters = "?username=player1&password=pass";

        client.connection = null;
        try {
            // Create connection
            URL url = new URL(client.getUrl());
            client.connection = (HttpsURLConnection) url.openConnection();
            client.connection.setRequestMethod("GET");
            client.connection.setRequestProperty("Content-Type", "api/event");

            client.connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));

            // Send request
			/*DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.close();*/

            // Get Response
            InputStream is = client.connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if
            // not Java 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            System.out.println(response);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client.connection != null) {
                client.connection.disconnect();
            }
        }

    }

    public boolean processGETRequest(String username, String password){

        try {
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "api/event");
        } catch (ProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;

    }

}
