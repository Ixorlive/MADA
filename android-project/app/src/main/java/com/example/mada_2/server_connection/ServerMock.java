package com.example.mada_2.server_connection;

import com.example.mada_2.Catalog.Meter;

import java.util.ArrayList;
import java.util.List;


public class ServerMock implements Server {

    public ServerMock() {
        // empty constructor
    }

    @Override
    public void Connect() {
        // Connect success
    }

    @Override
    public List<Meter> getMeters(String personal_account, String area) {
        List<Meter> meters = new ArrayList<>();
        if (personal_account.equals("12345678")) {
            meters.add(new Meter(1,"Горячая вода 1", ""));
            meters.add(new Meter(2,"Горячая вода 2", ""));
            meters.add(new Meter(3,"Холодная вода 1", ""));
            meters.add(new Meter(100,"Холодная вода 2", ""));
        } else {
            meters.add(new Meter(1,"Газ 1", ""));
            meters.add(new Meter(2,"Горячая вода 1", ""));
            meters.add(new Meter(100,"Холодная вода 1", ""));
        }

        return meters;
    }
}
