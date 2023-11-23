package com.mamits.apnaonlines.userv.util;

import android.os.Handler;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mamits.apnaonlines.userv.model.TimerObj;
import com.mamits.apnaonlines.userv.utils.DateUtils;

public class CustomRunnable implements Runnable {

  public long millisUntilFinished = 40000;
  public TextView Hrholder;
  public TextView Minholder;
  public TextView Secholder;
  Handler handler;

  public CustomRunnable(Handler handler,TextView hr,TextView min,TextView sec,long millisUntilFinished) {
    this.handler = handler;
    this.Hrholder = hr;
    this.Minholder = min;
    this.Secholder = sec;
    this.millisUntilFinished = millisUntilFinished;
  }

  @Override
  public void run() {
      /* do what you need to do */
    long seconds = millisUntilFinished / 1000;
    long minutes = seconds / 60;
    long hours = minutes / 60;

    TimerObj remainingTimeObj = DateUtils.Companion.getTimeObjFromMillis(millisUntilFinished);

//    long days = hours / 24;
//    String time = days+" "+"days" +" :" +hours % 24 + ":" + minutes % 60 + ":" + seconds % 60;
    Hrholder.setText(""+remainingTimeObj.getHour());
    Minholder.setText(""+remainingTimeObj.getMin());
    Hrholder.setText(""+remainingTimeObj.getSec());

    millisUntilFinished -= 1000;

    CommonUtils.Companion.printLog("DEV123",""+new Gson().toJson(remainingTimeObj));
      /* and here comes the "trick" */
    handler.postDelayed(this, 1000);
  }

}