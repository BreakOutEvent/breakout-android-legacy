package org.break_out.breakout.ui.adapters;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.break_out.breakout.R;
import org.break_out.breakout.model.EmergencyHelp;

import java.util.ArrayList;

/**
 * Created by Maximilian Dühr on 30.05.2016.
 */
public class HelpAdapter extends ArrayAdapter<EmergencyHelp> {
    private Context _context;
    private ArrayList<EmergencyHelp> _helpList;

    public HelpAdapter(Context context, int resource, ArrayList<EmergencyHelp> objects) {
        super(context, resource, objects);
        _context = context;
        _helpList = objects;
    }

    @Override
    public int getCount() {
        return _helpList.size();
    }

    @Override
    public EmergencyHelp getItem(int position) {
        return _helpList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EmergencyViewHolder holder;
        EmergencyHelp curObject = getItem(position);
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem_help,parent,false);
        }
        if(convertView.getTag() == null) {
            holder = new EmergencyViewHolder();
            holder._titleView = (TextView) convertView.findViewById(R.id.help_tv_title);
            holder._infoList = (TextView) convertView.findViewById(R.id.help_tv_numbers);
            convertView.setTag(holder);
        } else {
            holder = (EmergencyViewHolder) convertView.getTag();
        }

        holder._titleView.setText(curObject.getTitle());
        CharSequence resultSequence = "";
        int i = 1;
        for(EmergencyHelp.EmergencyInfo info :curObject.getInfos()) {
            resultSequence = TextUtils.concat(resultSequence,"\n",i+". ",info.toSpan());
            i++;
        }
        holder._infoList.setText(resultSequence);
        return convertView;
    }

    private static class EmergencyViewHolder {
        private TextView _titleView;
        private TextView _infoList;
    }
}
