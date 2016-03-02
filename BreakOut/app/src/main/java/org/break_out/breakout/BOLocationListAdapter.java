package org.break_out.breakout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Maximilian Dühr on 02.03.2016.
 */
public class BOLocationListAdapter extends ArrayAdapter<BOLocation> {
    private ArrayList<BOLocation> sourceData;
    private Context _context;

    public BOLocationListAdapter(Context context, int resource, ArrayList<BOLocation> objects) {
        super(context, resource, objects);
        sourceData = objects;
        _context = context;
    }

    @Override
    public BOLocation getItem(int position) {
        return sourceData.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BOLocation currentItem = getItem(position);
        BOLocationViewHolder holder;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listrow_bolocation,parent,false);
            holder = new BOLocationViewHolder();
            holder.tv_time = (TextView) convertView.findViewById(R.id.bolocation_tv_time);
            holder.tv_coords = (TextView) convertView.findViewById(R.id.bolocation_tv_coordinates);
            convertView.setTag(holder);
        } else {
            holder = (BOLocationViewHolder) convertView.getTag();
        }
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(currentItem.getTimestamp());
        String time = c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE);
        holder.tv_time.setText(time);
        String coordinates = "lat.: "+currentItem.getLatitude()+", long.: "+currentItem.getLongitude();
        holder.tv_coords.setText(coordinates);

        return convertView;
    }

    private static class BOLocationViewHolder {
        TextView tv_time;
        TextView tv_coords;
    }
}
