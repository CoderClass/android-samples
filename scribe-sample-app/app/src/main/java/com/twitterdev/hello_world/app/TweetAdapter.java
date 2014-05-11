package com.twitterdev.hello_world.app;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;

import com.twitterdev.hello_world.app.R;


public class TweetAdapter extends BaseAdapter {

    private Activity activity;
    private String[] data;
    private static LayoutInflater inflater=null;


    public TweetAdapter(Activity a, String[] d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //imageLoader=new ImgLoader(activity.getApplicationContext());
    }
    public void updateResults(String[] results) {
        data = results;
        //Triggers the list update
        notifyDataSetChanged();
    }


    public int getCount() {
        return data.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_v, null);
        TextView text=(TextView)vi.findViewById(R.id.tweet);
        text.setText(data[position]);
        return vi;
    }


}