package com.sfbeef.quicknote;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {
    String sfbeef = "\r\n   ________  ___  ____________\r\n  / __/ __/ / _ )/ __/ __/ __/\r\n _\\ \\/ _/  / _  / _// _// _/  \r\n/___/_/   /____/___/___/_/    \r\n                              ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView sfb = (TextView)findViewById(R.id.sfbeef);
        sfb.setText(sfbeef);
        sfb.setHorizontallyScrolling(true);

    }
}