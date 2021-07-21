package jp.ac.titech.itpro.sdl.map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "mainactivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button toRunning=(Button) this.findViewById(R.id.b_start);
        //start buttton
        toRunning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I_toRunning=new Intent(MainActivity.this, Running.class);
                startActivity(I_toRunning);
            }
        });
        //plan button
        Button toPlan=(Button) this.findViewById((R.id.b_plan));

        toPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I_toPlan=new Intent(MainActivity.this, Plan.class);
                startActivity(I_toPlan);
            }
        });


    }

}