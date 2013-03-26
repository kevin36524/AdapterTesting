package com.example.adaptertesting;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class LogTimeActivity extends Activity {

	final int MSG_START_TIMER = 0;
    final int MSG_STOP_TIMER = 1;
    final int MSG_UPDATE_TIMER = 2;
    final int REFRESH_RATE = 100;
    
    boolean mSetupDone = false;
    JSONObject mResObj = null;
    RaceDBHelper mDBHelper = new RaceDBHelper(this);
    
    Stopwatch mStopwatch = new Stopwatch();
    TextView clk_tv;
    String raceName;

    Handler mHandler = new Handler(){
    	@Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case MSG_START_TIMER:
                mStopwatch.start(); //start timer
                mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);
                break;

            case MSG_UPDATE_TIMER:
                clk_tv.setText(""+ mStopwatch.getElapsedTimeSecs());
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER,REFRESH_RATE); //text view is updated every second, 
                break;                                  //though the timer is still running
            case MSG_STOP_TIMER:
                mHandler.removeMessages(MSG_UPDATE_TIMER); // no more updates.
                mStopwatch.stop();//stop timer
                mStopwatch.clear();
                clk_tv.setText(""+ mStopwatch.getElapsedTime());
                break;

            default:
                break;
            }
        }
    };
    
	private ArrayAdapter<String> mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		renderGrid();
		Intent it = this.getIntent();
		raceName = (String) it.getExtras().get("raceName");
		Window w = getWindow();
		w.setTitle(raceName);
		clk_tv= (TextView)findViewById(R.id.clkTextView);
	}
	
	private void renderGrid() {
		GridView gridview = (GridView) findViewById(R.id.gridview);
		ArrayList<String> myStringArray = new ArrayList<String>();
		
		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myStringArray);
		gridview.setAdapter(mAdapter);

		gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				TextView tv = (TextView) arg1;
				if (!mSetupDone) setUpPlayers(false);
				logTime(tv.getText().toString());
			}
		});
		
		gridview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				TextView tv = (TextView) arg1;
				mAdapter.remove(tv.getText().toString());
				return false;
			}
		});
		
	}
	
	private void logTime (String pName) {
		try {
			JSONObject tempObj = mResObj.getJSONObject(pName);
			String lapTimes = (String) tempObj.get("lapTimes");
			Double lapStart = tempObj.getDouble("lapStart");
			Double bestTime = tempObj.getDouble("bestTime");
			Double lapTime;
			Double currentTime = mStopwatch.getElapsedTimeSecs();
			DecimalFormat f = new DecimalFormat("##.00");

			tempObj.put("lapStart", currentTime);
			if (lapStart != 0.0) {
				lapTime = currentTime - lapStart;
				if (lapTime < bestTime) {
					tempObj.put("bestTime",f.format(lapTime));
				}
				lapTimes += ", " + f.format(lapTime);
				tempObj.put("lapTimes", lapTimes);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void setUpPlayers (boolean isAll) {
		int nPlayers = mAdapter.getCount();
		String pName;
		JSONObject tempObj;
		mResObj = new JSONObject();
		
		for (int i=0; i< nPlayers; i++) {
			pName = mAdapter.getItem(i);
			double startTime = 0.0;
			if (isAll) startTime = 0.1;
			
			tempObj = new JSONObject();
			try {
				tempObj.put("lapStart", startTime);
				tempObj.put("lapTimes", "");
				tempObj.put("bestTime", 999.0);
				mResObj.put(pName,tempObj);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		mSetupDone = true;
	}
	
	public void addPlayer (View v) {
		TextView tv = (TextView)findViewById(R.id.player_name);
		String newName = tv.getText().toString();
		tv.setText("");
		
		mAdapter.add(newName);
	}
	
	public void startTimer (View v) {
		mHandler.sendEmptyMessage(MSG_START_TIMER);
	}
	
	public void startAllTimer (View v) {
		mHandler.sendEmptyMessage(MSG_START_TIMER);
		setUpPlayers(true);
	}
	
	public void clearTimer (View v) {
		mHandler.sendEmptyMessage(MSG_STOP_TIMER);
		mResObj = null;
	}
	
	public void showResults (View v) {
		if (mResObj != null) {
		    mDBHelper.addTimes(raceName, mResObj);
		}
		Intent it = new Intent(this,ResultsActivity.class);
		it.putExtra("raceName", raceName);
		startActivity(it);
		mResObj = null;
	}

}
