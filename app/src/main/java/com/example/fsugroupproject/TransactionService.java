package com.example.fsugroupproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

public class TransactionService extends Service {

    private int notificationID;

    @Override
    public void onCreate() {
        super.onCreate();

        // sets up the notification channel used to display transaction notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("transactionNotificationChannel", "Transaction Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            // gets the information about the transaction from the TransactionActivity class using the shared key
            String newTransaction = intent.getStringExtra("transactionOccurred");

            // builds the notification using the information passed into the newTransaction string
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "transactionNotificationChannel")
                    .setSmallIcon(android.R.drawable.stat_notify_chat)
                    .setContentTitle("New Transaction")
                    .setContentText(newTransaction)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            // initializes the notification manager used to push the notification
            NotificationManagerCompat transactionNotificationManager = NotificationManagerCompat.from(this);

            // checks that the permission is given to receive post notifications
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // generates a random notificationID so that notifications do not overlap each other
                notificationID = new Random().nextInt();

                // tells the notification manager to push the notification to the user
                transactionNotificationManager.notify(notificationID, builder.build());
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
