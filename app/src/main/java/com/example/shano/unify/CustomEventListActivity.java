package com.example.shano.unify;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shano.unify.m_DataObject.UnifyEvent;

import java.util.ArrayList;

/**
 * Created by shano on 4/2/2017.
 */


public class CustomEventListActivity extends BaseAdapter implements Filterable {

    Context c;
    ArrayList<UnifyEvent> eventsList;
    ArrayList<UnifyEvent> tempList;
    LayoutInflater inflater;
    String status;
    String type;
    CustomSearchFilter customSearchFilter;

    public CustomEventListActivity(Context c, ArrayList<UnifyEvent> eventsList, String status, String type) {
        this.c = c;
        this.eventsList = eventsList;
        this.tempList = eventsList;
        this.status = status;
        this.type = type;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return eventsList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (type.equals("event")) {

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.custom_event_layout, parent, false);
            }

            TextView eventName = (TextView) convertView.findViewById(R.id.eventName);
            TextView eventDate = (TextView) convertView.findViewById(R.id.eventDate);
            TextView eventDate1 = (TextView) convertView.findViewById(R.id.eventDate1);
            TextView eventLocation = (TextView) convertView.findViewById(R.id.eventLocation);
            TextView eventLocation1 = (TextView) convertView.findViewById(R.id.eventLocation1);
            ImageView eventImg = (ImageView) convertView.findViewById(R.id.eventImage);

            UnifyEvent event = eventsList.get(position);

            eventName.setText(event.getName());
            eventDate.setText(event.getDate());
            eventLocation.setText(event.getLocation());
            if (!event.getImageUrl().isEmpty()) {
                PicassoClient.downloadImage(c, event.getImageUrl(), eventImg, "eventsList");
            }else {eventImg.setImageDrawable(ContextCompat.getDrawable(c, R.drawable.no_image));
            }

        } else  if (type.equals("discount")) {

            if (convertView == null) {

                convertView = inflater.inflate(R.layout.custom_discount_layout, parent, false);
            }

            TextView discountName = (TextView) convertView.findViewById(R.id.entityName);
            TextView discountDate = (TextView) convertView.findViewById(R.id.discountDate);
            TextView discountDetails = (TextView) convertView.findViewById(R.id.discountDetails);
            ImageView discountImg = (ImageView) convertView.findViewById(R.id.discountImage);

            UnifyEvent discount = eventsList.get(position);

            discountName.setText(discount.getName());
            discountDate.setText(discount.getDate());
            discountDetails.setText(discount.getDetails());
            if (!discount.getImageUrl().isEmpty()) {
                PicassoClient.downloadImage(c, discount.getImageUrl(), discountImg, "discountsList");
            }else {
                discountImg.setImageDrawable(ContextCompat.getDrawable(c, R.drawable.no_image));
            }
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {

        if (customSearchFilter == null) {
            customSearchFilter = new CustomSearchFilter();
        }
        return customSearchFilter;
    }

    class CustomSearchFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence searchQuery) {

                FilterResults filterResults = new FilterResults();

                if (searchQuery != null && searchQuery.length() > 0) {

                    searchQuery = searchQuery.toString().toUpperCase();
                    ArrayList<UnifyEvent> filters = new ArrayList<>();

                    for (int i = 0; i < tempList.size(); i++) {
                        if (tempList.get(i).getName().toUpperCase().contains(searchQuery) || tempList.get(i).getDetails().toUpperCase().contains(searchQuery) || tempList.get(i).getLocation().toUpperCase().contains(searchQuery) || tempList.get(i).getDepartment().toUpperCase().contains(searchQuery) ) {
                            filters.add(tempList.get(i));
                        }
                    }

                    filterResults.count = filters.size();
                    filterResults.values = filters;
                } else {
                    filterResults.count = tempList.size();
                    filterResults.values = tempList;
                }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            eventsList = (ArrayList<UnifyEvent>) results.values;
            notifyDataSetChanged();

        }
    }
}
