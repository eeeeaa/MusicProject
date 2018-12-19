package com.tiger.user.musicproject.Management;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tiger.user.musicproject.R;

public class CustomToast {

    public static void makeText(Context mContext,String massage,int length){
        Toast toast = Toast.makeText(mContext,massage,length);
        View view = toast.getView();
        TextView text = (TextView) view.findViewById(android.R.id.message);
        text.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
        text.setBackgroundColor(Color.TRANSPARENT);
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "font/roboto_regular.ttf");
        text.setTextColor(Color.RED);
        text.setTypeface(typeface);
        toast.show();
    }
    public static void makeTextNormal(Context mContext,String massage,int length){
        Toast toast = Toast.makeText(mContext,massage,length);
        View view = toast.getView();
        TextView text = (TextView) view.findViewById(android.R.id.message);
        text.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
        text.setBackgroundColor(Color.TRANSPARENT);
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "font/roboto_regular.ttf");
        text.setTextColor(Color.GREEN);
        text.setTypeface(typeface);
        toast.show();
    }
}
