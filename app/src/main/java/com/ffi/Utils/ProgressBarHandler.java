package com.ffi.Utils;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import com.ffi.R;

/**
 * Created by admin on 8/18/2017.
 */

public class ProgressBarHandler {
    private ProgressBar mProgressBar;
    private Activity mActivity;

    public ProgressBarHandler(Activity activity) {
        mActivity = activity;

        ViewGroup layout = (ViewGroup) activity.findViewById(android.R.id.content).getRootView();

        mProgressBar = new ProgressBar(activity, null, android.R.attr.progressBarStyleLarge);
        mProgressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(mActivity, R.color.colorAccent),
                android.graphics.PorterDuff.Mode.MULTIPLY);

        mProgressBar.setIndeterminate(true);

        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        RelativeLayout rl = new RelativeLayout(activity);

        rl.setGravity(Gravity.CENTER);
        rl.addView(mProgressBar);

        layout.addView(rl, params);

        hideProgressBar();
    }

    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    public void hideProgressBar() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public boolean isShowing() {
       return mProgressBar.isShown();
    }
}