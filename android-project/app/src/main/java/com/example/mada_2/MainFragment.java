package com.example.mada_2;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.mada_2.Catalog.Meter;
import com.example.mada_2.Catalog.MeterAdapter;
import com.example.mada_2.database.DBWorker;
import com.example.mada_2.database.User;
import com.example.mada_2.ui.login.authorisation;

import java.util.ArrayList;
import java.util.List;

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

    public boolean checkCorrection(Meter meter, CharSequence new_data) {
        if (new_data.toString().isEmpty()) return false;
        double current_reading = Double.parseDouble(dbWorker.getMeterById(current_user, meter.id).meter_reading);
        double new_reading = Double.parseDouble(new_data.toString());

        return new_reading > current_reading;
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
            checkNewMeters();
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

    private void sendData(List<Meter> okList) {
        startProgressBar();
        for(Meter m: okList)
        {
            dbWorker.updateMeter(current_user, m);
        }
        (new Handler()).postDelayed(this::dataSent, 1500);
    }

    private void initSpinner() {
        List<String> allUser = dbWorker.getPasswordAllUser();
        allUser.add("????????????????");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, allUser);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accounts_list.setAdapter(adapter);
        accounts_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String person_account = parent.getItemAtPosition(position).toString(); //selected item
                if (!person_account.isEmpty()) {
                    if (person_account.equals("????????????????")) {
                        Fragment main = new authorisation();
                        requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                                main).commit();
                    }
                    else {
                        current_user = new User(person_account, "doesn't matter");
                        List<Meter> meters_list_new = dbWorker.getAllMeter(current_user);
                        MeterAdapter adapter = (MeterAdapter) recyclerView.getAdapter();
                        assert adapter != null;
                        adapter.updateAllData(meters_list_new);
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initRecyclerView() {
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
        progressBar.setVisibility(View.INVISIBLE);
        sendButton.setText("????????????????????!");
    }

    private void checkNewMeters() {
        MeterAdapter adapter = (MeterAdapter) recyclerView.getAdapter();
        List<Meter> new_meters = adapter.getItems();
        List<Meter> ok = new ArrayList<>();
        for (Meter meter : new_meters) {
            if (checkCorrection(meter, meter.meter_reading)) {
                ok.add(meter);
            }
        }
        if (ok.size() != new_meters.size()) {
            Dialog d = createDialog(ok);
            d.show();
        } else {
            sendData(ok);
        }
    }

    private void startProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private Dialog createDialog(List<Meter> ok) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(R.string.string_warning_wrong_readings)
                .setPositiveButton("????", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sendData(ok);
                    }
                })
                .setNegativeButton("????????????", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }
}