package com.discuss.utils;


import android.util.Log;
import android.widget.AbsListView;

/**
 * @author Deepak Thakur
 */
public class EndlessScrollListener implements AbsListView.OnScrollListener {
    private final Command command;
    private volatile int visibleThreshold;
    public EndlessScrollListener(Command command, int visibleThreshold) {
        this.command = command;
        this.visibleThreshold = visibleThreshold;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if ((totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            command.execute();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
}
