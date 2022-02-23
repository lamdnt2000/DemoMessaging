package com.example.demomessaging;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private String SENT = "SMS_SENT";
    private String DELIVERED = "SMS DELIVERED";
    private String FAILURE = "GENERIC_ERROR";
    private String NO_SERVICE = "NO SERVICE";
    private String NULL_PDU = "NULL PDU";
    private String RADIO_OFF = "RADIO_OFF";
    private String NOT_DELIVERED = "SMS_NOT_DELIVERED";
    private PendingIntent sentPI;
    private PendingIntent deliveredPI;
    private BroadcastReceiver smsSentReceiver;
    private BroadcastReceiver smsDeliveredReceiver;
    public static int status;// 0 set default; 1: get feedback; 2: invoke from
    // receiver
    private IntentFilter filter;
    private String SMS_RECEIVED = "SMS_RECEIVED_ACTION";
    private boolean registerIntentReceiver;
    private boolean registerSendReceiver;
    private boolean registerDeliveredReceiver;
    private String phone = "0348641261";


    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TextView txt = (TextView) findViewById(R.id.txtSMSMessage);
            txt.setText(intent.getExtras().getString("sms"));

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        status = 2;
        if (status == 2) {
            sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
            deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(
                    DELIVERED), 0);
            filter = new IntentFilter();
            filter.addAction(SMS_RECEIVED);
            registerReceiver(intentReceiver, filter);
            registerIntentReceiver = true;
        }
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    public void clickToSendSMS(View view) {
        status = 0;
        sendSMS(phone, "Messaging Demo - Mobile Programming");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (status == 2) {
            Log.d("onResume", "onResume");
            smsSentReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            Toast.makeText(getBaseContext(), SENT, Toast.LENGTH_SHORT).show();
                            Log.d("sent", SENT);
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            Toast.makeText(getBaseContext(), FAILURE,
                                    Toast.LENGTH_SHORT).show();
                            Log.d("sent", FAILURE);
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            Toast.makeText(getBaseContext(), FAILURE,
                                    Toast.LENGTH_SHORT).show();
                            Log.d("sent", FAILURE);
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            Toast.makeText(getBaseContext(), NULL_PDU,
                                    Toast.LENGTH_SHORT).show();
                            Log.d("sent", NULL_PDU);
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            Toast.makeText(getBaseContext(), RADIO_OFF,
                                    Toast.LENGTH_SHORT).show();
                            Log.d("sent", RADIO_OFF);
                            break;
                    }
                }
            };
            smsDeliveredReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            Toast.makeText(getBaseContext(), DELIVERED,
                                    Toast.LENGTH_SHORT).show();
                            Log.d("sent", RADIO_OFF);
                            break;
                        case Activity.RESULT_CANCELED:

                            Toast.makeText(getBaseContext(), NOT_DELIVERED,
                                    Toast.LENGTH_SHORT).show();
                            Log.d("sent", NOT_DELIVERED);
                            break;
                    }
                }
            };
            registerReceiver(smsSentReceiver, new IntentFilter(SENT));
            registerSendReceiver = true;
            registerReceiver(smsDeliveredReceiver, new IntentFilter(DELIVERED));
            registerDeliveredReceiver = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (status != 0) {
            Log.d("onPause", "onPause");
            if (intentReceiver != null && registerIntentReceiver) {
                unregisterReceiver(intentReceiver);
                registerIntentReceiver = false;
            }
            if (smsSentReceiver != null && registerSendReceiver) {
                unregisterReceiver(smsSentReceiver);
                registerSendReceiver = false;
                if (smsDeliveredReceiver != null && registerDeliveredReceiver) {
                    unregisterReceiver(smsDeliveredReceiver);
                    registerDeliveredReceiver = false;
                }
                status = 0;
            }
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (intentReceiver != null && registerIntentReceiver) {
            unregisterReceiver(intentReceiver);
            registerIntentReceiver = false;
        }
        if (smsSentReceiver != null && registerSendReceiver) {
            unregisterReceiver(smsSentReceiver);
            registerSendReceiver = false;
        }
        if (smsDeliveredReceiver != null && registerDeliveredReceiver) {
            unregisterReceiver(smsDeliveredReceiver);
            registerDeliveredReceiver = false;
        }
    }


    public void clickToSendSMSIntent(View view) {
        status = 0;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("vnd.android-dir/mms-sms");
        intent.putExtra("address", phone);
        intent.putExtra("sms_body", "Send message using intent - Mobile Programming");


        try {
            startActivity(intent);
            finish();
            Log.i("Finished sending SMS...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this,
                    "SMS faild, please try again later.", Toast.LENGTH_SHORT).show();
        }

    }

    public void clickToSendSMSFB(View view) {

        status = 1;
        SmsManager sms = SmsManager.getDefault();
        sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);
        filter = new IntentFilter();
        filter.addAction(SMS_RECEIVED);
        registerReceiver(intentReceiver, filter);
        registerIntentReceiver = true;
        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), SENT, Toast.LENGTH_SHORT).show();
                        Log.d("sent", SENT);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), FAILURE,
                                Toast.LENGTH_SHORT).show();
                        Log.d("sent", FAILURE);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), FAILURE,
                                Toast.LENGTH_SHORT).show();
                        Log.d("sent", FAILURE);
                        break;

                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), NULL_PDU,
                                Toast.LENGTH_SHORT).show();
                        Log.d("sent", NULL_PDU);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), RADIO_OFF,
                                Toast.LENGTH_SHORT).show();
                        Log.d("sent", RADIO_OFF);
                        break;
                }
            }
        };
        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), DELIVERED,
                                Toast.LENGTH_SHORT).show();
                        Log.d("sent", DELIVERED);
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), NOT_DELIVERED,
                                Toast.LENGTH_SHORT).show();
                        Log.d("sent", NOT_DELIVERED);
                        break;
                }
            }
        };
        registerReceiver(smsSentReceiver, new IntentFilter(SENT));
        registerReceiver(smsDeliveredReceiver, new IntentFilter(DELIVERED));
        sms.sendTextMessage(phone, null,
                "Messaging Demo with Feedback - Mobile Programming", sentPI,
                deliveredPI);
    }


    public void clickToSendEmail(View view) {
        String[] to = {"lamdntse140089@fpt.edu.vn"};
        String[] cc= {"lamdntse140089@fpt.edu.vn"};
        sendEmail(to,cc,"Demo send mess","Hello world");
    }

    public void sendEmail(String[] address, String[] cc, String subject, String msg) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, address);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_CC, cc);
        emailIntent.putExtra(Intent.EXTRA_TEXT,msg);
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent,"Email"));
    }
}