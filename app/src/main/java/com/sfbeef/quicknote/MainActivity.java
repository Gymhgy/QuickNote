package com.sfbeef.quicknote;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.sfbeef.quicknote.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private ActivityMainBinding binding;
    private EditText todoText;
    private CalendarView calendar;
    private TextView date_view;
    private ToggleButton curDateToggle;
    private ToggleButton viewList;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    private String dateStr;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("M-d-uuuu"));
        settings = getSharedPreferences("SHARED", Context.MODE_PRIVATE);
        editor = settings.edit();
        date_view = (TextView)findViewById(R.id.date_view);
        date_view.setText(dateStr);
        todoText = (EditText)findViewById(R.id.todoText);
        Button takePic = (Button)findViewById(R.id.camButton);
        calendar = (CalendarView) findViewById(R.id.calendar);
        curDateToggle = (ToggleButton) findViewById(R.id.curDateToggle);
        viewList = (ToggleButton) findViewById(R.id.viewList);
        viewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewList.setChecked(false);
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });
        todoText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(!s.toString().equals("Nothing yet.")) {
                    editor.putString(dateStr, s.toString());
                    editor.apply();
                }
            }
        });

        calendar
                .setOnDateChangeListener(
                        new CalendarView
                                .OnDateChangeListener() {
                            @Override

                            public void onSelectedDayChange(
                                    @NonNull CalendarView view,
                                    int year,
                                    int month,
                                    int dayOfMonth)
                            {

                                dateStr
                                        = (month + 1) + "-"
                                        + dayOfMonth + "-" + year;

                                date_view.setText(dateStr);
                                todoText.setText(settings.getString(dateStr, ""));
                            }
                        });

        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                EditText edit = ((EditText) v);
                Rect outR = new Rect();
                edit.getGlobalVisibleRect(outR);
                Boolean isKeyboardOpen = !outR.contains((int)ev.getRawX(), (int)ev.getRawY());
                if (isKeyboardOpen) {
                    edit.clearFocus();
                    InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
                }

                edit.setCursorVisible(!isKeyboardOpen);

            }
        }
        return super.dispatchTouchEvent(ev);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            InputImage image = InputImage.fromBitmap(photo, 0);

            Task<Text> result = recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onSuccess(Text visionText) {
                    String text = visionText.getText();
                    if(text == "") {
                        //Handle error
                    }
                    else {
                        Log.d("TAG",text);
                        boolean first = true;
                        String dateString = "";
                        String todo = "";
                        LocalDate date = LocalDate.MIN;
                        for(Text.TextBlock block : visionText.getTextBlocks()) {
                            for(Text.Line line : block.getLines()) {
                                if(first) {
                                    if(curDateToggle.isChecked()) {
                                        date = LocalDate.now();
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-d-uuuu");
                                        dateString = date.format(formatter);
                                        todo += line.getText();
                                        todo += "\n";
                                        first = false;
                                    }else {
                                        try {
                                            date = parseDate(line.getText().replace("o", "0").replace(".", ""));
                                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-d-uuuu");
                                            dateString = date.format(formatter);
                                            first = false;
                                        } catch (Exception e) {
                                            continue;
                                        }
                                    }
                                }
                                else {
                                    todo += line.getText();
                                    todo += "\n";
                                }
                            }
                        }
                        if(first) {
                            Toast.makeText(MainActivity.this, Html.fromHtml("<font color='#ff0000' ><b>" +
                                            "Error parsing date" + "</b></font>"),
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        ZonedDateTime zdt = ZonedDateTime.of(date.atStartOfDay(), ZoneId.systemDefault());
                        long milli = zdt.toInstant().toEpochMilli();
                        dateStr = dateString;
                        editor.putString(dateString, todo);
                        editor.apply();
                        date_view.setText(dateString);
                        calendar.setDate(milli);
                        todoText.setText(todo);

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle error
                }
            });


        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static LocalDate parseDate(String str) throws Exception {
        List<DateTimeFormatter> formatsWithYear = new ArrayList<>();
        formatsWithYear.add(DateTimeFormatter.ofPattern("M-d-[uuuu][uu]"));
        formatsWithYear.add(DateTimeFormatter.ofPattern("M/d/[uuuu][uu]"));
        List<DateTimeFormatter> formatsWithoutYear = new ArrayList<>();
        formatsWithoutYear.add(DateTimeFormatter.ofPattern("M-d"));
        formatsWithoutYear.add(DateTimeFormatter.ofPattern("M/d"));
        for(DateTimeFormatter format : formatsWithYear) {
            try {
                LocalDate date = LocalDate.parse(str, format);
                return date;
            } catch (DateTimeParseException dtpe) {
                continue;
            }
        }
        for(DateTimeFormatter format : formatsWithoutYear) {
            try {
                MonthDay mDay = MonthDay.parse(str, format);
                LocalDate date = mDay.atYear(2022);
                return date;
            } catch (DateTimeParseException dtpe) {
                continue;
            }
        }
        throw new Exception();
    }

}