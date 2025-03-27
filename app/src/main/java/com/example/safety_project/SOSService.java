package com.example.safety_project;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class SOSService extends Service implements SensorEventListener {

    private static final String CHANNEL_ID = "SOSServiceChannel";
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private static final int SHAKE_TIME_MS = 1000;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastShakeTime = 0;

    private DatabaseHelper myDB;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, getNotification());

        myDB = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Register accelerometer for shake detection
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            } else {
                Toast.makeText(this, "Accelerometer not found!", Toast.LENGTH_LONG).show();
            }
        }

        Toast.makeText(this, "SOS Background Service Started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gForce = (float) Math.sqrt(x * x + y * y + z * z) / SensorManager.GRAVITY_EARTH;
            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                long now = System.currentTimeMillis();
                if (now - lastShakeTime > SHAKE_TIME_MS) {
                    lastShakeTime = now;
                    sendSOSMessage();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "SOS Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification getNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SOS Service Running")
                .setContentText("Listening for emergency triggers...")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.icon)
                .build();
    }

    private void sendSOSMessage() {
        if (!hasPermissions()) {
            requestPermissions();
            return;
        }

        List<String> contacts = getEmergencyContacts();
        if (contacts.isEmpty()) {
            Toast.makeText(this, "No emergency contacts found!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                String message = "Emergency! I need help. My location: ";
                message += (location != null)
                        ? "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude()
                        : "Location unavailable";

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    for (String contact : contacts) {
                        smsManager.sendTextMessage(contact, null, message, null, null);
                    }
                    Toast.makeText(this, "SOS message sent!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "SMS Permission Not Granted!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Log.e("SOSService", "Failed to get location", e));
        } catch (SecurityException e) {
            Log.e("SOSService", "Permission issue while sending SMS", e);
            Toast.makeText(this, "Permission issue: Enable SMS & Location in Settings!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean hasPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Toast.makeText(this, "Grant SMS & Location permissions in App Settings!", Toast.LENGTH_LONG).show();
    }

    private List<String> getEmergencyContacts() {
        List<String> contacts = new ArrayList<>();
        Cursor data = myDB.getListContents();
        if (data != null) {
            while (data.moveToNext()) {
                contacts.add(data.getString(1)); // Assuming column 1 is the phone number
            }
            data.close();
        }
        return contacts;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
