package com.example.mada_2;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.mada_2.Catalog.Meter;
import com.example.mada_2.Catalog.MeterAdapter;
import com.example.mada_2.server_connection.Server;
import com.example.mada_2.server_connection.ServerMock;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class main_camera extends Fragment {

    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;

    RecyclerView recyclerView;

    private Spinner accounts_list;
    // TODO:
    private void initSpinner() {
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getContext());
        Set<String> personal_accounts = getPrefs.getStringSet("Accounts", null);
        if (personal_accounts == null) {
            // ERROR personal accounts not found
            // TODO: remove this
            Set<String> acc = new HashSet<>();
            acc.add("12345678");
            acc.add("32132132");
            SharedPreferences.Editor editor = getPrefs.edit();
            editor.putStringSet("Accounts", acc);
            editor.apply();
        }
        else {
            String[] arr_accounts = new String[personal_accounts.size()];
            int index = 0;
            for (String str : personal_accounts)
                arr_accounts[index++] = str;
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(),
                    android.R.layout.simple_list_item_1, arr_accounts);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            accounts_list.setAdapter(adapter);
        }
    }

    public main_camera() {
        // Required empty public constructor
    }

    public static main_camera newInstance(String param1, String param2) {
        main_camera fragment = new main_camera();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initRecyclerView(List<Meter> meters_list) {
        MeterAdapter groupAdapter = new MeterAdapter(this, meters_list);
        // recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(groupAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_camera, container, false);

        Button camera = view.findViewById(R.id.btn_send);
        camera.setOnClickListener(view1 -> {
            if (hasCameraPermission()){
                enableCamera();
            }else {
                requestPermission();
            }
        });

        accounts_list = view.findViewById(R.id.accounts_list);
        initSpinner();
//        accounts_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            public void onItemSelected(AdapterView<String> parent, View view, int position, long id)
//            {
//                String person_account = parent.getItemAtPosition(position).toString(); //selected item
//
//            }
//            public void onNothingSelected(AdapterView<?> parent)
//            {
//
//            }
//        });

        recyclerView = view.findViewById(R.id.recycler_meters);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Server server = new ServerMock();
        List<Meter> meters_list = server.getMeters("123456", "Выксунский район");
        initRecyclerView(meters_list);
        return view;
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                requireActivity(),
                CAMERA_PERMISSION,
                CAMERA_REQUEST_CODE
        );
    }

    private void enableCamera() {
        Intent intent = new Intent(getActivity(), CameraActivity.class);
        startActivity(intent);
    }

}