package com.example.mada_2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.mada_2.Catalog.Meter;
import com.example.mada_2.Catalog.MeterAdapter;
import com.example.mada_2.server_connection.Server;
import com.example.mada_2.server_connection.ServerMock;

import java.util.List;

public class MainFragment extends Fragment {

    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;

    ActivityResultLauncher<Intent> activityResultLauncher;
    private Spinner accounts_list;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    Button sendButton;
    Server server;

    public MainFragment() {
        // Required empty public constructor
    }

    public void ShowCameraActivity(int id) {
        if (hasCameraPermission()){
            Intent intent = new Intent(getActivity(), CameraActivity.class);
            intent.putExtra("id", id);
            activityResultLauncher.launch(intent);
        } else {
            requestPermission();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        accounts_list = view.findViewById(R.id.accounts_list);
        recyclerView = view.findViewById(R.id.recycler_meters);
        sendButton = view.findViewById(R.id.btn_send);
        progressBar = view.findViewById(R.id.progressBar);
        sendButton.setOnClickListener((View v) -> {
            startProgressBar();
            (new Handler()).postDelayed(this::dataSent, 1500);
            // TODO: send data;
        });
        initSpinner();
        initRecyclerView();
        initActivityResultLauncher();

        return view;
    }

    private void initSpinner() {
//        SharedPreferences getPrefs = PreferenceManager
//                .getDefaultSharedPreferences(this.getContext());
//        Set<String> personal_accounts = getPrefs.getStringSet("Accounts", null);
        String[] strings = {"12345678", "543", "Добавить"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, strings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accounts_list.setAdapter(adapter);
        accounts_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String person_account = parent.getItemAtPosition(position).toString(); //selected item
                List<Meter> meters_list_new = server.getMeters(person_account, "123");
                MeterAdapter adapter = (MeterAdapter) recyclerView.getAdapter();
                assert adapter != null;
                adapter.updateAllData(meters_list_new);
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initRecyclerView() {
        server = new ServerMock();
        // TODO: get from local data ?
        List<Meter> meters_list = server.getMeters("123456", "Выксунский район");
        MeterAdapter groupAdapter = new MeterAdapter(this, meters_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(groupAdapter);
    }

    private void initActivityResultLauncher() {
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        int id = data.getIntExtra("id", 0);
                        String decimals = data.getStringExtra("meter");
                        MeterAdapter adapter = (MeterAdapter) recyclerView.getAdapter();
                        assert adapter != null;
                        adapter.updateData(id, decimals);
                    }
                });
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

    private void dataSent() {
        // TODO: show fragment or text
        progressBar.setVisibility(View.INVISIBLE);
        sendButton.setText("Отправлено!");
    }

    private void startProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }
}