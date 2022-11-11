package com.example.mada_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.example.mada_2.database.DBWorker;
import com.example.mada_2.ui.login.authorisation;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Создание БД
        DBWorker dataBaseWorker = new DBWorker(getBaseContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        Fragment main = null;
        if(dataBaseWorker.isDbEmpty())
        {
            main = new authorisation();
        }
        else
        {
            main = new MainFragment();
        }
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                main).commit();
    }

}