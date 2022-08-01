package org.ahvroyal.smarthealthprediction;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Diagnosis implements Serializable {

    @SerializedName("Issue")
    @Expose
    private Issue issue;
    @SerializedName("Specialisation")
    @Expose
    private List<Specialisation> specialisation = null;

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public List<Specialisation> getSpecialisation() {
        return specialisation;
    }

    public void setSpecialisation(List<Specialisation> specialisation) {
        this.specialisation = specialisation;
    }

    @Override
    public String toString() {
        return "Diagnosi{" +
                "issue=" + issue.toString() +
                ", specialisation=" + specialisation.toString() +
                '}';
    }

}
