package com.example.safety_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Bundle extras = getIntent().getExtras();
//        String V1 = extras.getString(Intent.EXTRA_TEXT);
//        Log.d("NumberMainActivity", V1);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String V1 = extras.getString(Intent.EXTRA_TEXT);
            Log.d("NumberMainActivity", V1);
        }
    }

    public void addRelative(View v) {
        Intent i = new Intent(getApplicationContext(), AddRelative.class);
        startActivity(i);
    }

    public void helplineNumbers(View v) {
        Intent i = new Intent(getApplicationContext(), helplineCall.class);
        startActivity(i);
    }

    public void triggers(View v) {
        Intent i = new Intent(getApplicationContext(), TrigActivity.class);
        startActivity(i);
    }

    public void developedBy(View v) {
        Intent i = new Intent(getApplicationContext(), DeveloperByActivity.class);
        startActivity(i);
    }

    public void HowTo(View v){
        Intent i = new Intent(getApplicationContext(), HowToSwipe.class);
        startActivity(i);
    }

    public void LogOut(View v) {
        Intent i = new Intent(getApplicationContext(), Login.class);
        startActivity(i);
    }

    // New method to open webpage when the button is clicked
    public void openWebpage(View v) {
        String url = "https://www.facebook.com/groups/indianwomensgroup/"; // Replace with your actual link
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//
//        int action, keycode;
//
//        action = event.getAction();
//        keycode = event.getKeyCode();
//
//        switch (keycode) {
//            case KeyEvent.KEYCODE_VOLUME_UP: {
//                if (KeyEvent.ACTION_UP == action) {
//                    count++;
//                    String S1 = String.valueOf(count);
//                    Log.d("upButton", S1);
//                }
//            }
//            case KeyEvent.KEYCODE_VOLUME_DOWN:
//                if (KeyEvent.ACTION_DOWN == action) {
//                    count = 0;
//                    String S2 = String.valueOf(count);
//                    Log.d("downButton", S2);
//                }
//        }
//        return super.dispatchKeyEvent(event);
//    }
}
