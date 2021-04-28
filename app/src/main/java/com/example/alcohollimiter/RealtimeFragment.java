package com.example.alcohollimiter;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class RealtimeFragment extends Fragment implements View.OnClickListener{
    private View rootView;
    private Context myContext;

    private TimePickerDialog.OnTimeSetListener startTimesetListener;
    private TextView startTimeText;
    int curType = 0;
    int totalMl=0;

    ArrayList<AlcoholTypeData> typeDatas;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_realtime, container, false);
        myContext = container.getContext();

        Button time_starttime_edit_btn = (Button)rootView.findViewById(R.id.time_starttime_edit_btn);
        time_starttime_edit_btn.setOnClickListener(this);

        ImageButton main_bottle_btn = (ImageButton)rootView.findViewById(R.id.main_bottle_btn);
        main_bottle_btn.setOnClickListener(this);

        ImageButton select_1_btn = (ImageButton)rootView.findViewById(R.id.select_1_btn);
        select_1_btn.setOnClickListener(this);
        ImageButton select_2_btn = (ImageButton)rootView.findViewById(R.id.select_2_btn);
        select_2_btn.setOnClickListener(this);
        ImageButton select_3_btn = (ImageButton)rootView.findViewById(R.id.select_3_btn);
        select_3_btn.setOnClickListener(this);
        ImageButton main_drink_btn = (ImageButton)rootView.findViewById(R.id.main_drink_btn);
        main_drink_btn.setOnClickListener(this);
        Button main_total_edit_btn = (Button) rootView.findViewById(R.id.main_total_edit_btn);
        main_total_edit_btn.setOnClickListener(this);

        typeDatas = new ArrayList<AlcoholTypeData>();
        typeDatas.add(new AlcoholTypeData("소주", 17.8, 360, 50, R.drawable.img_soju_bottle));
        typeDatas.add(new AlcoholTypeData("맥주", 4.5, 500, 170, R.drawable.img_beer_bottle));
        typeDatas.add(new AlcoholTypeData("막걸리", 5, 750, 170, R.drawable.img_mak_bottle));

        runStartTime();

        return rootView;
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.main_bottle_btn:{
                BottleDataDialogFragment bddf = BottleDataDialogFragment.getInstance();
                bddf.show(getChildFragmentManager(), BottleDataDialogFragment.TAG_BOTTLEDATA_DIALOG);
            }break;
            case R.id.time_starttime_edit_btn:{
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sd = new SimpleDateFormat("hh mm");
                String getTime = sd.format(date);
                int hh = Integer.parseInt(getTime.split(" ")[0]);
                int mm = Integer.parseInt(getTime.split(" ")[1]);
                Log.i("lim", "gettime" + getTime);
                TimePickerDialog dialog = new TimePickerDialog(myContext, startTimesetListener, hh, mm, true );
                dialog.setTitle("시작시간");
                dialog.show();
            }break;
            case R.id.select_1_btn:{
                setMainType(0);
            }break;
            case R.id.select_2_btn:{
                setMainType(1);
            }break;
            case R.id.select_3_btn:{
                setMainType(2);
            }break;
            case R.id.select_more_btn:{

            }break;
            case R.id.main_drink_btn:{
                Log.i("asdf","asdf");
                setTotal(totalMl + typeDatas.get(curType).janMl);
                calculateBlood();
            }break;
            case R.id.main_total_edit_btn:{
                setTotal(0);
                calculateBlood();
            }
            default:{

            }break;
        }
    }
    void runStartTime() {
        startTimeText = (TextView)rootView.findViewById(R.id.time_starttime_text);
        startTimesetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startTimeText.setText(String.format("%d시간 %d분", hourOfDay, minute));
            }
        };
    }
    void setMainType(int type) {
        curType = type;
        Log.i("lim", Integer.toString(type));
        TextView typeText = (TextView)rootView.findViewById(R.id.main_type_text);
        EditText alcoholEdit = (EditText)rootView.findViewById(R.id.main_alcohol_edit);
        ImageButton bottleImg = (ImageButton)rootView.findViewById(R.id.main_bottle_btn);
        EditText bottlemlEdit = (EditText)rootView.findViewById(R.id.main_bottle_ml_edit);
        TextView janmlText = (TextView)rootView.findViewById(R.id.main_jan_ml_text);

        typeText.setText(typeDatas.get(type).name);
        alcoholEdit.setText(Double.toString(typeDatas.get(type).alcohol));
        bottlemlEdit.setText(Integer.toString(typeDatas.get(type).bottleMl));
        janmlText.setText(Integer.toString(typeDatas.get(type).janMl));
        bottleImg.setImageDrawable(getResources().getDrawable(typeDatas.get(type).imgAdrs, null));
    }

    public class AlcoholTypeData {
        public String name = "진로";
        public double alcohol = 17.8;
        public int bottleMl = 360;
        public int janMl = 50;
        public int imgAdrs = 0;

        public AlcoholTypeData(String _name, double _alcohol, int _bottleMl, int _janMl, int _imgAdrs) {
            name = _name;
            alcohol = _alcohol;
            bottleMl = _bottleMl;
            janMl = _janMl;
            imgAdrs = _imgAdrs;
        }
    }
    public void setTotal(int t){
        EditText totalEdit = (EditText)rootView.findViewById(R.id.main_total_ml_edit);
        totalMl = t;
        totalEdit.setText(Integer.toString(totalMl));
    }
    void calculateBlood(){
        double a = totalMl* (typeDatas.get(curType).alcohol/100.0);
        double pr = 76 *0.86;
        double rst = a*0.7/pr;
        TextView bloodText = (TextView)rootView.findViewById(R.id.blood_level);
        bloodText.setText(String.format("%.3f", rst));
    }
}