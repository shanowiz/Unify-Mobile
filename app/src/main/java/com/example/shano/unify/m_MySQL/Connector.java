package com.example.shano.unify.m_MySQL;

import android.content.Context;
import android.net.ConnectivityManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by shano on 4/6/2017.
 */

public class Connector {

    public static HttpURLConnection connect (String urlAddress) {

        try {

            URL url = new URL (urlAddress) ;
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //connection properties
            con.setRequestMethod("GET");
            con.setConnectTimeout(20000);
            con.setReadTimeout(20000);
            con.setDoInput(true);

            return con;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
