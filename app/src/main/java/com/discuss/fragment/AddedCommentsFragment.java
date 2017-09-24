package com.discuss.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.siddhantagrawal.check_discuss.R;


public class AddedCommentsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("BookMarkFragment", "frg 2");
        View itemView = inflater.inflate(R.layout.fragment_added_comments, container, false);
        TextView questionText =  (TextView) itemView.findViewById(R.id.fragment_added_comments);
        questionText.setText("comments added by you");
        return itemView;
    }
}
