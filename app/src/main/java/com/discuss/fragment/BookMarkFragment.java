package com.discuss.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.siddhantagrawal.check_discuss.R;

public class BookMarkFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_bookmarked_questions, container, false);
        TextView questionText =  (TextView) itemView.findViewById(R.id.fragment_bookmarked_questions);
        questionText.setText("Bookmarked questions");
        return itemView;
    }
}