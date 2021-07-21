package jp.ac.titech.itpro.sdl.map;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ServiceConnection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

public class Running extends AppCompatActivity implements SensorEventListener {
    private final static String TAG = Running.class.getSimpleName();
    private final static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private final static int REQ_PERMISSIONS = 1234;
    private FusedLocationProviderClient locationClient;
    private LocationRequest request;
    private LocationCallback callback;
    private Location location;
    private double distance=0;
    private Context fContext;
    FileOutputStream fos;
    OutputStreamWriter osw;
    //private static final int sensorTypeC=Sensor.TYPE_STEP_COUNTER;
    //private SensorManager manager;
    private Sensor stepCount;
    private float curStepCount;
    private float oldStep;
    private float newStep;
    private float height;
    private float setSpeed;
    private float lastStep;
    private AlertDialog alert;
    private AlertDialog.Builder builder;
    private int seconds = 0;
    private boolean running = false;
    private boolean wasRunning = false;
    private int timeCount=0;
    private int time1=0,time2=0;
    private double dis1=0,dis2=0;
    private float goal;
    private boolean wasDark;
    private String oldDay;
    private String curDay;
    private Time t;
    private boolean flagUpdate=true;
    private Intent serviceIntent;
    private SensorListener sensorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);


        Log.e(TAG, "onCreate: ");
        //lock running

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Running.class.getName());

        wakeLock.acquire();

        //new service
        serviceIntent = new Intent(this, SensorListener.class);

        //finish
        Button finish=this.findViewById(R.id.b_finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(goal<distance){
                    Dialog al1=new AlertDialog.Builder(Running.this)
                            .setMessage("Congratulations!you can go to sign for today")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent I_toPlan=new Intent(Running.this, Plan.class);
                                    startActivity(I_toPlan);
                                }
                            }).create();
                    al1.show();
                }else {
                    Dialog al2=new AlertDialog.Builder(Running.this)
                            .setMessage("You haven't reach the goal,sure to leave?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent I_toPlan=new Intent(Running.this, MainActivity.class);
                                    startActivity(I_toPlan);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create();
                    al2.show();
                }
            }
        });

        //some value

        //float format
        DecimalFormat format = new DecimalFormat("#.##");
        //time keeper
        running = true;
        if(savedInstanceState!=null){
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }
        runTime();
        //get height
        try {
            FileInputStream fis = Running.this.openFileInput("step.txt");
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            char[] input = new char[fis.available()];
            isr.read(input);
            isr.close();
            fis.close();
            String rawStr1=String.valueOf(input);
            String [] arrStr1=rawStr1.split("\\|");
            //get time
            t=new Time();
            t.setToNow();
            oldDay=arrStr1[2];
            curDay=String.valueOf(t.monthDay);
            oldStep=Float.parseFloat(arrStr1[0]);

            if (!oldDay.equals(curDay)){
                Log.e(TAG, "day pass" );
                flagUpdate=false;
                try {
                    fos=Running.this.openFileOutput("step.txt",MODE_PRIVATE);
                    osw = new OutputStreamWriter(fos, "UTF-8");
                    osw.write(String.valueOf(oldStep) + "|");
                    osw.write(String.valueOf(0)+"|");
                    osw.write(t.monthDay+"|");
                    osw.flush();
                    osw.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            fis = Running.this.openFileInput("step.txt");
            isr = new InputStreamReader(fis, "UTF-8");
            input = new char[fis.available()];
            isr.read(input);
            isr.close();
            fis.close();
            rawStr1=String.valueOf(input);
            arrStr1=rawStr1.split("\\|");
            lastStep=Float.parseFloat(arrStr1[0]);
            distance=Float.parseFloat(arrStr1[1]);
            Log.e(TAG, "distance:~~~~"+String.valueOf(distance) );
            fis = Running.this.openFileInput("information.txt");
            isr = new InputStreamReader(fis, "UTF-8");
            input = new char[fis.available()];
            isr.read(input);
            isr.close();
            fis.close();
            String rawStr=String.valueOf(input);
            String [] arrStr=rawStr.split("\\|");
            height=(Float.parseFloat(arrStr[0]))/100;
            setSpeed=Float.parseFloat(arrStr[5]);
            goal=Float.parseFloat(arrStr[4])*1000;

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //set initial mil
        TextView mil=(TextView) findViewById(R.id.mileage);
        if (distance!=0) {
            mil.setText(format.format(distance / 1000));
        }else {
            mil.setText("0.00");
        }
        //button pause
        Button pause=(Button)findViewById(R.id.b_pause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "pauseClick: " );
                Intent I_Pause=new Intent(Running.this, PauseDialog.class);
                startActivity(I_Pause);
            }
        });

        //step sensor

        //file write test
        try {
            fos = Running.this.openFileOutput("data.txt", Context.MODE_APPEND);
            osw = new OutputStreamWriter(fos, "UTF-8");
            Log.d(TAG, "file");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }



        //create client
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        request=LocationRequest.create();
        request.setInterval(2000);
        request.setFastestInterval(1000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.e(TAG, "client create");
        location=new Location("1");
        location.setLatitude(0);

        //get lat lo
        callback=new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (location.getLatitude()!=0){
                    Location newLocation=locationResult.getLastLocation();
                    double lat1=location.getLatitude();
                    double lon1=location.getLongitude();
                    double lat2=newLocation.getLatitude();
                    double lon2=newLocation.getLongitude();
                    double addDistance=0;
                    newStep=curStepCount;
                    if (Math.abs(lat1-lat2)>0.000001 || Math.abs(lon1-lon2)>0.000001){
                        addDistance=0.43*height*(newStep-oldStep);
                    }
                    oldStep=newStep;
                    distance=distance+addDistance;
                    Log.d(TAG, "onLocationResult: "+String.valueOf(distance));
                    if (distance>=0.01) {
                        mil.setText(format.format(distance / 1000));
                    }else {
                        mil.setText("0.00");
                    }

                    //set speed
                    TextView s_speed=(TextView)findViewById(R.id.show_speed);

                    if (timeCount==0){
                        time1=seconds;
                        dis1=distance;
                        Log.d(TAG, "first dis: "+String.valueOf(dis1));
                    }
                    if (timeCount%1==0&&timeCount!=0){
                        time2=seconds;
                        dis2=distance;
                        double speed=(dis2-dis1)/(time2-time1);
                        if (speed>setSpeed*1.2){
                            Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                        }
                        if (speed<setSpeed*0.8){
                            Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
                            vibrator.vibrate(200);
                        }
                        if (speed==0){
                            s_speed.setText("0.00m/s");
                        }else {
                            s_speed.setText(format.format(speed)+"m/s");
                        }
                        time1=time2;
                        dis1=dis2;
                    }
                    timeCount=timeCount+1;
                }
                else {
                    Log.e(TAG, "onLocationResult2: " );
                    oldStep=lastStep;
                    curStepCount=lastStep;
                    Log.e(TAG, "oldstep: "+String.valueOf(oldStep) );
                    location=locationResult.getLastLocation();
                }
            }
        };

        wakeLock.release();
        wakeLock = null;
    }


    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            sensorListener = ((SensorListener.MsgBinder)service).getService();

            sensorListener.setOnStepInform(new OnStepListener() {
                @Override
                public void onSteps(float steps) {
                    Log.d(TAG, "onInform: ");
                    curStepCount=steps;
                }
            });

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (!wasDark) {
            wasRunning = true;
        }else {
            wasDark=false;
        }
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (!wasDark) {
            Log.d(TAG, "onResume");
            if (wasRunning) {
                running = true;
                Log.d(TAG, "running true ");
            }
            bindService(serviceIntent,conn,Context.BIND_AUTO_CREATE);
            Log.d(TAG, "onResume: bind service");
            //manager.registerListener(this, stepCount, SensorManager.SENSOR_DELAY_FASTEST);
            startLocationUpdate(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (isScreenOn) {
            Log.d(TAG, "onPause");
            wasRunning = running;
            running = false;
            //manager.unregisterListener(this);
            unbindService(conn);
            locationClient.removeLocationUpdates(callback);
        }else {
            wasDark=true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (isScreenOn) {
            Log.d(TAG, "onStop");
            wasRunning = running;
            running = false;
            try {
                fos.flush();
                fos.close();
                osw.close();
                fos = Running.this.openFileOutput("step.txt", MODE_PRIVATE);
                osw = new OutputStreamWriter(fos, "UTF-8");
                osw.write(String.valueOf(curStepCount) + "|");
                osw.write(String.valueOf(distance) + "|");
                Time t = new Time();
                t.setToNow();
                osw.write(t.monthDay + "|");
                osw.flush();
                osw.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            wasDark=true;
        }
    }



    //distance in meter
    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double latA = Math.toRadians(lat1);
        double lonA = Math.toRadians(lon1);
        double latB = Math.toRadians(lat2);
        double lonB = Math.toRadians(lon2);
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
       // double cosAng = (Math.cos(latA) * Math.cos(latB) * Math.cos(lonB-lonA)) +
      //          (Math.sin(latA) * Math.sin(latB));
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(latA) * Math.cos(latB) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c= 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = c *6378137;
        return dist;
    }

    private void startLocationUpdate(boolean reqPermission) {
        Log.d(TAG, "startLocationUpdate");
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                if (reqPermission) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS, REQ_PERMISSIONS);
                } else {
                    String text = getString(R.string.toast_requires_permission_format, permission);
                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
        locationClient.requestLocationUpdates(request, callback, null);
    }



    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {

        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putInt("seconds",seconds);
        saveInstanceState.putBoolean("running",running);
        saveInstanceState.putBoolean("wasRunning",wasRunning);
    }

    private void runTime(){
        final Handler handler = new Handler();
        handler.post(new Runnable() {
                         @Override
                         public void run() {
                             TextView textView = findViewById(R.id.show_time);
                             int minute = seconds%3600/60;
                             String time = String.format("%02d:%02d",minute,seconds%60);
                             textView.setText(time);
                             if(running) seconds++;
                             handler.postDelayed(this,1000);
                         }
                     }
        );

    }
}
