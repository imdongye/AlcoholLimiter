package com.example.alcohollimiter;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;


public class RealtimeFragment extends Fragment implements View.OnClickListener, View.OnKeyListener{
    final static String[] KorOneCount = {"","한","두","세","네","다섯","여섯","일곱","여덟","아홉"};
    final static String[] KorTenCount = {"","열","스물","서른","마흔","쉰","예순","일흔","여든","아흔"};
    final static int[] faceImgIds = {R.drawable.img_face_1,R.drawable.img_face_2,R.drawable.img_face_3,
            R.drawable.img_face_4,R.drawable.img_face_5,R.drawable.img_face_6,R.drawable.img_face_7};

    private View rootView;
    private Context myContext;
    private TimePickerDialog.OnTimeSetListener startTimesetListener;
    TextView startTimeText;
    TextView elapsedTimeText;
    TextView tickerText;
    TextView lastDrinkTimeText;
    View elapsedView;
    CounterTask counterTask;

    int totalMl=0;
    int totalKcal = 0;
    DBHelper helper;
    SQLiteDatabase db;
    ArrayList<LiquorType> liquorTypes;
    int[] selectBtnOfIds = {0,1,2};
    int curLiquorTypeId = 0;

    int faceLevel = 0;


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
        myContext = getActivity().getApplicationContext();

        // 술 종류 데이터 가져오기
        liquorTypes = new ArrayList<LiquorType>();
        helper = new DBHelper(myContext);
        try {
            db = helper.getWritableDatabase();
        } catch (SQLiteException e){
            db = helper.getReadableDatabase();
        }
        getLiquorTypeInDB();
        selectLiquor(0);


        // 버튼연결
        rootView.findViewById(R.id.realtime_start_btn).setOnClickListener(this);
        rootView.findViewById(R.id.realtime_starttime_edit_btn).setOnClickListener(this);
        rootView.findViewById(R.id.realtime_stop_btn).setOnClickListener(this);

        rootView.findViewById(R.id.select_1_btn).setOnClickListener(this);
        rootView.findViewById(R.id.select_2_btn).setOnClickListener(this);
        rootView.findViewById(R.id.select_3_btn).setOnClickListener(this);
        rootView.findViewById(R.id.select_more_btn).setOnClickListener(this);

        rootView.findViewById(R.id.main_bottle_btn).setOnClickListener(this);
        rootView.findViewById(R.id.main_drink_ml_edit_btn).setOnClickListener(this);
        rootView.findViewById(R.id.main_jan_edit_btn).setOnClickListener(this);
        rootView.findViewById(R.id.main_drink_btn).setOnClickListener(this);
        rootView.findViewById(R.id.main_jan_min_btn).setOnClickListener(this);
        rootView.findViewById(R.id.main_jan_plus_btn).setOnClickListener(this);

        rootView.findViewById(R.id.realtime_total_amout_more_btn).setOnClickListener(this);
        rootView.findViewById(R.id.realtime_calorie_more_btn).setOnClickListener(this);

        rootView.findViewById(R.id.person_min_btn).setOnClickListener(this);
        rootView.findViewById(R.id.person_plus_btn).setOnClickListener(this);

        // edit text 동작설정
        rootView.findViewById(R.id.main_alcohol_edit).setOnKeyListener(this);
        rootView.findViewById(R.id.main_bottle_ml_edit).setOnKeyListener(this);
        rootView.findViewById(R.id.main_drink_ml_edit).setOnKeyListener(this);

        //시작시간패널, 카운트시간패널
        startTimeText = (TextView)rootView.findViewById(R.id.realtime_starttime_text);
        elapsedTimeText = (TextView)rootView.findViewById(R.id.realtime_elapsed_time_text);
        tickerText = (TextView)rootView.findViewById(R.id.realtime_elapsed_ticker);
        lastDrinkTimeText = (TextView)rootView.findViewById(R.id.subdata_last_drink_time_text);
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

        Log.i("songjo", "onCreate");
        return rootView;
    }
    void getLiquorTypeInDB() {
        liquorTypes.clear();
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
        }
    }
    void selectLiquor(int order) {
        curLiquorTypeId = selectBtnOfIds[order];
        LiquorType lt = getCurLiquorData();

        TextView typeText = (TextView)rootView.findViewById(R.id.main_type_text);
        ImageButton bottleImg = (ImageButton)rootView.findViewById(R.id.main_bottle_btn);
        EditText alcoholEdit = (EditText)rootView.findViewById(R.id.main_alcohol_edit);
        EditText bottlemlEdit = (EditText)rootView.findViewById(R.id.main_bottle_ml_edit);
        EditText drinkmlEdit = (EditText)rootView.findViewById(R.id.main_drink_ml_edit);
        TextView korBottleCountText = (TextView)rootView.findViewById(R.id.main_kor_bottle_count_text);
        ImageButton drinkBtn = (ImageButton)rootView.findViewById(R.id.main_drink_btn);

        TextView janCountText = (TextView)rootView.findViewById(R.id.main_jan_count_text);
        TextView janmlText = (TextView)rootView.findViewById(R.id.main_jan_ml_text);

        typeText.setText(lt.name);
        bottleImg.setImageDrawable(getResources().getDrawable(lt.bottleImg, null));
        alcoholEdit.setText(Double.toString(lt.abv));
        bottlemlEdit.setText(Integer.toString(lt.bottleMl));
        drinkmlEdit.setText(""+lt.drinkMl);
        korBottleCountText.setText(lt.getKorBottelCount());
        drinkBtn.setImageDrawable(getResources().getDrawable(lt.janImg, null));
        lt.setDrinkMl(lt.drinkMl);
        lt.setMulJan(1);
        calculateBlood();
    }
    void calculateBlood(){
        double a = 0.0;
        for(LiquorType lt :liquorTypes){
            a += lt.drinkMl * (lt.abv/100);
        }
        double p = myContext.getSharedPreferences(SettingsFragment.PREFS_NAME, 0).getFloat(SettingsFragment.P_WEIGHT, 70.0f);// 몸무게
        double r = (myContext.getSharedPreferences(SettingsFragment.PREFS_NAME, 0).getBoolean(SettingsFragment.P_ISMAN, true))?0.86:0.64;
        double rst = (a * 0.7894 * 0.7)/(p*r);// 알코올비중(0.7894), 체내흡수율(0.7)

        // 혈중알콜
        TextView bloodText = (TextView)rootView.findViewById(R.id.blood_level);
        bloodText.setText(String.format("%.3f", rst));

        //처벌기준
        String punishmentStr="";
        String drive_punish="";
        if(0.03<=rst){
            if(rst<0.08)
                punishmentStr = "100일간 면허정지,\n1년 이하의 징역이나 500만 원 이하의 벌금.";
            else if(rst<0.2)
                punishmentStr = "면허취소,\n1년 이상 2년 이하의 징역이나 500만 원 이상 1,000만 원 이하의 벌금.";
            else
                punishmentStr = "면허취소,\n2년 이상 5년 이하의 징역이나 1,000만 원 이상 2,000만 원 이하의 벌금.";
        }
        else
            punishmentStr = "없음";

        TextView punishText = rootView.findViewById(R.id.blood_punish_text);
        punishText.setText(punishmentStr);

        // 내주량 몇퍼
        double mySojuCap = myContext.getSharedPreferences(SettingsFragment.PREFS_NAME, 0).getFloat(SettingsFragment.P_SOJU_CAP, 1.5f);
        double percent = a/(mySojuCap*360*(17.5/100));
        TextView gaugeText = rootView.findViewById(R.id.realtime_alcohol_gauge);
        gaugeText.setText((int)(100*percent)+" %");
        //얼굴색
        double ffl = (int)(7*percent);
        if(ffl >= 7)
            ffl = 6.999;
        faceLevel = (int)ffl%7;

        ImageView faceImg = (ImageView)rootView.findViewById(R.id.realtime_alcohol_face);
        faceImg.setImageDrawable(getResources().getDrawable(faceImgIds[faceLevel], null));
        TextView faceLevelText = (TextView) rootView.findViewById(R.id.realtime_alcohol_face_level);
        faceLevelText.setText(String.format("%d/7단계", faceLevel+1));
        //한잔더
        LiquorType lt = getCurLiquorData();
        percent = (a+(lt.janMl*(lt.abv/100)))/(mySojuCap*360*(17.5/100));
        TextView oneJanText = (TextView)rootView.findViewById(R.id.subdata_one_jan_next_percent);
        oneJanText.setText((int)(100*percent)+" %");

        //한병더
        percent = (a+(lt.bottleMl*(lt.abv/100)))/(mySojuCap*360*(17.5/100));
        TextView oneBotText = (TextView)rootView.findViewById(R.id.subdata_one_bottle_next_percent);
        oneBotText.setText((int)(100*percent)+" %");
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
                db = helper.getWritableDatabase();
                getLiquorTypeInDB();
                totalMl = 0;
                calculateBlood();
                counterTask.setEnd();
            }break;
            case R.id.main_bottle_btn:{
                // 한병의 주량 비교 다이얼로그 플레그먼트
                BottleDataDialogFragment bddf = new BottleDataDialogFragment(
                        getCurLiquorData().bottleMl, getCurLiquorData().getOneBottleCapPercent());
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
                LiquorSelectDialogFragment dlsd = new LiquorSelectDialogFragment(liquorTypes);
                dlsd.show(getChildFragmentManager(), LiquorSelectDialogFragment.TAG_LIQUOR_SELECT);
            }break;
            case R.id.main_drink_ml_edit_btn:{
                getCurLiquorData().setDrinkMl(0);
                calculateBlood();
            }
            case R.id.main_jan_min_btn:{
                getCurLiquorData().minMulJan();
            }break;
            case R.id.main_jan_plus_btn:{
                getCurLiquorData().plusMulJan();
            }break;
            case R.id.main_drink_btn:{
                if(counterTask.isStart == false)
                    counterTask.setStartTime(System.currentTimeMillis());
                counterTask.getDrink();
                getCurLiquorData().drinkJan();
                calculateBlood();
            }break;
            default:{
            }break;
        }
    }
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event){
        if(event.getAction() == KeyEvent.ACTION_UP &&
                keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER){
            EditText et = (EditText)v;
            Log.i("songjo", "enter update");
            switch (v.getId()){
                case R.id.main_alcohol_edit:{
                    getCurLiquorData().abv = Double.valueOf(et.getText().toString());
                    helper.updateLiquor(db, getCurLiquorData());
                }break;
                case R.id.main_bottle_ml_edit:{
                    getCurLiquorData().bottleMl = Integer.valueOf(et.getText().toString());
                    helper.updateLiquor(db, getCurLiquorData());
                    et.clearFocus();
                }break;
                case R.id.main_drink_ml_edit:{
                    totalMl = Integer.valueOf(et.getText().toString());
                    et.clearFocus();
                }break;
            }
        }
        return false;
    }
    public String intToKor(int n){
        int ten = n%100;
        int one = ten%10;
        ten = (int)(ten/10);
        return String.format("%s%s", KorTenCount[ten], (one==0&&ten==0)?"빵":KorOneCount[one]);
    }
    public LiquorType getCurLiquorData(){
        return liquorTypes.get(curLiquorTypeId);
    }
    // 술데이터 타입
    public class LiquorType {
        public int id = -1;
        public String name = "진로";
        public double abv = 17.8;
        public int bottleMl = 360;
        public int janMl = 50;
        public int kcal100 = 90;
        public int bottleImg = 0;
        public int janImg = 0;
        int mulJan = 1;

        public int drinkMl = 0;

        public LiquorType(int _id, String _name, double _abv, int _bottleMl, int _janMl, int _kcal) {
            id = _id;
            name = _name;
            abv = _abv;
            bottleMl = _bottleMl;
            janMl = _janMl;
            kcal100 = _kcal;
            setImgById();
        }
        void setImgById(){
            janImg = R.drawable.img_jan_soju_50;
            switch (id){
                case 1:{ bottleImg = R.drawable.img_bottle_soju_360;  }break;
                case 2:{ bottleImg = R.drawable.img_bottle_beer_500; janImg = R.drawable.img_jan_beer_180;}break;
                case 3:{ bottleImg = R.drawable.img_bottle_mak_750; janImg = R.drawable.img_jan_mak_200;}break;
                case 4:{ bottleImg = R.drawable.img_bottle_wine; janImg =R.drawable.img_jan_beer_180;}break;
                case 5:{ bottleImg = R.drawable.img_bottle_vodka; }break;
                case 6:{ bottleImg = R.drawable.img_bottle_wisky; }break;
                case 7:{ bottleImg = R.drawable.img_bottle_high_250;} break;
                default:{ bottleImg = R.drawable.img_bottle_mix; janImg = R.drawable.img_jan_beer_180; }break;
            }
        }
        public String getKorBottelCount(){
            String result = "";
            int bc = (int)(drinkMl/bottleMl);
            int hc = (int)((drinkMl%bottleMl)/(bottleMl/2));
            int jc = (int)Math.ceil((drinkMl%(bottleMl/2))/janMl);
            if(bc>0)
                result = String.format("%s병 %s", intToKor(bc), (hc == 1)?"반":"");
            else if(hc>0)
                result = "반병";
            else if(jc>0)
                result = String.format("%s잔", intToKor(jc));
            if(bc+hc>0 && jc>0)
                result= result + String.format("하고 %s잔", intToKor(jc));
            return result;
        }
        public double getDrinkKcal(){
            double result = drinkMl*kcal100/100;
            return result;
        }
        public int getOneBottleCapPercent(){
            // 한병에 대한 내 주량의 퍼센트
            double mySojuCap = myContext.getSharedPreferences(SettingsFragment.PREFS_NAME, 0).getFloat(SettingsFragment.P_SOJU_CAP, 1.5f);
            double percent = (bottleMl*abv)/(mySojuCap*360*17.5);
            return (int)(100*percent);
        }
        public void plusMulJan(){
            setMulJan(mulJan+1);
        }
        public void minMulJan(){
            setMulJan(mulJan-1);
        }
        public void setMulJan(int n){
            mulJan = n;
            if(mulJan<1)
                mulJan = 1;
            TextView janCountText = (TextView)rootView.findViewById(R.id.main_jan_count_text);
            TextView janmlText = (TextView)rootView.findViewById(R.id.main_jan_ml_text);
            janCountText.setText(intToKor(mulJan)+" 잔");
            janmlText.setText(mulJan*janMl+"ml");
        }
        public void setDrinkMl(int _drinkMl){
            totalKcal -= getDrinkKcal();
            totalMl = totalMl - drinkMl +_drinkMl;
            drinkMl = _drinkMl;
            totalKcal += getDrinkKcal();

            EditText drinkmlEdit = (EditText)rootView.findViewById(R.id.main_drink_ml_edit);
            TextView korBottleCountText = (TextView)rootView.findViewById(R.id.main_kor_bottle_count_text);
            TextView total_amount_text = (TextView)rootView.findViewById(R.id.realtime_total_amout_text);
            TextView realtime_calorie_text = (TextView)rootView.findViewById(R.id.realtime_calorie_text);
            drinkmlEdit.setText(drinkMl+"");
            korBottleCountText.setText(getKorBottelCount());
            total_amount_text.setText(totalMl+" ml");
            realtime_calorie_text.setText(totalKcal+" kcal");
        }
        public void drinkJan(){
            setDrinkMl(drinkMl + mulJan * janMl);
            setMulJan(1);
        }
    }
    // 카운터 어싱크 테스크 쓰래드
    public class CounterTask extends AsyncTask<Integer, Integer, Integer> {
        long startTime;
        long lastDrinkTime = -1;
        boolean termination = false;
        boolean isStart = false;
        long tickerCount = 1;
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
                long mm = (eTime/60000)%60;
                long hh = eTime/(60*60*1000);
                elapsedTimeText.setText((hh != 0 ) ? String.format("%d시간 %d분 째", hh, mm) :String.format("%d분 째", mm));

                eTime = getElapsedDrinkTime();
                long ss = (eTime/1000)%60;
                mm = (eTime/60000)%60;
                hh = eTime/(60*60*1000);
                lastDrinkTimeText.setText(String.format("%d:%d:%d", hh, mm, ss));
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
            lastDrinkTimeText.setText("0:0:0");
            isStart= false;
            startTime = System.currentTimeMillis();
            refreshUI();
        }
        public void setStartTime(long _startTime) {
            long cTime = System.currentTimeMillis();
            startTime = (_startTime > cTime) ? cTime : _startTime;
            lastDrinkTime = -1;
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
        public long getElapsedDrinkTime() {
            if(lastDrinkTime == -1)
                return System.currentTimeMillis() - startTime;
            else
                return System.currentTimeMillis() - lastDrinkTime;
        }
        public boolean getIsStart() {
            return isStart;
        }
        public void exit() {
            termination = true;
        }
        public void getDrink(){
            lastDrinkTime = System.currentTimeMillis();
        }
    }

}