package com.example.mada_2;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mada_2.server_connection.service.HttpBaseSource;
import com.example.mada_2.ui.login.authorisation;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        List<String> allDistr = null;
        try {
            allDistr = HttpBaseSource.Companion.getClient().getDistrictsAsync().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Fragment auth = new authorisation(allDistr);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                auth).commit();

//        Fragment main_camera = new MainFragment();
//        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
//                main_camera).commit();
    }

}