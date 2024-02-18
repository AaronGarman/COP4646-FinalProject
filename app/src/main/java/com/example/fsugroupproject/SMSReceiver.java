package com.example.fsugroupproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {
    public static final String SMS_RECEIVED_ACTION = "com.example.fsugroupproject.SMS_RECEIVED_ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = "";

        final Bundle bundle = intent.getExtras();

        if(intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)){

            if(bundle != null){
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                String format = bundle.getString("format").toString();

                for(int i = 0 ; i < pdusObj.length ; i++){
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i], format);
                    String sender = currentMessage.getDisplayOriginatingAddress();
                    message = currentMessage.getDisplayMessageBody();
                }
            }
        }

        if (message.matches("sort:[A-Za-z]+")) {
            TransactionsListActivity.format = message.substring(5);

            // creates an intent used to send a broadcast to TransactionListActivity when SMS is received
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(SMS_RECEIVED_ACTION);
            context.sendBroadcast(broadcastIntent);
        }
    }
}