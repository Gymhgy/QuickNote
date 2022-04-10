package com.sfbeef.quicknote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class ListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        LinearLayout list = (LinearLayout) findViewById(R.id.list);
        SharedPreferences settings = getSharedPreferences("SHARED", Context.MODE_PRIVATE);
        Map<String,?> map = settings.getAll();
        Log.d("TAG", map.toString());
        if(map!=null) {
            SortedSet<String> keys = new TreeSet<>(map.keySet());

            for (String key : keys) {
                TextView boldDate = new TextView(this);
                boldDate.setTypeface(null, Typeface.BOLD);
                boldDate.setText(key);
                String value = (String) map.get(key);
                TextView txt = new TextView(this);
                txt.setText(value);
                if(!value.equals("")) {
                    list.addView(boldDate);
                    list.addView(txt);
                }
            }
        }
    }
}
