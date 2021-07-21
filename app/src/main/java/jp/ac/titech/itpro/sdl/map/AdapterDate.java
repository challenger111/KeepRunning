package jp.ac.titech.itpro.sdl.map;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/16.
 */

public class AdapterDate extends BaseAdapter {

    private Context context;
    private List<Integer> days = new ArrayList<>();
    //日历数据
    private List<Boolean> status = new ArrayList<>();
    FileInputStream fis;
    private String getPass1;
    private String getPass2;
    private String goal;
    private String signed;

    public AdapterDate(Context context) {
        this.context = context;
        int maxDay = DateUtil.getCurrentMonthLastDay();
        for (int i = 0; i < DateUtil.getFirstDayOfMonth() - 1; i++) {
            days.add(0);
            status.add(false);
        }
        for (int i = 0; i < maxDay; i++) {
            days.add(i+1);
            status.add(false);
        }
        try {
            Read();
            Read2();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return days.size();
    }

    @Override
    public Object getItem(int i) {
        return days.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if(view==null){
            view = LayoutInflater.from(context).inflate(R.layout.item_gv,null);
            viewHolder = new ViewHolder();
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tv = view.findViewById(R.id.tvWeek);
        viewHolder.rlItem = view.findViewById(R.id.rlItem);
        viewHolder.ivStatus = view.findViewById(R.id.ivStatus);
        viewHolder.tv.setText(days.get(i)+"");
        Log.d("calendar", String.valueOf(days.get(i)));
        if(days.get(i)==0){
            viewHolder.rlItem.setVisibility(View.GONE);
        }
        if(status.get(i)){
            viewHolder.tv.setTextColor(Color.parseColor("#FD0000"));
            viewHolder.ivStatus.setVisibility(View.VISIBLE);
        }else{
            viewHolder.tv.setTextColor(Color.parseColor("#666666"));
            viewHolder.ivStatus.setVisibility(View.GONE);
        }

        String[] arr=signed.split("\\|");
        for(int k=0;k<arr.length;k++){
            status.set(Integer.parseInt(arr[k]),true);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(status.get(i)){
                    status.set(i, false);
                    notifyDataSetChanged();
                    Toast.makeText(context,"Already sign in!",Toast.LENGTH_SHORT).show();
                }else{
                    if (Float.parseFloat(getPass1)>Float.parseFloat(goal)*1000) {
                        if (Integer.parseInt(getPass2)==days.get(i)) {
                            Toast.makeText(context, "Sign in success!", Toast.LENGTH_SHORT).show();
                            Write(i);
                            status.set(i, true);
                            notifyDataSetChanged();
                        }else {
                            Toast.makeText(context, "Not today!", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context, "You haven't got the goal!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return view;
    }

    class ViewHolder{
        RelativeLayout rlItem;
        TextView tv;
        ImageView ivStatus;
    }

    public void Read() throws IOException {
        File file= new File("/data/data/jp.ac.titech.itpro.sdl.map/files/step.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String tempString = null;
        tempString=reader.readLine();
        String [] arrStr1=tempString.split("\\|");
        getPass1=arrStr1[1];
        getPass2=arrStr1[2];
        file= new File("/data/data/jp.ac.titech.itpro.sdl.map/files/information.txt");
        reader = new BufferedReader(new FileReader(file));
        String tempString1 = null;
        tempString=reader.readLine();
        String [] arrStr2=tempString.split("\\|");
        goal=arrStr2[4];
    }
    public void Write(int i){
        File file= new File("/data/data/jp.ac.titech.itpro.sdl.map/files/sign.txt");
        try {
            FileWriter fw=new FileWriter(file,true  );
            String s=String.valueOf(i)+"|";
            fw.write(s,0,s.length());
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Read2() {
        try {
            File file= new File("/data/data/jp.ac.titech.itpro.sdl.map/files/sign.txt");
            BufferedReader reader = null;
            reader = new BufferedReader(new FileReader(file));
            signed=reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
