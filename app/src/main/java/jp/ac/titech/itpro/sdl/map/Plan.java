package jp.ac.titech.itpro.sdl.map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Plan extends AppCompatActivity {
    SignDate signDate;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("wqf","Success");
        setContentView(R.layout.activity_plan);
        signDate=(SignDate)this.findViewById(R.id.signDate);
        Button toInformation=(Button)this.findViewById(R.id.b_information);
        toInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  I_toInformation=new Intent(Plan.this,Information.class);
                startActivity(I_toInformation);
            }
        });
    }
}
