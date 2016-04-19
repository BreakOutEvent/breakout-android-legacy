package org.break_out.breakout;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.orm.SugarContext;

import org.break_out.breakout.manager.BOLocationManager;

import java.util.ArrayList;

//JUST FOR TEST PURPOSES
public class MainActivity extends AppCompatActivity {
    public static String TAG ="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    protected void onResume() {super.onResume();}
}
