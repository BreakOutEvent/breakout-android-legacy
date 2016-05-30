package org.break_out.breakout.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.break_out.breakout.R;
import org.break_out.breakout.model.EmergencyHelp;
import org.break_out.breakout.ui.activities.MainActivity;
import org.break_out.breakout.ui.adapters.HelpAdapter;

import java.util.ArrayList;

/**
 * Created by Maximilian Dühr on 30.05.2016.
 */
public class HelpFragment extends Fragment {
    private static final String TAG = "HelpFragment";
    private ListView _listView;
    private ArrayList<EmergencyHelp> _emergencyInfoList;
    private HelpAdapter _adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(_emergencyInfoList == null) {
            _emergencyInfoList = new ArrayList<>();
            populateEmergencies();
            _adapter = new HelpAdapter(getContext(),R.layout.listitem_help,_emergencyInfoList);
            _adapter.notifyDataSetChanged();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_help, container, false);
        _listView = (ListView) v.findViewById(R.id.help_listView);
        _listView.setAdapter(_adapter);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.help_toolbar);
        toolbar.setTitle(getString(R.string.title_help));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if(!(activity instanceof MainActivity)) {
                    return;
                }
                MainActivity mainActivity = (MainActivity) activity;
                mainActivity.openDrawer();
            }
        });

        return v;
    }

    private void populateEmergencies() {

        EmergencyHelp medicalHelp = new EmergencyHelp(getString(R.string.emergencytitle_medical),
                new EmergencyHelp.EmergencyInfo(getString(R.string.emergency_medical_1),getString(R.string.number_emergency)),
                new EmergencyHelp.EmergencyInfo(getString(R.string.emergency_medical_2),""),
                new EmergencyHelp.EmergencyInfo(getString(R.string.emergency_last),getString(R.string.number_breakout)));

        EmergencyHelp policeHelp = new EmergencyHelp(getString(R.string.emergencytitle_police),
                new EmergencyHelp.EmergencyInfo(getString(R.string.emergency_police_1),getString(R.string.number_emergency)),
                new EmergencyHelp.EmergencyInfo(getString(R.string.emergency_last),getString(R.string.number_breakout)));

        EmergencyHelp diplomaticHelp = new EmergencyHelp(getString(R.string.emergencytitle_diplomacic),
                new EmergencyHelp.EmergencyInfo(getString(R.string.emergency_diplomatic_1),getString(R.string.number_diplomatic)),
                new EmergencyHelp.EmergencyInfo(getString(R.string.emergency_last),getString(R.string.number_breakout)));

        EmergencyHelp otherHelp = new EmergencyHelp(getString(R.string.emergencytitle_etc),
                new EmergencyHelp.EmergencyInfo(getString(R.string.emergency_etc_1),getString(R.string.number_breakout)));

        _emergencyInfoList.clear();
        _emergencyInfoList.add(medicalHelp);
        _emergencyInfoList.add(policeHelp);
        _emergencyInfoList.add(diplomaticHelp);
        _emergencyInfoList.add(otherHelp);

    }
}
