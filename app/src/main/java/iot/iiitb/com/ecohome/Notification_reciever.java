package iot.iiitb.com.ecohome;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Notification_reciever extends BroadcastReceiver {

    PollutantBreakpointData model;
    private JSONObject dataAQI =null;
    private JSONObject dataTCI=null;

    private int IAQI=0,TCI=0,EIAQI=0;

    @Override
    public void onReceive(Context context, Intent intent) {
        model = new PollutantBreakpointData();
        updateDataAQI();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);


        //set color status
        String red="#D50000",yellow="#FF9800",green="#76FF03",blue="#00B0FF";
        int redC= Color.parseColor(red);
        int yellowC=Color.parseColor(yellow);
        int greenC=Color.parseColor(green);
        int blueC=Color.parseColor(blue);

        int colorTCI;
        if(TCI<=25){
            colorTCI=redC;
        }else if(TCI<=50){
            colorTCI=yellowC;
        }else if(TCI<=75){
            colorTCI=greenC;
        }else{
            colorTCI=blueC;
        }

        int colorIAQI,colorEIAQI;
        if(IAQI<=25){
            colorIAQI=redC;
        }else if(IAQI<=50){
            colorIAQI=yellowC;
        }else if(IAQI<=75){
            colorIAQI=greenC;
        }else{
            colorIAQI=blueC;
        }

        if(EIAQI<=1){
            colorEIAQI=redC;
        }else if(EIAQI<=3){
            colorEIAQI=yellowC;
        }else if(EIAQI<=6){
            colorEIAQI=greenC;
        }else{
            colorEIAQI=blueC;
        }

        //

        Intent repeating_intent = new Intent(context,MainActivity.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,100,repeating_intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_menu_enlightenment).setContentTitle("EcoHomesNotification")
                .setContentText("Notification Test").setAutoCancel(true);
        notificationManager.notify(100,builder.build());
    }


    public final void updateDataAQI(){
        EcoHomesRestClient.get_feeds_avg(60,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    System.out.print("AQI RESPONSE"+response.toString());
                    JSONArray array=response.getJSONArray("feeds");
                    dataAQI =array.getJSONObject(array.length()-1);
                    System.out.print("AQI RESPONSE"+dataAQI.toString());
                    updateDataTCI();
                }catch(Exception e){
                    System.err.print(e.toString());
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                System.out.println(errorResponse);
            }
        });
    }

    private void updateDataTCI() {
        //get current temp and humidity data
        EcoHomesRestClient.get_last_feed(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    System.out.print("TCI RESPONSE "+response.toString());
                    dataTCI=response;
                    System.out.print("TCI RESPONSE "+dataTCI.toString());
                    calculate_environment_quality_index();
                }catch(Exception e){
                    System.err.print(e.toString());
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                System.out.println(errorResponse);
            }
        });
    }


    public void calculate_environment_quality_index(){
        try{
            IAQI=model.getIAQI(dataAQI.getDouble("field3"),dataAQI.getDouble("field4"),dataAQI.getDouble("field5"));
            TCI=model.getTCI(Float.valueOf(dataTCI.getString("field2")),Float.valueOf(dataTCI.getString("field1")));
            EIAQI=model.getEIAQI(IAQI,TCI);
        }catch(Exception e){
            System.err.print("ca;aclakc"+e.toString());
        }
    }
}
