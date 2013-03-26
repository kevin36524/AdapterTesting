package com.example.adaptertesting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainScreen extends Activity {

	public String raceName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
	}

	public void startRace (View v) {
		EditText et = (EditText)findViewById(R.id.raceName);
		raceName = et.getText().toString();
		
		Intent it = new Intent(this,LogTimeActivity.class);
		it.putExtra("raceName", raceName);
		startActivity(it);
	}

}
