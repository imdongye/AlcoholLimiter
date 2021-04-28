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

    public BottleDataDialogFragment() {
    }
    public static BottleDataDialogFragment getInstance() {
        BottleDataDialogFragment bd = new BottleDataDialogFragment();
        return bd;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_bottledata, container);
        // 다이얼로그 코너 투명하게 하기위함
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        // 화면밖 터치시 종료
        setCancelable(true);

        Button mConfirmBtn = (Button) v.findViewById(R.id.bottledata_btn);
        mConfirmBtn.setOnClickListener(this);

        TextView totalml = (TextView) v.findViewById(R.id.bottledata_bottle_ml);
        String totalst = "360ml";
        SpannableString content = new SpannableString(totalst);
        content.setSpan(new UnderlineSpan(), 0, totalml.length(), 0);
        totalml.setText(content);

        TextView capacity = (TextView) v.findViewById(R.id.bottledata_capacity);

        return v;
    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bottledata_btn: {
                dismiss();
            }break;
        }
    }
}