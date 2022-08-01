package org.ahvroyal.smarthealthprediction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Issue implements Serializable {

    @SerializedName("ID")
    @Expose
    private Integer id;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Accuracy")
    @Expose
    private Double accuracy;
    @SerializedName("Icd")
    @Expose
    private String icd;
    @SerializedName("IcdName")
    @Expose
    private String icdName;
    @SerializedName("ProfName")
    @Expose
    private String profName;
    @SerializedName("Ranking")
    @Expose
    private Integer ranking;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public String getIcd() {
        return icd;
    }

    public void setIcd(String icd) {
        this.icd = icd;
    }

    public String getIcdName() {
        return icdName;
    }

    public void setIcdName(String icdName) {
        this.icdName = icdName;
    }

    public String getProfName() {
        return profName;
    }

    public void setProfName(String profName) {
        this.profName = profName;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", accuracy=" + accuracy +
                ", icd='" + icd + '\'' +
                ", icdName='" + icdName + '\'' +
                ", profName='" + profName + '\'' +
                ", ranking=" + ranking +
                '}';
    }

}
