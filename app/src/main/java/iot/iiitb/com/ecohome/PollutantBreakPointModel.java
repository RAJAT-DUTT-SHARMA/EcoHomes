package iot.iiitb.com.ecohome;

/**
 * Created by Er.RajaDS on 18-03-2018.
 */

public class PollutantBreakPointModel{
    float BPhi; //higher breakpoint value of Cp
    float BPlo; //lower breakpoint value of Cp
    int Ihi;  //index breakpoint value of BPhi
    int Ilo;  //index breakpoint value of BPlp

    /**
     float Cp;   //Rounded concentration of pollutant p
     float Ip;   //Index value for pollutant p
     Ip=(Cp-BPlo)*(Ihi-Ilo)/(BPhi-BPlo)+Ilo
     */
    public PollutantBreakPointModel(float BPlo,float BPhi,int Ilo,int Ihi ){
        this.BPhi=BPhi;
        this.BPlo=BPlo;
        this.Ihi=Ihi;
        this.Ilo=Ilo;
    }

}
