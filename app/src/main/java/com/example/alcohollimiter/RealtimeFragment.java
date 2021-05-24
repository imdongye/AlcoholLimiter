package com.example.alcohollimiter;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


public class RealtimeFragment extends Fragment implements View.OnClickListener{
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
    TextView startTimeText;
    TextView elapsedTimeText;
    TextView tickerText;
    View elapsedView;
    int[] tickerColors = {R.color.lim_bgreen, R.color.lim_red,R.color.lim_bgreen_d, R.color.lim_text_black};
    final int tickerColorN = tickerColors.length;
    public class CounterTask extends AsyncTask<Integer, Integer, Integer> {
        long startTime;
        boolean termination = false;
        boolean isStart = false;
        long tickerCount = 0;
        SimpleDateFormat startTimeFormat=new SimpleDateFormat("a h시 m분");
        @Override
        protected void onPreExecute() { // 1. UI thread
            super.onPreExecute();
            startTimeText.setText(startTimeFormat.format(new Date(System.currentTimeMillis())));
        }
        @Override
        protected Integer doInBackground(Integer... value) { // 2. worker thread
            while(!termination){
                try {
                    Thread.sleep(1000);
                }catch(InterruptedException e){}
                publishProgress();
            };
            return 0;
        }
        @Override
        protected void onProgressUpdate(Integer... value) { // 3. UI thread
            super.onProgressUpdate();
            long now = System.currentTimeMillis();
            if(!isStart) { // stop
                startTimeText.setText(startTimeFormat.format(new Date(now)));
            }
            else { // start
                long eTime = getElapsedTime();
                long mm = (eTime/(60000))%60;
                long hh = eTime/(60*60*1000);
                elapsedTimeText.setText((hh != 0 ) ? String.format("%d시간 %d분 째", hh, mm) :String.format("%d분 째", mm));
                int cn = (int)((tickerCount/2)%tickerColorN);
                tickerText.setTextColor(ContextCompat.getColor(myContext, tickerColors[cn]));
                tickerText.setVisibility((tickerCount%2 == 0) ? View.VISIBLE : View.INVISIBLE);
                tickerCount++;
            }
        }
        @Override
        protected void onPostExecute(Integer result) { // 4. UI thread
            super.onPostExecute(result);
        }
        public void setEnd() {
            isStart= false;
            refreshUI();
        }
        public void setStartTime(long _startTime) {
            long cTime = System.currentTimeMillis();
            startTime = (_startTime > cTime) ? cTime : _startTime;
            isStart = true;
            refreshUI();
        }
        void refreshUI() {
            startTimeText.setText(startTimeFormat.format(new Date(startTime)));
            long eTime = getElapsedTime();
            long mm = (eTime/(60000))%60;
            long hh = eTime/(60*60*1000);
            elapsedTimeText.setText((hh != 0 ) ? String.format("%d시간 %d분 째", hh, mm) :String.format("%d분 째", mm));
            elapsedView.setVisibility((isStart) ? View.VISIBLE : View.GONE);
        }
        public long getStartTime() {
            return startTime;
        }
        public long getElapsedTime() {
            return System.currentTimeMillis() - startTime;
        }
        public boolean getIsStart() {
            return isStart;
        }
    }

    private View rootView;
    private Context myContext;

    private TimePickerDialog.OnTimeSetListener startTimesetListener;
    CounterTask counterTask;
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

        //시작시간패널, 카운트시간패널
        Button starttime_edit_btn = (Button)rootView.findViewById(R.id.realtime_starttime_edit_btn);
        starttime_edit_btn.setOnClickListener(this);
        Button start_btn = (Button)rootView.findViewById(R.id.realtime_start_btn);
        start_btn.setOnClickListener(this);
        Button stop_btn = (Button)rootView.findViewById(R.id.realtime_stop_btn);
        stop_btn.setOnClickListener(this);
        startTimeText = (TextView)rootView.findViewById(R.id.realtime_starttime_text);
        elapsedTimeText = (TextView)rootView.findViewById(R.id.realtime_elapsed_time_text);
        tickerText = (TextView)rootView.findViewById(R.id.realtime_elapsed_ticker);
        elapsedView = rootView.findViewById(R.id.realtime_elapsed_view);
        counterTask = new CounterTask();
        counterTask.execute();
        startTimesetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);
                counterTask.setStartTime(cal.getTimeInMillis());
            }
        };

        return rootView;
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.realtime_starttime_edit_btn:{
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sd = new SimpleDateFormat("HH mm");
                String getTime = sd.format(date);
                int hh = Integer.parseInt(getTime.split(" ")[0]);
                int mm = Integer.parseInt(getTime.split(" ")[1]);
                TimePickerDialog dialog = new TimePickerDialog(myContext, AlertDialog.THEME_HOLO_LIGHT, startTimesetListener, hh, mm, false );
                dialog.setTitle("시작시간");
                dialog.show();

            }break;
            case R.id.realtime_start_btn:{
                rootView.findViewById(R.id.realtime_start_btn).setVisibility(View.GONE);
                rootView.findViewById(R.id.realtime_starttime_edit_btn).setVisibility(View.VISIBLE);
                counterTask.setStartTime(System.currentTimeMillis());
            }break;
            case R.id.realtime_stop_btn:{
                rootView.findViewById(R.id.realtime_start_btn).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.realtime_starttime_edit_btn).setVisibility(View.GONE);
                counterTask.setEnd();
            }break;
            case R.id.main_bottle_btn:{
                BottleDataDialogFragment bddf = BottleDataDialogFragment.getInstance();
                bddf.show(getChildFragmentManager(), BottleDataDialogFragment.TAG_BOTTLEDATA_DIALOG);
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
    void setMainType(int type) {
        curType = type;
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