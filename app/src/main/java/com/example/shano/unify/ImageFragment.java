package com.example.shano.unify;

import android.app.DialogFragment;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class ImageFragment extends DialogFragment {

    private Button enlargeImageBtn;
    private String imgUrl;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.image_fragment_layout,container,false);

        //set title
        getDialog().setTitle("Image");
        ImageView img = (ImageView) rootView.findViewById(R.id.enlargeImage);
        enlargeImageBtn = (Button) rootView.findViewById(R.id.closeImageButton);
        PicassoClient.downloadImage(context, imgUrl, img, "");
        enlargeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return rootView;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
