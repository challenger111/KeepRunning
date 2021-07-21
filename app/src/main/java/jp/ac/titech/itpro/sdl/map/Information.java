package jp.ac.titech.itpro.sdl.map;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class Information extends AppCompatActivity {
    private final static String TAG = Running.class.getSimpleName();
    FileOutputStream fos;
    OutputStreamWriter osw;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);


        EditText i1=this.findViewById(R.id.e_height);
        EditText i2=this.findViewById(R.id.e_weight);
        RadioButton i3=this.findViewById(R.id.btnMan);
        EditText i4=this.findViewById(R.id.e_age);
        EditText i5=this.findViewById(R.id.e_goal);
        EditText i6=this.findViewById(R.id.e_speed);



        Button store=(Button) this.findViewById(R.id.b_store);

        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float v1=Float.parseFloat(i1.getText().toString());
                float v2=Float.parseFloat(i2.getText().toString());
                float v3=1;
                if (i3.getText().toString()=="true"){
                    v3=0;
                }
                float v4=Float.parseFloat(i4.getText().toString());
                float v5=Float.parseFloat(i5.getText().toString());
                float v6=Float.parseFloat(i6.getText().toString());
                int flag1=1,flag2=1,flag3=1,flag4=1,flag5=1;
                //test
                Log.e(TAG, String.valueOf(v1) );
                if (v1>250 || v1<100) {
                    Toast.makeText(getBaseContext(), "please enter height 100-250", Toast.LENGTH_LONG).show();
                    flag1=0;
                }
                if (v2>200 || v2<30){
                    Toast.makeText(getBaseContext(), "please enter weight 30-250", Toast.LENGTH_LONG).show();
                    flag2=0;
                }
                if (v4>80 || v4<6){
                    Toast.makeText(getBaseContext(), "please enter age 6-80", Toast.LENGTH_LONG).show();
                    flag3=0;
                }
                if (v5>30 || v5<0){
                    Toast.makeText(getBaseContext(), "please enter distance 0-30", Toast.LENGTH_LONG).show();
                    flag4=0;
                }
                if (v6>8||v6<0){
                    Toast.makeText(getBaseContext(), "please enter speed 0-7", Toast.LENGTH_LONG).show();
                    flag4=0;
                }
                if((flag1*flag2*flag3*flag4*flag5)==1){
                    //file read/write
                    try {
                        fos = Information.this.openFileOutput("information.txt", Context.MODE_PRIVATE);
                        osw = new OutputStreamWriter(fos, "UTF-8");
                    } catch (FileNotFoundException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    try {
                        osw.write(String.valueOf(v1)+"|");
                        osw.write(String.valueOf(v2)+"|");
                        osw.write(String.valueOf(v3)+"|");
                        osw.write(String.valueOf(v4)+"|");
                        osw.write(String.valueOf(v5)+"|");
                        osw.write(String.valueOf(v6)+"|");
                        osw.flush();
                        osw.close();
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //pre-load value
        EditText i1=this.findViewById(R.id.e_height);
        EditText i2=this.findViewById(R.id.e_weight);
        RadioButton i3=this.findViewById(R.id.btnMan);
        EditText i4=this.findViewById(R.id.e_age);
        EditText i5=this.findViewById(R.id.e_goal);
        try {
            FileInputStream fis = Information.this.openFileInput("information.txt");
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            Log.e(TAG, "file read " );
            char[] input = new char[fis.available()];
            isr.read(input);
            isr.close();
            fis.close();
            String rawStr=String.valueOf(input);
            String [] arrStr=rawStr.split("\\|");
            i1.setText(arrStr[0]);
            i2.setText(arrStr[1]);
            i4.setText(arrStr[3]);
            i5.setText(arrStr[4]);

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
