package com.example.alcohollimiter;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;

    RealtimeFragment realtimeFragment = new RealtimeFragment();
    CalendarFragment calendarFragment = new CalendarFragment();
    SettingsFragment settingsFragment = new SettingsFragment();
    AfterFragment afterFragment = new AfterFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 액션바 설정
        ActionBar sab = getSupportActionBar();
        sab.setTitle("혈중 알코올 농도 계산기");

        // 플레그먼트 화면 전환
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, realtimeFragment);
        transaction.commitAllowingStateLoss();

        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottombar);
        bottomNavigationView.setOnNavigationItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch(item.getItemId()) {
                    case R.id.bottom_realtime: {
                        transaction.replace(R.id.frameLayout, realtimeFragment);
                    }break;
                    case R.id.bottom_callendar: {
                        transaction.replace(R.id.frameLayout, calendarFragment);
                    }break;
                    case R.id.bottom_setting: {
                        transaction.replace(R.id.frameLayout, settingsFragment);
                    }break;
                }
                transaction.commitAllowingStateLoss();
                return true;
            }
        });
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
    }
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sos: {

            }break;
            case R.id.action_inquiry: {

            }break;
            case R.id.action_reset_liquors: {
                DBHelper helper = new DBHelper(getApplicationContext());
                SQLiteDatabase db;
                try {
                    db = helper.getWritableDatabase();
                    helper.onUpgrade(db, 0, 0);
                    Toast.makeText(this, "술 종류 리스트가 초기화 됨", Toast.LENGTH_SHORT).show();
                } catch (SQLiteException e){
                }
            }
        }
        return true;
    }
}