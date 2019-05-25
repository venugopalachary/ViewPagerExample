package com.rotomaker.amma;

import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    TextView textView;

    private ArrayList<String> images;
    private ArrayList<String> brandnames;
    private ArrayList<String> brandid;

    public static final String TAG_GRID = "grids";

    //Tag values to read from json
    public static final String TAG_BANNER_URL = "brandposter";
    public static final String TAG_BRANDNAME = "brandname";
    public static final String TAG_BRANDID = "brandid";

    MyCustomPagerAdapter myCustomPagerAdapter;

    RequestQueue posterRequestQueue, bannerRequestQueue;

    private int mCounter;
    private int mMaxRepeat = 0;
    private Handler mHandler;
    private Runnable mRunnable;
    private int mInterval = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        textView = findViewById(R.id.textView);
        images = new ArrayList<>();
        brandnames = new ArrayList<>();
        brandid = new ArrayList<>();


        // Initialize a new instance of Handler
        mHandler = new Handler();

        loadData();




      /*  viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                // do transformation here
            }
        });*/
        viewPager.setPageTransformer(true, new FlipHorizontal());
    }

    private void bannerControl() {
        mCounter = 0;

        mRunnable = new Runnable() {

            @Override
            public void run() {
                // Do some task on delay
                doTask();
            }
        };

        mHandler.postDelayed(mRunnable, (mInterval));
    }

    private void doTask() {
        // Increase the counter by one
        // mCounter ++;


        viewPager.setCurrentItem(mCounter++, true);

        // Display the number of times the task executed
        textView.setText("Task executed : " + mCounter + " times.");

        // Schedule the task to do again after an interval
        mHandler.postDelayed(mRunnable, mInterval);

        // If the task reach the maximum repeat count then stop it here
        if (mCounter == mMaxRepeat) {
            mCounter = 0;
            //   viewPager.setCurrentItem(1, true);
            //   mCounter=0;
            // Remove any pending posts of Runnable r that are in the message queue.
            //  mHandler.removeCallbacks(mRunnable);
        }
    }


    // this method we developed here
    private void loadData() {


        StringRequest stringRequest = new StringRequest(Request.Method.GET, getString(R.string.BANNER_URL1).trim(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if (response.trim().equalsIgnoreCase("no brands found")) {

                    Toast.makeText(getApplicationContext(), "No Brands Found", Toast.LENGTH_LONG).show();
                } else {


                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("fetchingbanners");
                        //Looping through all the elements of json array
                        for (int i = 0; i < jsonArray.length(); i++) {
                            //Creating a json object of the current index
                            JSONObject obj = null;
                            try {
                                //getting json object from current index
                                obj = jsonArray.getJSONObject(i);
                                //getting image url and title from json object
                                images.add(obj.getString(TAG_BANNER_URL));
                                brandnames.add(obj.getString(TAG_BRANDNAME));
                                brandid.add(obj.getString(TAG_BRANDID));


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        bannerControl();
                        mMaxRepeat = brandid.size();

                        myCustomPagerAdapter = new MyCustomPagerAdapter(MainActivity.this, images, brandid);
                        viewPager.setAdapter(myCustomPagerAdapter);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("kioskid", "p4d006");
                return params;
            }
        };
        // Set the tag on the request.
        stringRequest.setTag(TAG_GRID);
        bannerRequestQueue = Volley.newRequestQueue(getApplicationContext());
        bannerRequestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    public class FlipHorizontal implements ViewPager.PageTransformer {

        @Override
        public void transformPage(@NonNull View view, float v) {
            final float rotation = 360f * v;

            view.setAlpha(1);
            // viewPager.setPivotY(view.getHeight()*0.5f);
            //  viewPager.setPivotX(view.getWidth() * 0.5f);
            viewPager.setRotationY(rotation);
        }
    }

}