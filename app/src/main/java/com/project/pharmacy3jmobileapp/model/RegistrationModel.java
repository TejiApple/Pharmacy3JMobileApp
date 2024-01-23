package com.project.pharmacy3jmobileapp.model;

public class RegistrationModel {
    public RegistrationModel() {
    }

    public RegistrationModel(String completeName, String mobilePhone, String birthdate, String seniorCitizenId, String cityMunicipality,
                             String barangay, String houseNo, String usernameReg, String passwordReg) {
        this.completeName = completeName;
        this.mobilePhone = mobilePhone;
        this.birthdate = birthdate;
        this.seniorCitizenId = seniorCitizenId;
        this.barangay = barangay;
        this.houseNo = houseNo;
        this.usernameReg = usernameReg;
        this.passwordReg = passwordReg;
    }

    private String completeName;
    private String mobilePhone;
    private String birthdate;
    private String seniorCitizenId;
    private String cityMunicipality;
    private String barangay;
    private String houseNo;
    private String usernameReg;
    private String passwordReg;

    public RegistrationModel(String updatedCompleteName, String updatedMobilePhone, String updatedBirthdate, String updatedSeniorCitizenId, String updatedHouseNo) {
        this.completeName = updatedCompleteName;
        this.mobilePhone = updatedMobilePhone;
        this.birthdate = updatedBirthdate;
        this.seniorCitizenId = updatedSeniorCitizenId;
        this.houseNo = updatedHouseNo;
    }

    public String getCompleteName() {
        return completeName;
    }

    public void setCompleteName(String completeName) {
        this.completeName = completeName;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getSeniorCitizenId() {
        return seniorCitizenId;
    }

    public void setSeniorCitizenId(String seniorCitizenId) {
        this.seniorCitizenId = seniorCitizenId;
    }

    public String getCityMunicipality() {
        return cityMunicipality;
    }

    public void setCityMunicipality(String cityMunicipality) {
        this.cityMunicipality = cityMunicipality;
    }

    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getUsernameReg() {
        return usernameReg;
    }

    public void setUsernameReg(String usernameReg) {
        this.usernameReg = usernameReg;
    }

    public String getPasswordReg() {
        return passwordReg;
    }

    public void setPasswordReg(String passwordReg) {
        this.passwordReg = passwordReg;
    }
}
