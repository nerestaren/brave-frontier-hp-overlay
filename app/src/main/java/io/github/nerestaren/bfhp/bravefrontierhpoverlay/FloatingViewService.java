package io.github.nerestaren.bfhp.bravefrontierhpoverlay;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import io.github.nerestaren.bfhp.bravefrontierhpoverlay.R;

/**
 * Created by Antoni on 23/10/2015.
 */

//http://icetea09.com/blog/2015/03/16/android-floating-view-like-facebook-chatheads/

public class FloatingViewService extends Service {

    private WindowManager mWindowManager;
    private ImageView mImgFloatingView;
    private boolean mIsFloatingViewAttached = false;

    @Override
    public IBinder onBind(Intent intent) {
        //Not use this method
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!mIsFloatingViewAttached){
            mWindowManager.addView(mImgFloatingView, mImgFloatingView.getLayoutParams());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        mImgFloatingView = new ImageView(this);
        mImgFloatingView.setImageResource(R.drawable.hp);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;

        int sWidth = mWindowManager.getDefaultDisplay().getWidth();
        int sHeight = mWindowManager.getDefaultDisplay().getHeight();
        float sAr = sWidth / sHeight;
        int gameHeight, gameWidth, marginLeft, marginTop;
        float gameAr = 640/960;

        if (sAr > gameAr) {
            gameWidth = (int) (sHeight * gameAr);
            gameHeight = sHeight;
            marginLeft = (sWidth - gameWidth) / 2;
            marginTop = 434 * gameHeight / 960;
        } else if (sAr < gameAr) {
            gameWidth = sWidth;
            gameHeight = (int) (sWidth / gameAr);
            marginLeft = 0;
            marginTop = 434 * gameHeight / 960 + (sHeight - gameHeight) / 2;
        } else {
            gameWidth = sWidth;
            gameHeight = sHeight;
            marginLeft = 0;
            marginTop = 434 * gameHeight / 960;
        }

        params.width = gameWidth;
        params.x = marginLeft;
        params.y = marginTop;

        mWindowManager.addView(mImgFloatingView, params);

        mImgFloatingView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mImgFloatingView, params);
                        return true;
                }
                return false;
            }
        });

        mIsFloatingViewAttached = true;
    }

    public void removeView() {
        if (mImgFloatingView != null){
            mWindowManager.removeView(mImgFloatingView);
            mIsFloatingViewAttached = false;
        }
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "onDestroy", Toast.LENGTH_SHORT);
        super.onDestroy();
        removeView();
    }
}