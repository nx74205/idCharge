package de.nx74205.idcharge.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import de.nx74205.idcharge.R;
import de.nx74205.idcharge.model.LocalChargeData;

public class ChargeDataAdapter extends RecyclerView.Adapter<ChargeDataAdapter.ViewHolder> {

    private ArrayList<LocalChargeData> localChargeDataList;
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
        public TextView chargedKwPaidView;
        public TextView priceView;
        public TextView chargeTypView;
        public TextView bcConsumptionView;
        public TextView socView;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            timeStampView = itemView.findViewById(R.id.timeStampView);
            mileageView = itemView.findViewById(R.id.mileageView);
            chargedKwPaidView = itemView.findViewById(R.id.chargedKwPaidView);
            priceView = itemView.findViewById(R.id.priceView);
            chargeTypView = itemView.findViewById(R.id.chargeTypeView);
            bcConsumptionView = itemView.findViewById(R.id.bcConsumptionView);
            socView = itemView.findViewById(R.id.socView);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClicked(position);
                        }
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_view, parent, false);
        ViewHolder vh = new ViewHolder(v, listener);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocalChargeData currentItem = localChargeDataList.get(position);

        holder.timeStampView.setText(currentItem.getTimeStamp().format(DATE_FORMAT));
        holder.mileageView.setText(currentItem.getMileage().toString());
        holder.chargedKwPaidView.setText(currentItem.getChargedKwPaid().toString());
        holder.priceView.setText(currentItem.getPrice().toString());
        holder.chargeTypView.setText(currentItem.getChargeTyp());
        holder.bcConsumptionView.setText(currentItem.getBcConsumption().toString());
        holder.socView.setText(currentItem.getTargetSoc().toString().concat(" %"));

    }

    @Override
    public int getItemCount() {
        return localChargeDataList.size();
    }
}