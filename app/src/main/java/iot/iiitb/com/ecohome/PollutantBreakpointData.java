package iot.iiitb.com.ecohome;

import java.util.ArrayList;

/**
 * Created by Er.RajaDS on 18-03-2018.
 */

public class PollutantBreakpointData {

    private IndexStatusModel[] IAQI_DATA=new IndexStatusModel[4];
    private IndexStatusModel[] EIAQI_DATA=new IndexStatusModel[4];
    private IndexStatusModel[] TCI_DATA=new IndexStatusModel[4];

    private ArrayList<PollutantBreakPointModel> dataCo=new ArrayList<>();
    private ArrayList<PollutantBreakPointModel> dataVOC=new ArrayList<>();
    private ArrayList<PollutantBreakPointModel> dataPM10=new ArrayList<>();
    private ArrayList<PollutantBreakPointModel> dataTemperature=new ArrayList<>();
    private ArrayList<PollutantBreakPointModel> dataHumidity=new ArrayList<>();


    public PollutantBreakpointData(){

        //concentration in ppm
        dataCo.add(new PollutantBreakPointModel(0.0f,1.7f,76,100));
        dataCo.add(new PollutantBreakPointModel(1.8f,8.7f,51,75));
        dataCo.add(new PollutantBreakPointModel(8.8f,10.0f,26,50));
        dataCo.add(new PollutantBreakPointModel(10.1f,50.0f,0,25));

        //concentration in mg/m3
        dataPM10.add(new PollutantBreakPointModel(0.0f,0.020f,76,100));
        dataPM10.add(new PollutantBreakPointModel(0.021f,0.150f,51,75));
        dataPM10.add(new PollutantBreakPointModel(0.151f,0.180f,26,50));
        dataPM10.add(new PollutantBreakPointModel(0.181f,0.600f,0,25));

        //concentration in ppm
        dataVOC.add(new PollutantBreakPointModel(0.0f,0.087f,76,100));
        dataVOC.add(new PollutantBreakPointModel(0.088f,0.261f,51,75));
        dataVOC.add(new PollutantBreakPointModel(0.262f,0.43f,26,50));
        dataVOC.add(new PollutantBreakPointModel(0.44f,3.00f,0,25));

        //values in degree celcius
        dataTemperature.add(new PollutantBreakPointModel(20.0f,26.0f,76,100));
        dataTemperature.add(new PollutantBreakPointModel(26.1f,29.0f,51,75));
        dataTemperature.add(new PollutantBreakPointModel(29.1f,39.0f,26,50));
        dataTemperature.add(new PollutantBreakPointModel(39.1f,45.0f,0,25));

        //values in % (relative humidity)
        dataHumidity.add(new PollutantBreakPointModel(40.0f,70.0f,76,100));
        dataHumidity.add(new PollutantBreakPointModel(70.1f,80.0f,51,75));
        dataHumidity.add(new PollutantBreakPointModel(80.1f,90.0f,26,50));
        dataHumidity.add(new PollutantBreakPointModel(90.1f,100.0f,0,25));

        IAQI_DATA[0]=new IndexStatusModel(76,100,"Good",3);
        IAQI_DATA[1]=new IndexStatusModel(51,75,"Moderate",2);
        IAQI_DATA[2]=new IndexStatusModel(26,50,"Unhealthy",1);
        IAQI_DATA[3]=new IndexStatusModel(0,25,"Hazardous",-3);

        TCI_DATA[0]=new IndexStatusModel(76,100,"Most Comfort",3);
        TCI_DATA[1]=new IndexStatusModel(51,75,"Comfort",2);
        TCI_DATA[2]=new IndexStatusModel(26,50,"Not Comfort",1);
        TCI_DATA[3]=new IndexStatusModel(0,25,"Least Comfort",0);

        EIAQI_DATA[0]=new IndexStatusModel(7,7,"Excellent",0);
        EIAQI_DATA[1]=new IndexStatusModel(4,6,"Good",0);
        EIAQI_DATA[2]=new IndexStatusModel(2,3,"Bad",0);
        EIAQI_DATA[3]=new IndexStatusModel(-4,1,"Worst",0);

    }

    /**
     float Cp;   //Rounded concentration of pollutant p
     float Ip;   //Index value for pollutant p
     Ip=(Cp-BPlo)*(Ihi-Ilo)/(BPhi-BPlo)+Ilo
     * @param CO
     * @param PM10
     * @param VOC
     */

    public int getIAQI(double CO, double PM10, double VOC ){
        int IAQI=0;

        int AQI_CO=0,AQI_PM10=0,AQI_VOC=0;
        //initialize variables depending on extreme cases
        if(CO<0.0d){
            AQI_CO=100;
        }else if (CO>50.0d){
            AQI_CO=0;
        }

        if(VOC<0.0d){
            AQI_VOC=100;
        }else if (VOC>3.0d){
            AQI_VOC=0;
        }
        if(PM10<0.0d){
            AQI_PM10=100;
        }else if (PM10>0.6d){
            AQI_PM10=0;
        }

        for (PollutantBreakPointModel datum:dataCo
             ) {
            if(datum.BPhi>=CO && datum.BPlo<=CO){
                AQI_CO= (int) ((CO-datum.BPlo)*((datum.Ihi-datum.Ilo)/(datum.BPhi-datum.BPlo))+datum.Ilo);
                break;
            }
        }
        for (PollutantBreakPointModel datum:dataVOC
                ) {
            if(datum.BPhi>=VOC && datum.BPlo<=VOC){
                AQI_VOC= (int) ((VOC-datum.BPlo)*((datum.Ihi-datum.Ilo)/(datum.BPhi-datum.BPlo))+datum.Ilo);
                break;
            }
        }
        for (PollutantBreakPointModel datum:dataPM10
                ) {
            if(datum.BPhi>=PM10 && datum.BPlo<=PM10){
                AQI_PM10= (int) ((PM10-datum.BPlo)*((datum.Ihi-datum.Ilo)/(datum.BPhi-datum.BPlo))+datum.Ilo);
                break;
            }
        }

        if(AQI_CO<AQI_PM10){
            IAQI=AQI_CO;
        }else{
            IAQI=AQI_PM10;
        }
        if(AQI_VOC<IAQI){
            IAQI=AQI_VOC;
        }



        return IAQI;
    }

    public int getTCI(float temperature,float humidity ){
        int TCI=0;
        int CI_HUM=0,CI_TEMP=0;

//        if(temperature<20f && temperature>45f){
//            CI_TEMP=0;
//        }
//        if(humidity<40f && humidity>100f){
//            CI_HUM=0;
//        }

        if(temperature<20f){
            CI_TEMP=(int)(temperature-0)*((50-0)/(20-0))+0;
        }else if(temperature>45f || temperature<0f){
            CI_TEMP=0;
        }else{
            for (PollutantBreakPointModel datum:dataTemperature
                    ) {
                if(datum.BPhi>=temperature && datum.BPlo<=temperature){
                    CI_TEMP= (int) ((temperature-datum.BPlo)*((datum.Ihi-datum.Ilo)/(datum.BPhi-datum.BPlo))+datum.Ilo);
                    break;
                }
            }
        }
        if(humidity<40f){
            CI_HUM=(int)(humidity-0)*((75-0)/(40-0))+0;
        }else if(humidity>100f || humidity<0f)
            CI_HUM=0;
        else{
            for (PollutantBreakPointModel datum:dataHumidity
                    ) {
                if(datum.BPhi>=humidity && datum.BPlo<=humidity){
                    CI_HUM= (int) ((humidity-datum.BPlo)*((datum.Ihi-datum.Ilo)/(datum.BPhi-datum.BPlo))+datum.Ilo);
                    break;
                }
            }
        }



        if(CI_HUM<CI_TEMP){
            TCI=CI_HUM;
        }else{
            TCI=CI_TEMP;
        }
        System.out.println("TCI "+TCI+" Temp "+temperature+" humid "+humidity);
        return TCI;
    }

    public int getEIAQI(int IAQI,int TCI ){
        int EIAQI=0;
        for (IndexStatusModel datum:IAQI_DATA
             ) {
            if(datum.getRangeH()>=IAQI && datum.getRangeL()<=IAQI){
                EIAQI+=datum.getWeightage();
            }
        }

        for (IndexStatusModel datum:TCI_DATA
                ) {
            if(datum.getRangeH()>=TCI && datum.getRangeL()<=TCI){
                EIAQI+=datum.getWeightage();
            }
        }

        return EIAQI;
    }

    public String getEIAQIStatus(int EIAQI){
        String result=null;
        for (IndexStatusModel datum:EIAQI_DATA
             ) {
            if(datum.getRangeL()<=EIAQI && datum.getRangeH()>=EIAQI){
                result=datum.getStatus();
            }
        }
        return result;
    }
    public String getIAQIStatus(int IAQI){
        String result=null;
        for (IndexStatusModel datum:IAQI_DATA
                ) {
            if(datum.getRangeL()<=IAQI && datum.getRangeH()>=IAQI){
                result=datum.getStatus();
            }
        }
        return result;
    }
    public String getTCIStatus(int TCI){
        String result=null;
        for (IndexStatusModel datum:TCI_DATA
                ) {
            if(datum.getRangeL()<=TCI && datum.getRangeH()>=TCI){
                result=datum.getStatus();
            }
        }
        return result;
    }
}
