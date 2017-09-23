package com.example.siddhantagrawal.check_discuss;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentOne extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("FragmentOne", "frg 1");
        View itemView = inflater.inflate(R.layout.fragment_one, container, false);
        TextView questionText =  (TextView) itemView.findViewById(R.id.frag1);
        questionText.setText(R.string.frag_a);
        return itemView;
    }
}