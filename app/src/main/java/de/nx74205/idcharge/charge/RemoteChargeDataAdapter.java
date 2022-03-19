package de.nx74205.idcharge.charge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.nx74205.idcharge.R;
import de.nx74205.idcharge.main.ChargeDataAdapter;
import de.nx74205.idcharge.model.RemoteChargeData;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class RemoteChargeDataAdapter extends RecyclerView.Adapter<RemoteChargeDataAdapter.ViewHolderClass> {

    private final ArrayList<RemoteChargeData> remoteChargeDataList;
    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private RemoteChargeDataAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClicked(int position);

    }
    public void setOnItemClickListener(RemoteChargeDataAdapter.OnItemClickListener listener) {
        this.listener = listener;

    }
    public class ViewHolderClass extends RecyclerView.ViewHolder {

        public TextView remoteFromTimeStampView;
        public TextView remoteToTimestampView;
        public TextView remoteChargedKwView;
        public TextView remoteSocStartView;
        public TextView remoteSocEndView;
        public TextView remoteAvgChargedKwView;
        public TextView remoteStatusView;
        public ImageView remoteSquareView;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            remoteFromTimeStampView = itemView.findViewById(R.id.remoteFromTimeStampView);
            remoteToTimestampView = itemView.findViewById(R.id.remoteToTimestampView);
            remoteChargedKwView = itemView.findViewById(R.id.remoteChargedKwView);
            remoteSocStartView = itemView.findViewById(R.id.remoteSocStartView);
            remoteSocEndView = itemView.findViewById(R.id.remoteSocEndView);
            remoteAvgChargedKwView = itemView.findViewById(R.id.remoteAvgChargedKwView);
            remoteStatusView = itemView.findViewById(R.id.remoteStatusView);
            remoteSquareView = itemView.findViewById(R.id.remoteSquareView);

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

    public RemoteChargeDataAdapter(ArrayList<RemoteChargeData> remoteChargeDataList) {
        this.remoteChargeDataList = remoteChargeDataList;
    }

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_remote_charge_data, parent, false);
        return new ViewHolderClass(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass holder, int position) {
        RemoteChargeData currentItem = remoteChargeDataList.get(position);

        if (currentItem.getMobileChargeId() == null || currentItem.getMobileChargeId() <= 0) {
            holder.remoteSquareView.setVisibility(View.INVISIBLE);
        } else {
            holder.remoteSquareView.setVisibility(View.VISIBLE);
        }

        DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");
        DecimalFormat noDigitsFormat = new DecimalFormat("###,###");
        String noDate = "00.00.0000 00:00";

        if (currentItem.getStartOfCharge() != null) {
            holder.remoteFromTimeStampView.setText(currentItem.getStartOfCharge().format(DATE_FORMAT));
        } else {
            holder.remoteFromTimeStampView.setText(noDate);
        }
        if (currentItem.getEndOfCharge() != null) {
            holder.remoteToTimestampView.setText(currentItem.getEndOfCharge().format(DATE_FORMAT));
        } else {
            holder.remoteToTimestampView.setText(noDate);
        }
        holder.remoteChargedKwView.setText(decimalFormat.format(currentItem.getQuantityChargedKwh()));
        holder.remoteSocStartView.setText(noDigitsFormat.format(currentItem.getSocStart()).concat(" %"));
        holder.remoteSocEndView.setText(noDigitsFormat.format(currentItem.getSocEnd()).concat(" %"));
        holder.remoteAvgChargedKwView.setText(decimalFormat.format(currentItem.getAverageChargingPower()));
        holder.remoteStatusView.setText(currentItem.getChargingStatus().name());

    }

    @Override
    public int getItemCount() {
        return remoteChargeDataList.size();
    }
}