package com.avin.intelliscan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.avin.intelliscan.detection.DetectorActivity;
import com.avin.intelliscan.texttrac.MainActivity;

public class HomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
    }

    public void opnTxtExt(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void opnObjDec(View view) {
        startActivity(new Intent(this, DetectorActivity.class));
    }
}
