package com.example.shano.unify.push_Notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shano.unify.EachEventActivity;
import com.example.shano.unify.MainActivity;
import com.example.shano.unify.R;
import com.example.shano.unify.m_DataObject.UnifyEvent;
import com.example.shano.unify.m_MySQL.Downloader;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

/**
 * Created by shano on 4/24/2017.
 */

public class FcmMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Random rand = new Random();
        int  index = rand.nextInt(1000) + 1;

        //you can get your text message here.
        Map<String, String> data = remoteMessage.getData();
        String title= data.get("title");
        String message = data.get("line1");
        String type = data.get("line2");

        ArrayList<String> preference = getPreference ();

        for (String temp : preference) {

            if (temp.equals(title) || title.contains(temp) || title.equals("university") || title.equals("admin")) {

                //Bitmap notificationLargeIcon = BitmapFactory.decodeResource(this.getResources(),R.drawable.unify1);

                Intent intent = new Intent(this, EachEventActivity.class);
                if (type.equals("event")) {
                    //sets event object details
                    String event = data.get("line1");
                    String venue = data.get("line3");
                    String details = data.get("line4");
                    String time = data.get("line5");
                    String cost = data.get("line6");
                    String date = data.get("line7");
                    String imageUrl = data.get("line8");

                    intent.putExtra("type","eventNotification");
                    intent.putExtra("eventName",event);
                    intent.putExtra("eventVenue",venue);
                    intent.putExtra("eventDetails",details);
                    intent.putExtra("eventTime",time);
                    intent.putExtra("eventCost",cost);
                    intent.putExtra("eventDate",date);
                    intent.putExtra("eventImg",imageUrl);

                }else if (type.equals("discount")) {
                    //sets discount object with details
                    String discount = data.get("line1");
                    String venue = data.get("line3");
                    String details = data.get("line4");
                    String date = data.get("line5");
                    String imageUrl = data.get("line6");

                    intent.putExtra("type","discountNotification");
                    intent.putExtra("discountName",discount);
                    intent.putExtra("discountVenue",venue);
                    intent.putExtra("discountDetails",details);
                    intent.putExtra("discountDate",date);
                    intent.putExtra("discountImg",imageUrl);

                }

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, index, intent, PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder notificationBuiler = new NotificationCompat.Builder(this)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_HIGH);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(index, notificationBuiler.build());
            }
        }
    }


    public ArrayList<String> getPreference () {

        ArrayList<String> preference = new ArrayList<>();

        try {
            String message ="";
            FileInputStream fileInputStream = openFileInput("preference");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();

            while ((message = bufferedReader.readLine()) != null) {

                stringBuffer.append(message);
                String split [] = stringBuffer.toString().split("\\+");

                for (int i=0; i<split.length; i++) {

                    preference.add(split[i]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return preference;
    }
}
