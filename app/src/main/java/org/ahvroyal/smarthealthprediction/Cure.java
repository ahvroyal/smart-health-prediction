package org.ahvroyal.smarthealthprediction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Cure implements Serializable {

    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("DescriptionShort")
    @Expose
    private String descriptionShort;
    @SerializedName("MedicalCondition")
    @Expose
    private String medicalCondition;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("PossibleSymptoms")
    @Expose
    private String possibleSymptoms;
    @SerializedName("ProfName")
    @Expose
    private String profName;
    @SerializedName("Synonyms")
    @Expose
    private String synonyms;
    @SerializedName("TreatmentDescription")
    @Expose
    private String treatmentDescription;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionShort() {
        return descriptionShort;
    }

    public void setDescriptionShort(String descriptionShort) {
        this.descriptionShort = descriptionShort;
    }

    public String getMedicalCondition() {
        return medicalCondition;
    }

    public void setMedicalCondition(String medicalCondition) {
        this.medicalCondition = medicalCondition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPossibleSymptoms() {
        return possibleSymptoms;
    }

    public void setPossibleSymptoms(String possibleSymptoms) {
        this.possibleSymptoms = possibleSymptoms;
    }

    public String getProfName() {
        return profName;
    }

    public void setProfName(String profName) {
        this.profName = profName;
    }

    public String getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(String synonyms) {
        this.synonyms = synonyms;
    }

    public String getTreatmentDescription() {
        return treatmentDescription;
    }

    public void setTreatmentDescription(String treatmentDescription) {
        this.treatmentDescription = treatmentDescription;
    }

}