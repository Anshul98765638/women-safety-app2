package com.example.safety_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TrigActivity extends AppCompatActivity {

    private int volumeButtonPressCount = 0; // Counter for volume button presses
    private static final int REQUIRED_PRESS_COUNT = 5; // Number of presses needed for SOS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trig);

        // 🔹 Find the trigger button and set up click listener
        Button triggerButton = findViewById(R.id.triggerButton);
        triggerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmergencySMS();
            }
        });
    }

    // 🔹 Function to trigger emergency SMS (calls MainActivity's method)
    private void sendEmergencySMS() {
        Toast.makeText(this, "SOS Triggered! Sending SMS...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("trigger_sos", true);
        startActivity(intent);
    }

    // 🔹 Detect Volume Button Press for SOS
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            volumeButtonPressCount++;
            if (volumeButtonPressCount >= REQUIRED_PRESS_COUNT) {
                sendEmergencySMS();
                volumeButtonPressCount = 0; // Reset counter after triggering SOS
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }
}
