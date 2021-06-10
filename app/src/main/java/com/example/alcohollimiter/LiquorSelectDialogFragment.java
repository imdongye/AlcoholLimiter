package com.example.alcohollimiter;
        import android.app.Activity;
        import android.content.Context;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.ImageView;
        import android.widget.ListView;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.core.widget.NestedScrollView;
        import androidx.fragment.app.DialogFragment;

        import java.util.ArrayList;


public class LiquorSelectDialogFragment extends DialogFragment{

    public static final String TAG_LIQUOR_SELECT = "dialog_liquor_select";

    View rootView;

    ArrayList<RealtimeFragment.LiquorType> liquorTypes;
    LiquorSelectDialogFragment(ArrayList<RealtimeFragment.LiquorType> _liquorTypes) {
        liquorTypes = _liquorTypes;

        Log.i("songjo",liquorTypes.size()+"");
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dialog_liquor_select, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.liquor_list_view);
        LiquorListAdapter adapter = new LiquorListAdapter(getActivity(), R.layout.liquor_list_item, liquorTypes);
        listView.setAdapter(adapter);
        // 할것: Room Live 리스너와 연결
        rootView.findViewById(R.id.select_dialog_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Room Live 리스너와 연결 해제

        Log.i("songjo","slelctdialogfragment destory");
    }

    public class LiquorListAdapter extends ArrayAdapter<RealtimeFragment.LiquorType>{
        private final Activity myContext;
        private ArrayList<RealtimeFragment.LiquorType> liquorTypes;
        public LiquorListAdapter(Activity context, int resource, ArrayList<RealtimeFragment.LiquorType> objects){
            super(context, resource, objects);
            liquorTypes = objects;
            myContext = context;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if(convertView == null){
                Log.i("songjo","getView convert null"+position);

                LayoutInflater inflater = LayoutInflater.from(myContext);
                v = inflater.inflate(R.layout.liquor_list_item, null);
            }
            else
                Log.i("songjo","getView"+position);
            RealtimeFragment.LiquorType liquorType = getItem(position);
            if(liquorType != null){
                TextView tv = (TextView)v.findViewById(R.id.liquor_name);
                ImageView iv = (ImageView)v.findViewById(R.id.liquor_img);
                tv.setText(liquorType.name);
                iv.setImageDrawable(getResources().getDrawable(liquorType.bottleImg,null));
            }
            else
                Log.i("songjo","fuck"+position);

            return v;
        }
    }
}