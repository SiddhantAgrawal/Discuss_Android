package com.discuss.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.siddhantagrawal.check_discuss.R;
import com.squareup.picasso.Picasso;

/**
 *
 * @author Deepak Thakur
 */
public class UIUtil {

    public static void setTextView(View view, int id, String value) {
        TextView textView = (TextView) view.findViewById(id);
        textView.setText(value);
    }

    public static void setImageView(Context context, View view, int id, String imageUrl) {
        if(null != imageUrl) {
            ImageView image = view.findViewById(id);
            Picasso.with(context).load(imageUrl).into(image);
        }
    }

}
