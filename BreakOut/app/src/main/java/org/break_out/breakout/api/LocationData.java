package com.example;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationData {

    @SerializedName("ADMINISTRATIVE_AREA_LEVEL_1")
    @Expose
    private String aDMINISTRATIVEAREALEVEL1;
    @SerializedName("POLITICAL")
    @Expose
    private String pOLITICAL;
    @SerializedName("ROUTE")
    @Expose
    private String rOUTE;
    @SerializedName("ADMINISTRATIVE_AREA_LEVEL_2")
    @Expose
    private String aDMINISTRATIVEAREALEVEL2;
    @SerializedName("LOCALITY")
    @Expose
    private String lOCALITY;
    @SerializedName("SUBLOCALITY_LEVEL_1")
    @Expose
    private String sUBLOCALITYLEVEL1;
    @SerializedName("COUNTRY")
    @Expose
    private String cOUNTRY;
    @SerializedName("POSTAL_CODE")
    @Expose
    private String pOSTALCODE;
    @SerializedName("STREET_NUMBER")
    @Expose
    private String sTREETNUMBER;
    @SerializedName("SUBLOCALITY")
    @Expose
    private String sUBLOCALITY;

    /**
     * @return The aDMINISTRATIVEAREALEVEL1
     */
    public String getADMINISTRATIVEAREALEVEL1() {
        return aDMINISTRATIVEAREALEVEL1;
    }

    /**
     * @param aDMINISTRATIVEAREALEVEL1 The ADMINISTRATIVE_AREA_LEVEL_1
     */
    public void setADMINISTRATIVEAREALEVEL1(String aDMINISTRATIVEAREALEVEL1) {
        this.aDMINISTRATIVEAREALEVEL1 = aDMINISTRATIVEAREALEVEL1;
    }

    /**
     * @return The pOLITICAL
     */
    public String getPOLITICAL() {
        return pOLITICAL;
    }

    /**
     * @param pOLITICAL The POLITICAL
     */
    public void setPOLITICAL(String pOLITICAL) {
        this.pOLITICAL = pOLITICAL;
    }

    /**
     * @return The rOUTE
     */
    public String getROUTE() {
        return rOUTE;
    }

    /**
     * @param rOUTE The ROUTE
     */
    public void setROUTE(String rOUTE) {
        this.rOUTE = rOUTE;
    }

    /**
     * @return The aDMINISTRATIVEAREALEVEL2
     */
    public String getADMINISTRATIVEAREALEVEL2() {
        return aDMINISTRATIVEAREALEVEL2;
    }

    /**
     * @param aDMINISTRATIVEAREALEVEL2 The ADMINISTRATIVE_AREA_LEVEL_2
     */
    public void setADMINISTRATIVEAREALEVEL2(String aDMINISTRATIVEAREALEVEL2) {
        this.aDMINISTRATIVEAREALEVEL2 = aDMINISTRATIVEAREALEVEL2;
    }

    /**
     * @return The lOCALITY
     */
    public String getLOCALITY() {
        return lOCALITY;
    }

    /**
     * @param lOCALITY The LOCALITY
     */
    public void setLOCALITY(String lOCALITY) {
        this.lOCALITY = lOCALITY;
    }

    /**
     * @return The sUBLOCALITYLEVEL1
     */
    public String getSUBLOCALITYLEVEL1() {
        return sUBLOCALITYLEVEL1;
    }

    /**
     * @param sUBLOCALITYLEVEL1 The SUBLOCALITY_LEVEL_1
     */
    public void setSUBLOCALITYLEVEL1(String sUBLOCALITYLEVEL1) {
        this.sUBLOCALITYLEVEL1 = sUBLOCALITYLEVEL1;
    }

    /**
     * @return The cOUNTRY
     */
    public String getCOUNTRY() {
        return cOUNTRY;
    }

    /**
     * @param cOUNTRY The COUNTRY
     */
    public void setCOUNTRY(String cOUNTRY) {
        this.cOUNTRY = cOUNTRY;
    }

    /**
     * @return The pOSTALCODE
     */
    public String getPOSTALCODE() {
        return pOSTALCODE;
    }

    /**
     * @param pOSTALCODE The POSTAL_CODE
     */
    public void setPOSTALCODE(String pOSTALCODE) {
        this.pOSTALCODE = pOSTALCODE;
    }

    /**
     * @return The sTREETNUMBER
     */
    public String getSTREETNUMBER() {
        return sTREETNUMBER;
    }

    /**
     * @param sTREETNUMBER The STREET_NUMBER
     */
    public void setSTREETNUMBER(String sTREETNUMBER) {
        this.sTREETNUMBER = sTREETNUMBER;
    }

    /**
     * @return The sUBLOCALITY
     */
    public String getSUBLOCALITY() {
        return sUBLOCALITY;
    }

    /**
     * @param sUBLOCALITY The SUBLOCALITY
     */
    public void setSUBLOCALITY(String sUBLOCALITY) {
        this.sUBLOCALITY = sUBLOCALITY;
    }

}