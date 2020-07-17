package com.example.soilrespiration.common;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

public class CountTimer {

    private long mMillisInterval;
    private long mMillisStart = -1;
    private long mMillisPause;
    private long mMillisLastTickStart;
    private long mTotalPausedFly;

    private volatile int mState = State.TIMER_NOT_START;
    private static final int MSG = 1;

    public CountTimer(long interval){
        mMillisInterval = interval;
        onStart(0);
    }

    protected synchronized void setInterval(long interval){
        mMillisInterval = interval;
    }

    /**
     * Start the timer or resume the timer
     */
    public synchronized void start(){
        if (mState == State.TIMER_RUNNING){
            return;
        }else if (mState == State.TIMER_NOT_START){
            mTotalPausedFly = 0;
            mMillisStart = SystemClock.elapsedRealtime();
            mState = State.TIMER_RUNNING;
            mHandler.sendEmptyMessageDelayed(MSG, mMillisInterval);
        }else{
            mState = State.TIMER_RUNNING;
            long delay = mMillisInterval - (mMillisPause - mMillisLastTickStart);
            mTotalPausedFly += SystemClock.elapsedRealtime() - mMillisPause;
            mHandler.sendEmptyMessageDelayed(MSG, delay);
        }
    }

    /**
     * Pause the timer
     */
    public synchronized void stop(){
        if (mState != State.TIMER_RUNNING){
            return;
        }
        mHandler.removeMessages(MSG);
        mState = State.TIMER_PAUSED;

        mMillisPause = SystemClock.elapsedRealtime();
        onStop(mMillisPause - mMillisStart - mTotalPausedFly);
    }

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            synchronized (CountTimer.this){
                if (mState != State.TIMER_RUNNING){
                    return;
                }

                mMillisLastTickStart = SystemClock.elapsedRealtime();
                onTick(mMillisLastTickStart - mMillisStart - mTotalPausedFly);
                if (mState != State.TIMER_RUNNING){
                    return;
                }
                long delay = mMillisLastTickStart + mMillisInterval - SystemClock.elapsedRealtime();
                while (delay < 0){
                    delay += mMillisInterval;
                }
                sendMessageDelayed(obtainMessage(MSG), delay);
            }
        }
    };

    /**
     * @param millisFly The amount of time fly,not include paused time.
     */
    protected void onStart(long millisFly){
    }

    /**
     * @param millisFly The amount of time fly,not include paused time.
     */
    protected void onStop(long millisFly){
    }

    /**
     * @param millisFly The amount of time fly,not include paused time.
     */
    protected void onTick(long millisFly){
    }
}
