package com.example.mada_2.server_connection;

import com.example.mada_2.Catalog.Meter;

import java.util.List;

// will change in the future, while too lazy to think
public interface Server {
    void Connect(/* ??? */);
    List<Meter> getMeters(String personal_account, String area);
}
