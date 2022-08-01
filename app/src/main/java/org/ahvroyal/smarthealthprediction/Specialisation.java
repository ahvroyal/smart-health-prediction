package org.ahvroyal.smarthealthprediction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Specialisation implements Serializable {

    @SerializedName("ID")
    @Expose
    private Integer id;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("SpecialistID")
    @Expose
    private Integer specialistID;

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

    public Integer getSpecialistID() {
        return specialistID;
    }

    public void setSpecialistID(Integer specialistID) {
        this.specialistID = specialistID;
    }

    @Override
    public String toString() {
        return "Specialisation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", specialistID=" + specialistID +
                '}';
    }

}
