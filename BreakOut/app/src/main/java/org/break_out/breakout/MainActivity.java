package org.break_out.breakout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends BOActivity {
    private Button _button_requestLocationPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _button_requestLocationPermission = (Button) findViewById(R.id.main_button_requestPermissionLocation);

        _button_requestLocationPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForPermission(REQUESTCODE_LOCATION, new PermissionCallback() {
                    @Override
                    public void onResult(boolean granted) {
                        Toast.makeText(MainActivity.this, "Granted: " + granted, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
