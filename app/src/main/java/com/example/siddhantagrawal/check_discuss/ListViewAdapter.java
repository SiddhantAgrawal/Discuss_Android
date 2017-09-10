package com.example.siddhantagrawal.check_discuss;


import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 *
 * @author siddhant.agrawal, Deepak Thakur
 *
 */
public class ListViewAdapter extends BaseAdapter {

    // Declare Variables
    Context context;
    LayoutInflater inflater;
    List<Population> data;
    ImageLoader imageLoader;

    public ListViewAdapter(Context context,
                           List<Population> arraylist) {
        this.context = context;
        data = arraylist;
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // Declare Variables
        TextView rank;
        TextView country;
        TextView population;
        ImageView flag;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.listview_item, parent, false);
        // Get the position
        final Population resultp = data.get(position);

        // Locate the TextViews in listview_item.xml
        rank = (TextView) itemView.findViewById(R.id.rank);
        country = (TextView) itemView.findViewById(R.id.country);
        population = (TextView) itemView.findViewById(R.id.population);

        // Locate the ImageView in listview_item.xml
        flag = (ImageView) itemView.findViewById(R.id.flag);

        // Capture position and set results to the TextViews
        rank.setText(resultp.rank);
        country.setText(resultp.country);
        population.setText(resultp.population);
        // Capture position and set results to the ImageView
        // Passes flag images URL into ImageLoader.class
        imageLoader.DisplayImage(resultp.flag, flag);
        // Capture ListView item click
        itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get the position
                Population result = data.get(position);
                Intent intent = new Intent(context, SingleItemView.class);
                // Pass all data rank
                intent.putExtra("rank", result.rank);
                // Pass all data country
                intent.putExtra("country", result.country);
                // Pass all data population
                intent.putExtra("population", result.population);
                // Pass all data flag
                intent.putExtra("flag", result.flag);
                // Start SingleItemView Class
                context.startActivity(intent);

            }
        });
        return itemView;
    }
}
