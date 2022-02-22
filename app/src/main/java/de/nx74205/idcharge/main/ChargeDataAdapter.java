package de.nx74205.idcharge.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import de.nx74205.idcharge.R;
import de.nx74205.idcharge.model.LocalChargeData;

public class ChargeDataAdapter extends RecyclerView.Adapter<ChargeDataAdapter.ViewHolder> {

    private final ArrayList<LocalChargeData> localChargeDataList;
    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClicked(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView timeStampView;
        public TextView mileageView;
        public TextView distanceView;
        public TextView chargedKwPaidView;
        public TextView priceView;
        public TextView chargeTypView;
        public TextView bcConsumptionView;
        public TextView socView;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            timeStampView = itemView.findViewById(R.id.timeStampView);
            mileageView = itemView.findViewById(R.id.mileageView);
            distanceView = itemView.findViewById(R.id.distanceView);
            chargedKwPaidView = itemView.findViewById(R.id.chargedKwPaidView);
            priceView = itemView.findViewById(R.id.priceView);
            chargeTypView = itemView.findViewById(R.id.chargeTypeView);
            bcConsumptionView = itemView.findViewById(R.id.bcConsumptionView);
            socView = itemView.findViewById(R.id.socView);


            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClicked(position);
                    }
                }
            });

        }
    }

    public ChargeDataAdapter(ArrayList<LocalChargeData> localChargeDataList) {
        this.localChargeDataList = localChargeDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_local_charge_data, parent, false);
        return new ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocalChargeData currentItem = localChargeDataList.get(position);

        DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");
        DecimalFormat noDigitsFormat = new DecimalFormat("###,###");

        holder.timeStampView.setText(currentItem.getTimeStamp().format(DATE_FORMAT));
        holder.mileageView.setText(noDigitsFormat.format(currentItem.getMileage()));
        holder.distanceView.setText(noDigitsFormat.format(currentItem.getDistance()));
        holder.chargedKwPaidView.setText(decimalFormat.format(currentItem.getChargedKwPaid()));
        holder.priceView.setText(decimalFormat.format(currentItem.getPrice()).concat(" €"));
        holder.chargeTypView.setText(currentItem.getChargeTyp());
        holder.bcConsumptionView.setText(decimalFormat.format(currentItem.getBcConsumption()));
        holder.socView.setText(noDigitsFormat.format(currentItem.getTargetSoc()).concat(" %"));

    }

    @Override
    public int getItemCount() {
        return localChargeDataList.size();
    }
}