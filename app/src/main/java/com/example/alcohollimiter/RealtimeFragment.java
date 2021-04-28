package com.example.alcohollimiter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


public class RealtimeFragment extends Fragment implements View.OnClickListener{
    Context ct = null;
    View rootView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_realtime, container, false);
        ct = container.getContext();
        ImageButton bottleBtn = (ImageButton)rootView.findViewById(R.id.main_bottle_btn);
        bottleBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.main_bottle_btn:{
                //final Calendar c = MyApplication.getRealtimeData().calendar;
                /*AlertDialog.Builder builder = new AlertDialog.Builder(ct);
                String dataText = String.format("%d년 %2d월 %2d일 %2d시 %2d분", c.get(Calendar.YEAR), (c.get(Calendar.MONTH)+1), c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
                builder.setMessage(dataText);
                builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();*/
                Log.i("lim","start");
                Toast.makeText(ct.getApplicationContext(), "asdf", Toast.LENGTH_LONG).show();
                Log.i("lim","end");
            }break;
        }
    }

}