package com.mobiapp4u.pc.routinebasket.Model;

public class User {
    private String name;
    private String password;
    private String phone;
    private String IsStaff;
    private String securecode;
    private String homeAdress;
    private String latlng;
    private String email;

    public User() {
    }

    public User(String name, String password, String securecode) {
        this.name = name;
        this.password = password;
        this.securecode = securecode;
        IsStaff = "false";


    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public String getHomeAdress() {
        return homeAdress;
    }

    public void setHomeAdress(String homeAdress) {
        this.homeAdress = homeAdress;
    }

    public String getSecurecode() {
        return securecode;
    }

    public void setSecurecode(String securecode) {
        this.securecode = securecode;
    }

    public String getName() {
        return name;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
