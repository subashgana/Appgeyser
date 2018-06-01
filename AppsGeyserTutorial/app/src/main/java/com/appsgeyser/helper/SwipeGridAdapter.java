package com.appsgeyser.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.appsgeyser.Utils.DetailsVo;
import com.appsgeyser.tutorial.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by subash.b on 07-Mar-17.
 */

public class SwipeGridAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<DetailsVo> movieList;
    private String[] bgColors;
    private ArrayList<DetailsVo> arraylist;
    ImageLoader imageloader;

    public SwipeGridAdapter(Activity activity, List<DetailsVo> movieList, ImageLoader imageLoader) {
        this.activity = activity;
        this.movieList = movieList;
        this.imageloader = imageLoader;
        this.arraylist = new ArrayList<DetailsVo>();
        this.arraylist.addAll(movieList);
        bgColors = activity.getApplicationContext().getResources().getStringArray(R.array.movie_serial_bg);
    }


    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public Object getItem(int location) {
        return movieList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewHolder viewHolder;

        if (convertView == null)
            convertView = inflater.inflate(R.layout.grid_row, null);
        viewHolder = new ViewHolder();
        viewHolder.serial = (NetworkImageView) convertView.findViewById(R.id.grid_image);
        viewHolder.title = (TextView) convertView.findViewById(R.id.textView2);
        viewHolder.title.setText(String.valueOf(movieList.get(position).getTitle()));
        Typeface tf = Typeface.createFromAsset(activity.getAssets(), "titaillium.ttf");
        viewHolder.title.setTypeface(tf);
        viewHolder.title.setSelected(true);
        String color = bgColors[position % bgColors.length];
        viewHolder.title.setTextColor(Color.BLACK);
        viewHolder.title.setBackgroundColor(Color.parseColor(color));
        viewHolder.title.setSingleLine(true);
        //title.setBackgroundColor(Color.parseColor(color));
        //title.setText(movieList.get(position).getTitle());
        viewHolder.serial.setImageUrl(movieList.get(position).getmUrl(), imageloader);
        return convertView;
    }


    class ViewHolder {

        NetworkImageView serial;
        TextView title;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        movieList.clear();
        if (charText.length() == 0) {
            movieList.addAll(arraylist);
        } else {
            for (DetailsVo wp : arraylist) {
                if (wp.getTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                    movieList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}