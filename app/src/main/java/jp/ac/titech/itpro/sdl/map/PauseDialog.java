package jp.ac.titech.itpro.sdl.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PauseDialog extends Activity {
    public static PauseDialog instance = null;
    private TextView comfirm_textview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pausedialog);
        this.setFinishOnTouchOutside(false);
        getWindow().setDimAmount(0f);
        instance = this;
        initView();
    }
    private void initView() {
        comfirm_textview = (TextView) this.findViewById(R.id.comfirm_textview);
        comfirm_textview.setClickable(true);
        comfirm_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View paramView) {
                Intent I_Pause=new Intent(PauseDialog.this, PauseDialog.class);
                startActivity(I_Pause);
                finish();
            }
        });
    }
}

