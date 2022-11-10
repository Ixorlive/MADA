package com.example.mada_2.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.*;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.mada_2.MainFragment;
import com.example.mada_2.R;
import com.example.mada_2.dto.ResponseMeterDataDto;
import com.example.mada_2.server_connection.service.HttpBaseSource;

import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class authorisation extends Fragment {


    List<String> dist = null;

    public authorisation(List<String> dist) {
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
            Bitmap bitmap = null;
            EditText password = view.findViewById(R.id.password);
            try {
                String bb = HttpBaseSource.Companion.getClient()
                        .authorizeAsync(password.getText().toString(),
                                spinner.getSelectedItem().toString())
                        .get().getCaptchaBase64();
                byte[] b = Base64.getDecoder().decode(bb);
                bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Dialog d = createDialog(bitmap);
            d.show();
        });
        return view;
    }

    public Dialog createDialog(Bitmap map) {
        assert map != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_captcha, null);
        ImageView v = view.findViewById(R.id.img_captcha);
        EditText mEdit = view.findViewById(R.id.text_captcha);
        v.setImageBitmap(map);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String value = mEdit.getText().toString();
                        try {
                            ResponseMeterDataDto responseMeterDataDto =
                                    HttpBaseSource.Companion
                                            .getClient()
                                            .submitCaptchaAsync(value)
                                            .get();
                            Log.d("SubmitCaptcha", responseMeterDataDto.toString());
                            if(responseMeterDataDto.component1())
                            {
                                Fragment mainFragment = new MainFragment();
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .add(R.id.fragment_container, mainFragment)
                                        .commit();
                            }
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }
}