package jp.ac.titech.itpro.sdl.map;


import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class SensorListener extends Service implements SensorEventListener {
    private float steps;
    private static final int sensorTypeC=Sensor.TYPE_STEP_COUNTER;
    private SensorManager manager;
    private Sensor stepCount;
    private OnStepListener onStepInform;
    private final static String TAG = Running.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MsgBinder();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        steps=event.values[0];
        if(onStepInform != null){
            onStepInform.onSteps(steps);
        }
        Log.e("TAG", "onSensorChanged:cur "+String.valueOf(steps) );
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        SensorRegister();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SensorUnregister();
    }

    public void SensorRegister(){
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (manager == null) {
            Toast.makeText(this, "step1", Toast.LENGTH_LONG).show();
            return;
        }
        stepCount = manager.getDefaultSensor(sensorTypeC);
        if (stepCount == null) {
            Toast.makeText(this, "step2", Toast.LENGTH_LONG).show();
        }
        manager.registerListener(this,stepCount, SensorManager.SENSOR_DELAY_FASTEST);
        Log.d(TAG, "SensorRegister: created");
    }


    public void SensorUnregister(){
        manager.unregisterListener(this);
    }

    public void setOnStepInform(OnStepListener onStepListener){
        this.onStepInform= onStepListener;
    }
    public float getSteps() {
        return steps;
    }
    public class MsgBinder extends Binder {
        public SensorListener getService(){
            return SensorListener.this;
        }
    }
}
