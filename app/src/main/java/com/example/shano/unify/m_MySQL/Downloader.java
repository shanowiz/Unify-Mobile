package com.example.shano.unify.m_MySQL;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.view.View;
import android.widget.ListView;

import com.example.shano.unify.MainActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by shano on 4/6/2017.
 */

public class Downloader extends AsyncTask<Void,Void,String> {

    private Context c;
    private String urlAddress;
    private ListView lv;
    private String status;
    private String type;
    private ProgressDialog pd;
    private View view;

    public Downloader(Context c, String urlAddress, ListView lv,String status, String type, View view) {
        this.c = c;
        this.status= status;
        this.urlAddress = urlAddress;
        this.lv = lv;
        this.type = type;
        this.view = view;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pd = new ProgressDialog(c);
        pd.setTitle("Retrieve");
        pd.setMessage("Retrieving.....please wait....");
        pd.show();
    }

    @Override
    protected String doInBackground(Void... params) {

        return downloadData();
    }


    @Override
    protected void onPostExecute(String jsonData) {
        super.onPostExecute(jsonData);

        pd.dismiss();

        if (jsonData == null) {
            //testing if data was retrieved
            MainActivity.getInstance().displayNoDataMsg();
            Snackbar snackbar = Snackbar.make(view, Html.fromHtml("<font color=\"#ffffff\">NO CONNECTION</font>"), Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(Color.parseColor("#1a75ff"));
            snackbar.show();
        }else {
            //parser
            DataParser parser = new DataParser(c, jsonData, lv, status, type, view);
            parser.execute();
        }
    }

    private String downloadData () {

        HttpURLConnection con = Connector.connect(urlAddress);

        if (con==null) {

            return null;
        }

        try {

            InputStream is = new BufferedInputStream(con.getInputStream());
            BufferedReader br = new BufferedReader( new InputStreamReader(is));

            String line;
            StringBuffer jsonData = new StringBuffer();

            while ((line=br.readLine()) != null) {

                jsonData.append(line+"\n");

            }
            br.close();
            is.close();

            return jsonData.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
