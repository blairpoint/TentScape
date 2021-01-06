package com.example.tentscape;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    private long lastTouchTime = 0;
    private long currentTouchTime = 0;
    public String username = "user1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayout = findViewById(R.id.linear_layout);
        linearLayout.addView(new CustomView(this));


    }

    class CustomView extends View {
        Bitmap mapBMP = null;
        Bitmap markerBMP = null;
        Bitmap tentBMP = null;
        Bitmap stageBMP = null;
        ArrayList<Rect> rects = new ArrayList<>();
        Rect mapRect;
        Rect myRect;
        Rect myTent;
        boolean isDoubleTap = false;

        public CustomView(Context context) {
            super(context);

            mapBMP = BitmapFactory.decodeResource(context.getResources(), R.drawable.campsite);
            markerBMP = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker);
            tentBMP = BitmapFactory.decodeResource(context.getResources(), R.drawable.tent1);
            stageBMP = BitmapFactory.decodeResource(context.getResources(), R.drawable.stage2);

            ViewTreeObserver viewTreeObserver = linearLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        linearLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int width = linearLayout.getHeight();
                        int height = linearLayout.getWidth();
                        mapRect = new Rect(0, 0, height, width);
                    }
                });
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap(mapBMP, null, mapRect, null);

            if (!rects.isEmpty()) {
                for (Rect rect : rects) {
                    canvas.drawBitmap(markerBMP, null, rect, null);
                    String currentLoc = rects.toString();
                    Log.d("currentloc", "current location is "+ currentLoc);
                    rects.remove(0);

                }
            }

            if(isDoubleTap && myTent != null) {
                canvas.drawBitmap(tentBMP, null, myTent, null);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            super.onTouchEvent(e);
            lastTouchTime = currentTouchTime;
            currentTouchTime = System.currentTimeMillis();
            int x = (int) e.getX();
            int y = (int) e.getY();
            if(e.getAction() == MotionEvent.ACTION_DOWN) {
                if (currentTouchTime - lastTouchTime < 250) {
                    lastTouchTime = 0;
                    currentTouchTime = 0;
                    Log.d("click", "doubleclicked");
                    myTent = new Rect(x, y, x + 60, y + 60);
                    isDoubleTap = true;
                }
                if (currentTouchTime - lastTouchTime > 250) {//Single
                    lastTouchTime = 0;
                    currentTouchTime = 0;
                    Log.d("click", "singleclicked");
                    rects.add(new Rect(x, y, x + 20, y + 20));

                }
                invalidate();//Recall onDraw method
            }
            return true;
        }
    }
}