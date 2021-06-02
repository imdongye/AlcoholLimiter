package com.example.alcohollimiter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.UnicodeSet;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.alcohollimiter.R;

import org.w3c.dom.Text;

public class BottleDataDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String TAG_BOTTLEDATA_DIALOG = "dialog_bottledata";
    View rootView;
    int bottleml = 390;
    int cap_percent = 50;
    public BottleDataDialogFragment(int _bottleml, int _cap_percent){
        bottleml = _bottleml;
        cap_percent = _cap_percent;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dialog_bottledata, container, false);
        // 다이얼로그 코너 투명하게 하기위함
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        // 화면밖 터치시 종료
        setCancelable(true);

        Button mConfirmBtn = (Button) rootView.findViewById(R.id.bottledata_btn);
        mConfirmBtn.setOnClickListener(this);
        setDate();
        return rootView;
    }/*
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // 전체화면시
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }*/
    private void setDate(){
        TextView bottlemlView = (TextView) rootView.findViewById(R.id.bottledata_bottle_ml);
        TextView capacityView = (TextView) rootView.findViewById(R.id.bottledata_capacity);

        String bottlemlStr = bottleml + "ml";
        SpannableString content = new SpannableString(bottlemlStr);
        content.setSpan(new UnderlineSpan(), 0, bottlemlStr.length(), 0);
        bottlemlView.setText(content);

        capacityView.setText(cap_percent+" %");
    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bottledata_btn: {
                dismiss();
            }break;
        }
    }
}