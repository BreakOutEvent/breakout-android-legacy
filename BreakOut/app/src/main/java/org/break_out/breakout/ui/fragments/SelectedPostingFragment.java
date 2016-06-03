package org.break_out.breakout.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.break_out.breakout.R;
import org.break_out.breakout.model.BOMedia;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Maximilian Dühr on 03.06.2016.
 */
public class SelectedPostingFragment extends Fragment {
    private ArrayList _arrayList_comments;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_posts, container, false);

        return v;
    }


    public class Comment{
        private int remoteId;
        private String firstName;
        private String lastName;
        private String text;
        private BOMedia profileImage;
        public Comment(int remoteId, String text, String firstName, String lastName, BOMedia profileImage){
            this.firstName = firstName;
            this.lastName = lastName;
            this.text = text;
            this.profileImage = profileImage;
            this.remoteId = remoteId;
        }

        public  Comment fromJSON(JSONObject obj){
            return null;
        }
    }
}
