package com.avin.lite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.avin.lite.detection.DetectorActivity;

public class HomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
    }

    public void opnTxtExt(View view) {

    }

    public void opnObjDec(View view) {
        startActivity(new Intent(this, DetectorActivity.class));
    }
}
