package com.mamits.apnaonlines.user.ui.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;

public class AutoScrollRecyclerView extends RecyclerView {
    private static long delayTime = 3000;// How long after the interval to perform scrolling
    AutoPollTask autoPollTask;// Scroll thread
    private boolean running; // Is it rolling?
    private boolean canRun;// Can it be automatically scrolled, depending on whether the data exceeds the screen

    public AutoScrollRecyclerView(Context context) {
        super(context);
    }

    public AutoScrollRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        autoPollTask = new AutoPollTask(this);// Instantiate the scroll refresh thread
    }

    static class AutoPollTask implements Runnable {
        private final WeakReference<AutoScrollRecyclerView> mReference;

        // Use weak references to hold external class references to prevent memory leaks
        public AutoPollTask(AutoScrollRecyclerView reference) {
            this.mReference = new WeakReference<>(reference);
        }

        @Override
        public void run() {
            AutoScrollRecyclerView recyclerView = mReference.get();// Get the recyclerview object
            if (recyclerView != null && recyclerView.running && recyclerView.canRun) {
                recyclerView.scrollBy(2, 2);// Note the difference between scrollBy and scrollTo
                //delayed to send
                recyclerView.postDelayed(recyclerView.autoPollTask, delayTime);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                /*if (running)
                    stop();*/
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                /*if (canRun)
                    start();*/
                break;
        }
        return super.onTouchEvent(e);
    }

    //Open: If it is running, stop first -> then open
    public void start() {
        if (running)
            stop();
        canRun = true;
        running = true;
        postDelayed(autoPollTask, delayTime);
    }

    public void stop() {
        running = false;
        removeCallbacks(autoPollTask);
    }
}