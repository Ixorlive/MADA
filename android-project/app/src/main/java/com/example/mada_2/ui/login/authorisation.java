package com.example.mada_2.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.fragment.app.*;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.mada_2.R;
import com.example.mada_2.server_connection.service.BaseSource;
import com.example.mada_2.server_connection.service.HttpBaseSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import kotlin.jvm.JvmOverloads;

public class authorisation extends Fragment {


    List<String> dist = null;

    public authorisation(List<String> dist)
    {
        this.dist = dist;
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authorisation, container, false);
        Spinner spinner = view.findViewById(R.id.area);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, dist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        Button button = view.findViewById(R.id.login);
        button.setOnClickListener(v -> {
//            HttpBaseSource.Companion.getClient().authorizeAsync(view.findViewById(R.id.area).getText(), view.findViewById(R.id.password).getText());
        });
        return view;
    }

}