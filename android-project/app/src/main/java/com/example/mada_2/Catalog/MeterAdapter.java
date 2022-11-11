package com.example.mada_2.Catalog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mada_2.R;
import com.example.mada_2.MainFragment;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.material.color.MaterialColors.getColor;

public class MeterAdapter extends RecyclerView.Adapter<MeterAdapter.MeterVH> {

    MainFragment fragment;
    Context mContext;

    public static final String TAG = "MeterAdapter";
    List<Meter> Meters;

    public MeterAdapter(MainFragment current_fragment, List<Meter> groupList) {
        this.fragment = current_fragment;
        this.Meters = new ArrayList<>(groupList);
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
        if (Meters != null)
            return Meters.size();
        else
            return 0;
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

    public List<Meter> getItems() {
        return Meters;
    }

    class MeterVH extends RecyclerView.ViewHolder {

        TextView meter_title;
        EditText editText;
        ImageView btn_camera;
        ImageView img_warning;

        public MeterVH(@NonNull final View itemView) {
            super(itemView);

            meter_title = itemView.findViewById(R.id.text_meter_name);
            editText = itemView.findViewById(R.id.text_meter_reading);
            btn_camera = itemView.findViewById(R.id.btn_camera);
            img_warning = itemView.findViewById(R.id.img_warning);
        }
        @SuppressLint({"ResourceAsColor", "RestrictedApi", "ResourceType"})
        void bindTo(Meter currentMeter) {
            meter_title.setText(currentMeter.name);
            editText.setText(currentMeter.meter_reading);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // TODO: check correction of input
                    if (fragment.checkCorrection(currentMeter, s)) {
                        currentMeter.meter_reading = s.toString();
                        img_warning.setVisibility(View.INVISIBLE);
                    } else {
                        img_warning.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            btn_camera.setOnClickListener((View view) -> {
                fragment.ShowCameraActivity(currentMeter.id);
            });
        }
    }
}