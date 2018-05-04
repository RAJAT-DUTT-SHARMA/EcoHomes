package iot.iiitb.com.ecohome;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.lang.String;
import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import cz.msebera.android.httpclient.Header;


public class Home extends Fragment {

    TextView tv_aqi,tv_tci,tv_temp,tv_hum,tv_eiaqi, tv_co, tv_voc, tv_pm10;
    TextView tv_eiaqi_status,tv_iaqi_status,tv_tci_status;
    Button btn_getData;

    PollutantBreakpointData model;
    private JSONObject dataAQI =null;
    private JSONObject dataTCI=null;

    private int IAQI=0,TCI=0,EIAQI=0;
    //NotificationCompat.Builder notificationObj;
    public Home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        notificationObj.setAutoCancel(true);

//        notificationObj = new NotificationCompat.Builder(getActivity());
//        notificationObj.setAutoCancel(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);

        tv_aqi=view.findViewById(R.id.txt_aqi);
        tv_eiaqi=view.findViewById(R.id.txt_eiaqi);
        tv_tci=view.findViewById(R.id.txt_tci);
        tv_hum=view.findViewById(R.id.txt_humidity);
        tv_temp=view.findViewById(R.id.txt_temperature);
        tv_co=view.findViewById(R.id.txt_co);
        tv_voc=view.findViewById(R.id.txt_voc);
        tv_pm10=view.findViewById(R.id.txt_pm10);

        tv_eiaqi_status=view.findViewById(R.id.tv_eiaqi_status);
       // tv_iaqi_status=view.findViewById(R.id.tv_iaqi_status);
     //   tv_tci_status=view.findViewById(R.id.tv_tci_status);


        btn_getData=view.findViewById(R.id.btn_getData);
        btn_getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDataAQI();
            }

        });
        model=new PollutantBreakpointData();
        return view;
    }

    public final void updateDataAQI(){
        /*TEST DATA
        JSONObject object=new JSONObject();try {
            object.put("CO", "4.7");
            object.put("PM10", "0.162");
            object.put("VOC","0.289");
            object.put("humidity", "40");
            object.put("temperature", "22");
            calculate_indoor_air_quality_index(object);
        }catch(Exception e){

        }
        */

        //get avg of past data to calculate index
        EcoHomesRestClient.get_feeds_avg(60,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    System.out.print("AQI RESPONSE "+response.toString());
                    JSONArray array=response.getJSONArray("feeds");
                    System.out.println("length aqi"+array);
                    dataAQI =array.getJSONObject(array.length()-1);
                    System.out.print("AQI RESPONSE "+dataAQI.toString());
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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        System.out.print("Inside OnResume");
        updateDataAQI();
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void calculate_environment_quality_index(){

        try{
            double co, pm10, voc;
            co=dataAQI.getDouble("field3");
            pm10=dataAQI.getDouble("field4");
            voc=dataAQI.getDouble("field5");

            if(co==0.0)
                co=dataTCI.getDouble("field3");

            if(pm10==0.0)
                pm10=dataTCI.getDouble("field5");

            if(voc==0.0)
                voc=dataTCI.getDouble("field4");

            IAQI=model.getIAQI(dataAQI.getDouble("field3"),dataAQI.getDouble("field4"),dataAQI.getDouble("field5"));
            TCI=model.getTCI(Float.valueOf(dataTCI.getString("field2")),Float.valueOf(dataTCI.getString("field1")));
            EIAQI=model.getEIAQI(IAQI,TCI);
            updateUI();
        }catch(Exception e){
            System.err.print("ca;aclakc"+e.toString());
        }
    }

    private void updateUI() {
        try{

            String temp=dataTCI.getString("field1");
            String humidity=dataTCI.getString("field2");
            tv_hum.setText( temp.substring(0,temp.indexOf("."))+ " %");
            tv_temp.setText(humidity.substring(0,humidity.indexOf(".")) + " C");

            String co=dataAQI.getString("field3");
            if(co==null)
                co=dataTCI.getString("field3");

            String pm10=dataAQI.getString("field4");
            if(pm10==null)
                pm10=dataTCI.getString("field4");

            String voc=dataAQI.getString("field5");
            if(voc==null)
                voc=dataTCI.getString("field5");

            tv_co.setText(co.substring(0,co.indexOf(".")+2)+ " ppm");
            tv_pm10.setText(pm10.substring(0,pm10.indexOf(".")+2)+ " mg/m3");
            tv_voc.setText(voc.substring(0,voc.indexOf(".")+2)+ " ppm");
        }catch (Exception e){
            e.printStackTrace();
        }

        String red="#D50000",yellow="#FF9800",green="#76FF03",blue="#00B0FF";
        int redC=Color.parseColor(red);
        int yellowC=Color.parseColor(yellow);
        int greenC=Color.parseColor(green);
        int blueC=Color.parseColor(blue);

//        int colorTCI;
//        if(TCI<=25){
//            colorTCI=redC;
//        }else if(TCI<=50){
//            colorTCI=yellowC;
//        }else if(TCI<=75){
//            colorTCI=greenC;
//        }else{
//            colorTCI=blueC;
//        }
//
//        int colorIAQI,colorEIAQI;
//        if(IAQI<=25){
//            colorIAQI=redC;
//        }else if(IAQI<=50){
//            colorIAQI=yellowC;
//        }else if(IAQI<=75){
//            colorIAQI=greenC;
//        }else{
//            colorIAQI=blueC;
//        }
//
        int colorEIAQI;
        if(EIAQI<=1){
            colorEIAQI=redC;
        }else if(EIAQI<=3){
            colorEIAQI=yellowC;
        }else if(EIAQI<=6){
            colorEIAQI=greenC;
        }else{
            colorEIAQI=blueC;
        }

        tv_eiaqi.setText(""+EIAQI+"/7");
        tv_eiaqi_status.setTextColor(colorEIAQI);
        tv_eiaqi_status.setText(model.getEIAQIStatus(EIAQI));
 //       tv_tci.setText(""+TCI+"/100");


        tv_aqi.setText(""+IAQI+"/100");
   //     tv_iaqi_status.setTextColor(colorIAQI);
     //   tv_iaqi_status.setText(model.getIAQIStatus(IAQI));

        tv_tci.setText(""+TCI+"/100");
        System.out.println("tv_tci"+tv_tci);
     //   tv_tci_status.setTextColor(colorTCI);
       // tv_tci_status.setText(model.getTCIStatus(TCI));
    }

//    public void sendNotification(){
//        notificationObj.setSmallIcon(R.drawable.ic_menu_enlightenment);
//        notificationObj.setTicker("This is a Test.");
//        notificationObj.setWhen(System.currentTimeMillis());
//        notificationObj.setContentTitle("Notification Title");
//        notificationObj.setContentText("Notification Test Body");
//
//        Intent intent = new Intent(getActivity(),MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//        notificationObj.setContentIntent(pendingIntent);
//
//
//        //Builds notification and issues it.
//        NotificationManager nm = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//        nm.notify(5127,notificationObj.build());
//
//    }




}
