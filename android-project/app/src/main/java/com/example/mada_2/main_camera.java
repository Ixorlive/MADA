package com.example.mada_2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    ActivityResultLauncher<Intent> activityResultLauncher;

    RecyclerView recyclerView;
    Server server;

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
            String[] arr_accounts = new String[personal_accounts.size() + 1];
            int index = 0;
            for (String str : personal_accounts)
                arr_accounts[index++] = str;
            arr_accounts[index] = getResources().getString(R.string.add);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(),
                    android.R.layout.simple_list_item_1, arr_accounts);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            accounts_list.setAdapter(adapter);
        }
    }

    public main_camera() {
        // Required empty public constructor
    }

    public void ShowCameraActivity(int id) {
        Intent intent = new Intent(getActivity(), CameraActivity.class);
        intent.putExtra("id", id);
        activityResultLauncher.launch(intent);
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
        accounts_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String person_account = parent.getItemAtPosition(position).toString(); //selected item
                List<Meter> meters_list_new = server.getMeters(person_account, "123");
                MeterAdapter adapter = (MeterAdapter) recyclerView.getAdapter();
                assert adapter != null;
                adapter.updateAllData(meters_list_new);
                adapter.notifyDataSetChanged();
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        recyclerView = view.findViewById(R.id.recycler_meters);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        server = new ServerMock();
        List<Meter> meters_list = server.getMeters("123456", "Выксунский район");
        initRecyclerView(meters_list);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        assert data != null;
                        int id = data.getIntExtra("id", 0);
                        String decimals = data.getStringExtra("meter");
                        MeterAdapter adapter = (MeterAdapter) recyclerView.getAdapter();
                        adapter.updateData(id, decimals);
                    }
                });

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