package com.murach.runtracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

/**
 * Created by Julz on 6/7/2015.
 */
public class StartMenuActivity extends Activity implements OnClickListener {

    private Button stopwatchButton;
    private Button mapButton;
    //Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_menu);

        // get references to widgets
        stopwatchButton = (Button) findViewById(R.id.stopwatchButton);
        mapButton = (Button) findViewById(R.id.mapButton);

        // set listeners
        stopwatchButton.setOnClickListener(this);
        mapButton.setOnClickListener(this);

        /*final Context c = this;

        stopwatchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(c, StopwatchActivity.class));
                finish();
            }
        });
        mapButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(c, RunMapActivity.class));
                finish();
            }
        });*/
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.stopwatchButton:
                Intent watchIntent = new Intent(StartMenuActivity.this, StopwatchActivity.class);
                StartMenuActivity.this.startActivity(watchIntent);
                break;
            case R.id.mapButton:
                Intent mapIntent = new Intent(StartMenuActivity.this, RunMapActivity.class);
                StartMenuActivity.this.startActivity(mapIntent);
                break;
        }
    }
}
