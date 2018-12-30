package com.example.shano.unify;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by shano on 4/6/2017.
 */

public class PicassoClient {

    public static void downloadImage (Context c, String imgUrl, ImageView img, String type) {

        if (imgUrl.length()>0 && imgUrl!=null) {

            if (type.equals("eventsList") || type.equals("discountsList")) {
                Picasso.with(c).load(imgUrl).into(img);
            } else {
                Picasso.with(c).load(imgUrl).into(img);
            }

        } else {

            Picasso.with(c).load(R.drawable.no_image).into(img);
        }

    }
}
