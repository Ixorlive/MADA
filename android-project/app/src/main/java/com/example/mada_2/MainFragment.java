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

import android.os.Handler;
import android.preference.PreferenceManager;
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
import com.example.mada_2.database.DBWorker;
import com.example.mada_2.database.User;
import com.example.mada_2.server_connection.Server;
import com.example.mada_2.server_connection.ServerMock;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainFragment extends Fragment {

    ActivityResultLauncher<Intent> activityResultLauncher;
    private Spinner accounts_list;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    Button sendButton;
    DBWorker dbWorker;
    User current_user; // current personal account

    public MainFragment() {
        // Required empty public constructor
    }

    public MainFragment(User curr_user) {
        current_user = curr_user;
    }

    public void ShowCameraActivity(int id) {
        Intent intent = new Intent(getActivity(), NewCamera.class);
        intent.putExtra("id", id);
        activityResultLauncher.launch(intent);
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
        dbWorker = new DBWorker(getContext());
        if (current_user == null) {
            current_user = dbWorker.getFirstUser();
        }
        initSpinner();
        initRecyclerView();
        initActivityResultLauncher();
        return view;
    }

    private void initSpinner() {
        //DBWorker dbWorker = new DBWorker(getContext());
        List<String> allUser = dbWorker.getPasswordAllUser();
        allUser.add("Добавить");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, allUser);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accounts_list.setAdapter(adapter);
        accounts_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String person_account = parent.getItemAtPosition(position).toString(); //selected item
                if (!person_account.isEmpty()) {
                    current_user = new User(person_account, "doesn't matter");
                    List<Meter> meters_list_new = dbWorker.getAllMeter(current_user);
                    MeterAdapter adapter = (MeterAdapter) recyclerView.getAdapter();
                    assert adapter != null;
                    adapter.updateAllData(meters_list_new);

                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initRecyclerView() {
        //DBWorker dbWorker = new DBWorker(getContext());
        MeterAdapter groupAdapter = new MeterAdapter(this, dbWorker.getAllMeter(current_user));
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

    private void dataSent() {
        // TODO: show fragment or text
        progressBar.setVisibility(View.INVISIBLE);
        sendButton.setText("Отправлено!");
    }

    private void startProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }
}