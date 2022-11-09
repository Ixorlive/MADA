package com.example.mada_2.Catalog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mada_2.R;
import com.example.mada_2.main_camera;

import java.util.List;

import static com.google.android.material.color.MaterialColors.getColor;

public class MeterAdapter extends RecyclerView.Adapter<MeterAdapter.MeterVH> {

    main_camera fragment;
    Context mContext;

    public static final String TAG = "MeterAdapter";
    List<Meter> Meters;

    public MeterAdapter(main_camera current_fragment, List<Meter> groupList) {
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

    public void updateAllData(List<Meter> viewModels) {
        Meters.clear();
        Meters.addAll(viewModels);
        notifyDataSetChanged();
    }

    public void updateData(int id, String data) {
        Meters.forEach((Meter meter)-> {
            if (meter.id == id) {
                meter.meter_reading = data;
            }
        });
        notifyDataSetChanged();
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
        void bindTo(Meter currentMeter) {
            meter_title.setText(currentMeter.name);
            editText.setText(currentMeter.meter_reading);
            btn_camera.setOnClickListener((View view) -> {
                fragment.ShowCameraActivity(currentMeter.id);
            });
        }

        @Override
        public void onClick(View v) {
            // No action to click
        }
    }
}