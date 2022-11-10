package com.example.mada_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.mada_2.server_connection.service.HttpBaseSource;
import com.example.mada_2.ui.login.authorisation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setContentView(R.layout.fragment_authorisation);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        List<String> dist = null;
        try {
            dist = HttpBaseSource.Companion.getClient().getDistrictsAsync().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Fragment auth = new authorisation(dist);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                auth).commit();
//        Fragment main_camera = new authorisation();
//        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
//                main_camera).commit();
    }

}