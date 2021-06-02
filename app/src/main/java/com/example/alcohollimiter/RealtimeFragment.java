package com.example.alcohollimiter;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class RealtimeFragment extends Fragment implements View.OnClickListener{
    private View rootView;
    private Context myContext;
    private TimePickerDialog.OnTimeSetListener startTimesetListener;
    TextView startTimeText;
    TextView elapsedTimeText;
    TextView tickerText;
    View elapsedView;
    CounterTask counterTask;
    int totalMl=0;
    SQLiteDatabase db;
    ArrayList<LiquorType> liquorTypes;
    int[] selectBtnOfIds = {0,1,2};
    int curLiquorTypeId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onViewStateRestored(Bundle savedInstanceState){
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState == null)
            return;
        Log.i("songjo", "재생성");
        long startTime = savedInstanceState.getLong("startTime");
        boolean isStart = savedInstanceState.getBoolean("isStart");
        counterTask = new CounterTask();
        counterTask.execute();
        counterTask.restore(startTime, isStart);
    }
    @Override
    public void onSaveInstanceState (Bundle outState){
        super.onSaveInstanceState(outState);

        outState.putLong("startTime", counterTask.getStartTime());
        outState.putBoolean("isStart", counterTask.getIsStart());
        Log.i("songjo", "저장하기");
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

        startTimesetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);
                counterTask.setStartTime(cal.getTimeInMillis());
            }
        };
        counterTask = new CounterTask();
        counterTask.execute();

        liquorTypes = new ArrayList<LiquorType>();
        DBHelper helper = new DBHelper(myContext);
        try {
            db = helper.getWritableDatabase();
        } catch (SQLiteException e){
            db = helper.getReadableDatabase();
        }
       // helper.onUpgrade(db, 0, 0);

        Cursor cursor;
        cursor = db.rawQuery("SELECT * from liquors", null);
        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            double abv = cursor.getFloat(2);
            int botml = cursor.getInt(3);
            int janml = cursor.getInt(4);
            int kcal = cursor.getInt(5);
            liquorTypes.add(new LiquorType(id, name, abv, botml, janml, kcal));
            Log.i("songjo", Integer.toString(id));
        }
        Log.i("songjo", "onCreate");
        return rootView;
    }
    void selectLiquor(int order) {
        curLiquorTypeId = selectBtnOfIds[order];
        LiquorType lt = liquorTypes.get(curLiquorTypeId);

        TextView typeText = (TextView)rootView.findViewById(R.id.main_type_text);
        EditText alcoholEdit = (EditText)rootView.findViewById(R.id.main_alcohol_edit);
        ImageButton bottleImg = (ImageButton)rootView.findViewById(R.id.main_bottle_btn);
        ImageButton drinkBtn = (ImageButton)rootView.findViewById(R.id.main_drink_btn);
        EditText bottlemlEdit = (EditText)rootView.findViewById(R.id.main_bottle_ml_edit);
        TextView janmlText = (TextView)rootView.findViewById(R.id.main_jan_ml_text);

        typeText.setText(lt.name);
        alcoholEdit.setText(Double.toString(lt.abv));
        bottlemlEdit.setText(Integer.toString(lt.bottleMl));
        janmlText.setText(Integer.toString(lt.janMl));
        bottleImg.setImageDrawable(getResources().getDrawable(lt.bottleImg, null));
        drinkBtn.setImageDrawable(getResources().getDrawable(lt.janImg, null));
    }
    public void setTotal(int t){
        EditText totalEdit = (EditText)rootView.findViewById(R.id.main_total_ml_edit);
        totalMl = t;
        totalEdit.setText(Integer.toString(totalMl));
    }
    void calculateBlood(){
        double abv = liquorTypes.get(curLiquorTypeId).abv;
        double a = totalMl* (abv/100.0);
        double pr = 76 *0.86;
        double rst = a*0.7/pr;
        TextView bloodText = (TextView)rootView.findViewById(R.id.blood_level);
        bloodText.setText(String.format("%.3f", rst));
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
                selectLiquor(0);
            }break;
            case R.id.select_2_btn:{
                selectLiquor(1);
            }break;
            case R.id.select_3_btn:{
                selectLiquor(2);
            }break;
            case R.id.select_more_btn:{

            }break;
            case R.id.main_drink_btn:{
                setTotal(totalMl + liquorTypes.get(curLiquorTypeId).janMl);
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
    // 술데이터 타입
    public class LiquorType {
        public int id = -1;
        public String name = "진로";
        public double abv = 17.8;
        public int bottleMl = 360;
        public int janMl = 50;
        public int kcal = 90;
        public int bottleImg = 0;
        public int janImg = 0;

        public LiquorType(int _id, String _name, double _abv, int _bottleMl, int _janMl, int _kcal) {
            id = _id;
            name = _name;
            abv = _abv;
            bottleMl = _bottleMl;
            janMl = _janMl;
            kcal = _kcal;
            setImgById();
        }
        void setImgById(){
            janImg = R.drawable.img_jan_beer_180;
            switch (id){
                case 1:{ bottleImg = R.drawable.img_bottle_soju_360; janImg = R.drawable.img_jan_soju_50; }break;
                case 2:{ bottleImg = R.drawable.img_bottle_beer_500; }break;
                case 3:{ bottleImg = R.drawable.img_bottle_mak_750; janImg = R.drawable.img_jan_mak_200;}break;
                case 4:{ bottleImg = R.drawable.img_bottle_wine; }break;
                case 5:{ bottleImg = R.drawable.img_bottle_vodka; }break;
                case 6:{ bottleImg = R.drawable.img_bottle_wisky; }break;
                default:{ bottleImg = R.drawable.img_bottle_mix; }break;
            }
        }
    }
    // 카운터 어싱크 테스크 쓰래드
    public class CounterTask extends AsyncTask<Integer, Integer, Integer> {
        long startTime;
        boolean termination = false;
        boolean isStart = false;
        long tickerCount = 0;
        int[] tickerColors = {R.color.lim_bgreen, R.color.lim_red,R.color.lim_bgreen_d, R.color.lim_text_black};
        final int tickerColorN = tickerColors.length;
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
                startTime = now;
                startTimeText.setText(startTimeFormat.format(new Date(startTime)));
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
            Log.i("songjo","테스크 종료됨");
        }
        public void setEnd() {
            isStart= false;
            startTime = System.currentTimeMillis();
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
        public void restore(long _startTime, boolean _isStart){
            Log.i("songjo", String.format("%l, %b", _startTime, _isStart));
            if(_isStart)
                setStartTime(_startTime);
        }
        public long getStartTime() {
            return startTime;
        }
        public long getElapsedTime() {
            return System.currentTimeMillis() - startTime + 60000;
        }
        public boolean getIsStart() {
            return isStart;
        }
        public void exit() {
            termination = true;
        }
    }

}