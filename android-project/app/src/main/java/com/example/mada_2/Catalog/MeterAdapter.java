package com.example.mada_2.Catalog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mada_2.CameraActivity;
import com.example.mada_2.R;
import java.util.List;

import static com.google.android.material.color.MaterialColors.getColor;

public class MeterAdapter extends RecyclerView.Adapter<MeterAdapter.MeterVH> {

    Fragment fragment;
    Context mContext;

    public static final String TAG = "MeterAdapter";
    List<Meter> Meters;
    public MeterAdapter(Fragment current_fragment, List<Meter> groupList) {
        this.fragment = current_fragment;
        this.Meters = groupList;
    }

    @NonNull
    @Override
    public MeterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meters_list, parent, false);
        mContext = parent.getContext();
        return new MeterVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeterVH holder, int position) {
        Meter meter = Meters.get(position);
        holder.bindTo(meter);
    }

    @Override
    public int getItemCount() {
        return Meters.size();
    }

    class MeterVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView meter_title;
        EditText editText;
        ImageView btn_camera;

        public MeterVH(@NonNull final View itemView) {
            super(itemView);

            meter_title = itemView.findViewById(R.id.text_meter_name);
            editText = itemView.findViewById(R.id.text_meter_reading);
            btn_camera = itemView.findViewById(R.id.btn_camera);
            itemView.setOnClickListener(this);
        }

        @SuppressLint({"ResourceAsColor", "RestrictedApi", "ResourceType"})
        void bindTo(Meter currentMeter){
            meter_title.setText(currentMeter.name);
            btn_camera.setOnClickListener((View view) -> {
                Intent intent = new Intent(fragment.getActivity(), CameraActivity.class);
                fragment.startActivity(intent);
            });
        }

        @Override
        public void onClick(View v) {
            // No action to click
        }
    }
}