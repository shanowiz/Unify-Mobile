package com.example.shano.unify;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.shano.unify.m_DataObject.UnifyEvent;

/**
 * Created by shano on 6/17/2017.
 */

public class ReminderNotificationReceiver extends BroadcastReceiver {

    private static String note = "Reminder";
    //private UnifyEvent event;

    @Override
    public void onReceive(Context context, Intent intent) {
        String text = intent.getStringExtra("reminderName");
        int reminderId = intent.getIntExtra("reminderId",0);

        /*event = new UnifyEvent();
        event.setId(intent.getIntExtra("reminderId",0));
        event.setName(intent.getStringExtra("reminderName"));
        event.setDetails(intent.getStringExtra("eventDetails"));
        event.setDate(intent.getStringExtra("eventDate"));
        event.setCost(intent.getStringExtra("eventCost"));
        event.setTime(intent.getStringExtra("eventTime"));
        event.setLocation(intent.getStringExtra("eventVenue"));
        event.setImageUrl(intent.getStringExtra("eventImg"));
        EachEventActivity.setEvent(event);
        EachEventActivity.setReceiverReminderId(reminderId);*/

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent i = new Intent(context, EachEventActivity.class);
        i.putExtra("type","reminder");
        i.putExtra("reminderName",intent.getStringExtra("reminderName"));
        i.putExtra("reminderId",intent.getStringExtra("reminderId"));
        i.putExtra("eventDetails",intent.getStringExtra("eventDetails"));
        i.putExtra("eventDate",intent.getStringExtra("eventDate"));
        i.putExtra("eventTime",intent.getStringExtra("eventTime"));
        i.putExtra("eventCost",intent.getStringExtra("eventCost"));
        i.putExtra("eventVenue",intent.getStringExtra("eventVenue"));
        i.putExtra("eventImg",intent.getStringExtra("eventImg"));
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,reminderId,i,PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder (context)
                .setContentIntent(pendingIntent)
                .setContentTitle(note)
                .setContentText(text)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH);
        notificationManager.notify(reminderId, builder.build());


    }

    public static String getNote() {
        return note;
    }

    public static void setNote(String note) {
        ReminderNotificationReceiver.note = note;
    }

}
