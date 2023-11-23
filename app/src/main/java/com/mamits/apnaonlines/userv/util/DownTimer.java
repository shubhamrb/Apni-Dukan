package com.mamits.apnaonlines.userv.util;

import android.os.Handler;
import android.widget.TextView;

import com.mamits.apnaonlines.userv.model.TimerObj;
import com.mamits.apnaonlines.userv.utils.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class DownTimer implements Runnable {

    private TextView HrTextView;
    private TextView minTextView;
    private TextView secTextView;
    private String endTime;
    private DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private Handler handler = new Handler();

    public DownTimer(String endTime, TextView hrtextView, TextView mintextView, TextView sectextView) {
        this.endTime = endTime;
        this.HrTextView = hrtextView;
        minTextView = mintextView;
        secTextView = sectextView;
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void start() {
        if (handler != null)
            handler.postDelayed(this, 0);
    }

    public void cancel() {
        if (handler != null)
            handler.removeCallbacks(this);
    }

    @Override
    public void run() {
        if (handler == null)
            return;

        if (HrTextView == null)
            return;

//        long timeDiff = endTime - System.currentTimeMillis();
        String currDateTime = DateUtils.Companion.getCurrentDate("yyyy-MM-dd HH:mm:ss");
        TimerObj timerObj = DateUtils.Companion.checkTimeDifference(currDateTime, endTime);

        try {
//            Date date = new Date(timeDiff);
            HrTextView.setText(timerObj.getHour());
            minTextView.setText(timerObj.getMin());
            secTextView.setText(timerObj.getSec());
        }catch (Exception e){e.printStackTrace();}
    }
}
