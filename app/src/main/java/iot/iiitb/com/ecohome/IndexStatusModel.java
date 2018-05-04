package iot.iiitb.com.ecohome;

/**
 * Created by Er.RajaDS on 18-03-2018.
 */

public class IndexStatusModel {
private String status;
private int rangeL,rangeH;
private int weightage;

    public IndexStatusModel(int rangeL, int rangeH, String status,int weightage) {
        this.rangeH=rangeH;
        this.rangeL=rangeL;
        this.status=status;
        this.weightage=weightage;
    }

    public int getWeightage() {
        return weightage;
    }

    public void setWeightage(int weightage) {
        this.weightage = weightage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRangeL() {
        return rangeL;
    }

    public void setRangeL(int rangeL) {
        this.rangeL = rangeL;
    }

    public int getRangeH() {
        return rangeH;
    }

    public void setRangeH(int rangeH) {
        this.rangeH = rangeH;
    }
}
