package com.example.adaptertesting;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

public class ResultsActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);
		
		Intent it = getIntent();
		String raceName = (String)it.getExtras().get("raceName");
		
		RaceDBHelper mdbHelper = new RaceDBHelper(this);
		Cursor raceCursor = mdbHelper.getTimes(raceName);
		
		String[] mDataColumns = { RaceDBHelper.PLAYER_NAME, RaceDBHelper.BEST_TIMES };
		int[] mViewIDs = { android.R.id.text1, android.R.id.text2 };
		SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, raceCursor, mDataColumns, mViewIDs, 0);
		setListAdapter(mAdapter);
		Log.i("KevinDebug", "inside ResultsActivity");
	}

}
