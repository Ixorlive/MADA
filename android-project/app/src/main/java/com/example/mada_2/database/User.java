package com.example.mada_2.database;

public class User {

    private String password = null;
    private String district = null;

    public User(String password, String district)
    {
        this.password = password;
        this.district = district;
    }

    public String getPassword()
    {
        return password;
    }

    public String getDistrict()
    {
        return district;
    }

}
