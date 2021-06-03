package com.example.alcohollimiter;
        import android.app.Activity;
        import android.os.Bundle;
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
        LiquorListAdapter adapter = new LiquorListAdapter(getActivity());
        listView.setAdapter(adapter);
        return rootView;
    }

    public class LiquorListAdapter extends ArrayAdapter<RealtimeFragment.LiquorType>{
        private final Activity context;
        public LiquorListAdapter(Activity context){
            super(context, R.layout.liquor_list_item, liquorTypes);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.liquor_list_item,null, true);
            ImageView imageView = (ImageView)rowView.findViewById(R.id.liquor_img);
            TextView textView = (TextView)rowView.findViewById(R.id.liquor_name);

            imageView.setImageDrawable(getResources().getDrawable(liquorTypes.get(position).bottleImg, null));
            textView.setText(liquorTypes.get(position).name);
            return rowView;
        }
    }
}