package com.sfbeef.quicknote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
                String value = (String) map.get(key);
                if(!value.equals("")) {

                    TextView boldDate = new TextView(this);
                    boldDate.setTypeface(null, Typeface.BOLD);
                    boldDate.setText(key);
                    RelativeLayout box = new RelativeLayout(this);

                    TextView txt = new TextView(this);
                    txt.setText(value.trim() + "\n");
                    ImageButton delete = new ImageButton(this);
                    delete.setImageResource(R.drawable.ic_delete_foreground);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams
                            (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    lp.setMargins(0,0,75,0);
                    delete.setLayoutParams(lp);
                    box.addView(txt);
                    box.addView(delete);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            settings.edit().remove(key).apply();
                            list.removeView(boldDate);
                            list.removeView(box);
                        }
                    });
                    list.addView(boldDate);
                    list.addView(box);
                }
            }
        }
        Button about = (Button) findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, AboutActivity.class);
                Log.d("TAG","CLICK");
                startActivity(intent);
            }
        });
    }
}
