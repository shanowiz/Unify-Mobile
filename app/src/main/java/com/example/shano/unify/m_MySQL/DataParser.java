package com.example.shano.unify.m_MySQL;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.view.View;
import android.widget.ListView;

import com.example.shano.unify.CustomEventListActivity;
import com.example.shano.unify.MainActivity;
import com.example.shano.unify.m_DataObject.UnifyEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by shano on 4/6/2017.
 */

public class DataParser extends AsyncTask<Void, Void, Integer>  {

    private Context c;
    private String jsonData;
    private ListView lv;
    private String status;
    private String type;
    private View view;
    public static CustomEventListActivity customAdapter;

    private ProgressDialog pd;
    static ArrayList<UnifyEvent> eventsArray = new ArrayList<>();


    public DataParser(Context c, String jsonData, ListView lv, String status,String type, View view) {
        this.c = c;
        this.status = status;
        this.jsonData = jsonData;
        this.lv = lv;
        this.type = type;
        this.view = view;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pd = new ProgressDialog(c);
        pd.setTitle("Parse");
        pd.setMessage("Parsing...Please wait...");
        pd.show();
    }

    @Override
    protected Integer doInBackground(Void... params) {

        return this.parseData();
    }


    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);

        pd.dismiss();

        if (eventsArray.isEmpty()) {
            MainActivity.getInstance().displayNoDataMsg();
            lv.setAdapter(null);
            Snackbar snackbar = Snackbar.make(view, Html.fromHtml("<font color=\"#ffffff\">NO DATA</font>"),Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(Color.parseColor("#1a75ff"));
            snackbar.show();
        }else if (result==0) {
            //Toast.makeText(c,"Unable to parse",Toast.LENGTH_SHORT).show();
            MainActivity.getInstance().displayNoDataMsg();
            lv.setAdapter(null);
            Snackbar snackbar = Snackbar.make(view, Html.fromHtml("<font color=\"#ffffff\">UNABLE TO PARSE</font>"),Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(Color.parseColor("#1a75ff"));
            snackbar.show();
        } else {
            //Bind data to list view
            customAdapter = new CustomEventListActivity(c, eventsArray, status, type);
            lv.setAdapter(customAdapter);
        }
    }

    public static ArrayList<UnifyEvent> getEventsArray() {
        return eventsArray;
    }

    public static void setEventsArray(ArrayList<UnifyEvent> eventsArray1) { eventsArray = eventsArray1; }

    private int parseData () {

        try {

            JSONArray ja = new JSONArray(jsonData);
            JSONObject jo = null;

            eventsArray.clear();

            UnifyEvent event;

            for (int i = 0; i < ja.length(); i++) {

                Calendar calendar = Calendar.getInstance();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date date = new Date();

                if (type.equals("event")) {

                    jo = ja.getJSONObject(i);

                    int eventCount = jo.getInt("count");
                    String id = jo.getString("id");
                    String eventName = jo.getString("name");
                    String eventDate = jo.getString("eventdate");
                    String location = jo.getString("venue");
                    String imgUrl = jo.getString("image");
                    String department = jo.getString("department");
                    String details = jo.getString("details");
                    String cost = jo.getString("cost");
                    String startTime = jo.getString("starttime");
                    String endTime = jo.getString("endtime");
                    int attending = jo.getInt("attending");

                    Date eventDate1 = new SimpleDateFormat("dd-MM-yyyy").parse(eventDate);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String stringDate = simpleDateFormat.format(calendar.getTime());

                    if (eventDate1.after(date) || stringDate.equals(eventDate)) {

                        event = new UnifyEvent();

                        if (department.equals(status) || department.contains(status)) {

                            event.setEventCount(eventCount);
                            event.setId(id);
                            event.setName(eventName);
                            event.setDepartment(department);
                            event.setDate(eventDate);
                            event.setLocation(location);
                            event.setImageUrl(imgUrl);
                            event.setDetails(details);
                            event.setCost(cost);
                            event.setTime(startTime + " - " + endTime);
                            event.setAttending(attending);
                            eventsArray.add(event);

                        } else if (status.equals("happeningToday")) {

                            SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy");
                            String currentDate = mdformat.format(calendar.getTime());

                            if (currentDate.equals(eventDate)) {

                                event.setEventCount(eventCount);
                                event.setId(id);
                                event.setName(eventName);
                                event.setDepartment(department);
                                event.setDate(currentDate);
                                event.setLocation(location);
                                event.setImageUrl(imgUrl);
                                event.setDetails(details);
                                event.setCost(cost);
                                event.setTime(startTime + " - " + endTime);
                                event.setAttending(attending);
                                eventsArray.add(event);

                            }

                        } else if (status.equals("allEvents") || status.equals("allEventsOnCreate")) {

                            event.setEventCount(eventCount);
                            event.setId(id);
                            event.setName(eventName);
                            event.setDepartment(department);
                            event.setDate(eventDate);
                            event.setLocation(location);
                            event.setImageUrl(imgUrl);
                            event.setDetails(details);
                            event.setCost(cost);
                            event.setTime(startTime + " - " + endTime);
                            event.setAttending(attending);
                            eventsArray.add(event);
                        }
                    }

                } else if (type.equals("discount")) {

                    UnifyEvent discount;

                    jo = ja.getJSONObject(i);

                    int eventCount = jo.getInt("count");
                    String id = jo.getString("id");
                    String entity = jo.getString("name");
                    String details = jo.getString("details");
                    String discountDate = jo.getString("date");
                    String location = jo.getString("venue");
                    String imgUrl = jo.getString("image");
                    //int attending = jo.getInt("attending");

                    Date discountDate1 = new SimpleDateFormat("dd-MM-yyyy").parse(discountDate);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String stringDate = simpleDateFormat.format(calendar.getTime());

                    if (discountDate1.after(date) || stringDate.equals(discountDate)) {

                        discount = new UnifyEvent();

                        discount.setEventCount(eventCount);
                        discount.setId(id);
                        discount.setName(entity);
                        discount.setDate(discountDate);
                        discount.setDetails(details);
                        discount.setLocation(location);
                        discount.setImageUrl(imgUrl);
                        //discount.setAttending(attending);
                        eventsArray.add(discount);

                     }
                }
        }
            return 1;

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;

    }
}
