package com.inclass.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rvalerio.fgchecker.AppChecker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.rvalerio.fgchecker.Utils.hasUsageStatsPermission;

public class MainActivity extends AppCompatActivity {
    Button btnStopService,btnStartClass,btnStartTest,btUsagePermission;
    TextView startTime,stopTime,activeTime,demoversion,attendance,exam;
    private AppChecker appChecker;
    private SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS="new";
    public static final String ex="Switch";
    public static final String start="start";
    public static final String stop="stop";
    public static final String active="active";
    public static int count=0;
    private String a1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            Toast.makeText(this, "Grant Permission and restart app", Toast.LENGTH_SHORT).show();
        }
        btUsagePermission = (Button) findViewById(R.id.usage_permission);

        if(!needsUsageStatsPermission()) {
            btUsagePermission.setVisibility(View.GONE);
        } else {
            btUsagePermission.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestUsageStatsPermission();
                }
            });
        }
        btnStopService = findViewById(R.id.buttonStopService);
        btnStartClass=findViewById(R.id.buttonStartClass);
        btnStartTest=findViewById(R.id.buttonStartTest);
        startTime=findViewById(R.id.starttime);
        stopTime=findViewById(R.id.stoptime);
        activeTime=findViewById(R.id.activetime);
        demoversion=findViewById(R.id.textView5);
        //attendance.findViewById(R.id.attendance);
        //exam.findViewById(R.id.exam);
        btnStopService.setVisibility(View.GONE);

        if(needsUsageStatsPermission()){
            btnStopService.setVisibility(View.GONE);
            btnStartClass.setVisibility(View.GONE);
            btnStartTest.setVisibility(View.GONE);
            startTime.setVisibility(View.GONE);
            stopTime.setVisibility(View.GONE);
            activeTime.setVisibility(View.GONE);
            demoversion.setText("Restart after providing permissions");
            //attendance.setVisibility(View.GONE);
            //exam.setVisibility(View.GONE);
        }
        sharedPreferences = getSharedPreferences(" ",MODE_PRIVATE);
        final SharedPreferences.Editor editor= sharedPreferences.edit();

        startTime.setText("Session Started: "+(sharedPreferences.getString(start,"")));
        stopTime.setText("Session Ended: "+(sharedPreferences.getString(stop,"")));
        loadData();
        activeTime.setText(a1);
        if(sharedPreferences.getBoolean("run",false)==true){
            btnStopService.setVisibility(View.VISIBLE);
            btnStartClass.setVisibility(View.GONE);
        }

        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService();
                editor.putString(stop,getCurrentTime());
                stopTime.setText("Session Stopped: "+getCurrentTime());
                editor.putBoolean("run",false);
                editor.apply();
                Toast.makeText(getApplicationContext(),"Data sent to host", Toast.LENGTH_SHORT).show();
                btnStartClass.setVisibility(View.VISIBLE);
                btnStopService.setVisibility(View.INVISIBLE);
                loadData();
                activeTime.setText(a1);
            }
        });
        btnStartClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //btnStopService.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), "APP WILL RUN IN BACKGROUND", Toast.LENGTH_SHORT).show();
                startService();
                startTime.setText("Session Started: "+getCurrentTime());
                stopTime.setText("Session Ended:");
                editor.putString(start,getCurrentTime());
                editor.putBoolean("run",true);
                editor.apply();
                btnStartClass.setVisibility(View.GONE);
                activeTime.setText("Active :");
                saveData("0");
                //startChecker();
                Toast.makeText(getApplicationContext(),"YOU CAN JOIN THE CLASS NOW", Toast.LENGTH_SHORT).show();
                btnStopService.setVisibility(View.VISIBLE);
            }
        });
        btnStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),SecondActivity.class);
                startActivity(i);
            }
        });
    }

    private boolean needsUsageStatsPermission() {
        return postLollipop() && !hasUsageStatsPermission(this);
    }
    private boolean postLollipop() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
    private void requestUsageStatsPermission() {
        if(!hasUsageStatsPermission(this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }
    public void startService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "");
        ContextCompat.startForegroundService(this, serviceIntent);
    }
    public void stopService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
    }

    public void saveData(String s1){
        String s2="Active: "+s1+"m";
        SharedPreferences sharedPreferences= getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editer = sharedPreferences.edit();
        editer.putString(active, s2);
        editer.apply();
    }

    public static String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }
    public void loadData(){
        SharedPreferences sharedPreferences= getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        a1=sharedPreferences.getString(active,"");
    }
}