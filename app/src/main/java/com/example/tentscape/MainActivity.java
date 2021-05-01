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
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.tabs.TabLayout;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    private long lastTouchTime = 0;
    private long currentTouchTime = 0;
    public String username = "user1";
    TabLayout toolbar;
    ArrayList<Rect> publicdata;
    private int id;
    private String name;
    private String avatar;
    private String curloc;
    private Rect tentloc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = findViewById(R.id.linear_layout);
        linearLayout.addView(new CustomView(this));
        TabLayout tabLayout = findViewById(R.id.tablayout);

        int id = 1;
        String name = "blair";
        String avatar = "marker.jpg";
        String curloc = "559, 482, 639, 562";
        try {
            String databaseUser = "blairuser";
            String databaseUserPass = "Fiddler56!";
            Class.forName("org.postgresql.Driver");
            Connection connection = null;
            String url = "jdbc:postgresql://13.210.214.176/test";
            connection = DriverManager.getConnection(url, databaseUser, databaseUserPass);
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("select * from userdata;");
            while (rs.next()) {

                Log.d("postgres","psql" + rs.getString("id")+" "+rs.getString("name")+" "+rs.getString("avatar")+" "+rs.getString("curloc")+" "+rs.getString("tentloc"));
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }




    }

    class CustomView extends View {
        Bitmap mapBMP = null;
        Bitmap markerBMP = null;
        Bitmap tentBMP = null;
        Bitmap stageBMP = null;
        Bitmap pubBMP = null;
        Bitmap toiletBMP = null;
        Bitmap foodBMP = null;
        ArrayList<Rect> publicdata = new ArrayList<>();
        Rect pubTents;
        ArrayList<Rect> rects = new ArrayList<>();
        Rect mapRect;
        Rect myStage;
        Rect myToilet;
        Rect myTent;
        Rect myFood;
        boolean isDoubleTap = false;

        public CustomView(Context context) {
            super(context);

            mapBMP = BitmapFactory.decodeResource(context.getResources(), R.drawable.campsite);
            markerBMP = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker);
            tentBMP = BitmapFactory.decodeResource(context.getResources(), R.drawable.tent1);
            stageBMP = BitmapFactory.decodeResource(context.getResources(), R.drawable.festivalstage);
            pubBMP = BitmapFactory.decodeResource(context.getResources(), R.drawable.tent2);
            toiletBMP = BitmapFactory.decodeResource(context.getResources(), R.drawable.toilet);
            foodBMP = BitmapFactory.decodeResource(context.getResources(), R.drawable.food);

            myStage = new Rect(278, 159, 358, 239);

            pubTents = new Rect(489, 621, 569, 671);
            myToilet = new Rect(538, 330 , 618, 410);
            myFood = new Rect(434, 925 , 514, 1005);



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
            canvas.drawBitmap(stageBMP, null, myStage, null);
            canvas.drawBitmap(pubBMP, null, pubTents, null);
            canvas.drawBitmap(toiletBMP, null, myToilet, null);
            canvas.drawBitmap(foodBMP,null,myFood,null);



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
                    Toast.makeText(MainActivity.this, "x "+Float.toString(x)+" y "+Float.toString(y),Toast.LENGTH_SHORT).show();

                    myTent = new Rect(x, y, x + 60, y + 60);
                    isDoubleTap = true;
                }
                if (currentTouchTime - lastTouchTime > 250) {//Single
                    lastTouchTime = 0;
                    currentTouchTime = 0;
                    Log.d("click", "singleclicked");
                    rects.add(new Rect(x, y, x + 80, y + 80));

                }

                invalidate();//Recall onDraw method
            }
            return true;
        }
    }



}