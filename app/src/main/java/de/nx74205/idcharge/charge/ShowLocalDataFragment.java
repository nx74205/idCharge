package de.nx74205.idcharge.charge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.nx74205.idcharge.R;
import de.nx74205.idcharge.model.LocalChargeData;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class ShowLocalDataFragment extends Fragment {

    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final LocalChargeData data;

    public ShowLocalDataFragment(LocalChargeData data) {
        this.data = data;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_local_charge_data, container, false);

        TextView timeStampView;
        TextView mileageView;
        TextView distanceView;
        TextView chargedKwPaidView;
        TextView priceView;
        TextView chargeTypView;
        TextView bcConsumptionView;
        TextView socView;


        timeStampView = v.findViewById(R.id.timeStampView);
        mileageView = v.findViewById(R.id.mileageView);
        distanceView = v.findViewById(R.id.distanceView);
        chargedKwPaidView = v.findViewById(R.id.chargedKwPaidView);
        priceView = v.findViewById(R.id.priceView);
        chargeTypView = v.findViewById(R.id.chargeTypeView);
        bcConsumptionView = v.findViewById(R.id.bcConsumptionView);
        socView = v.findViewById(R.id.socView);

        DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");
        DecimalFormat noDigitsFormat = new DecimalFormat("###,###");

        timeStampView.setText(data.getTimeStamp().format(DATE_FORMAT));
        mileageView.setText(noDigitsFormat.format((data.getMileage() == null) ? 0L: data.getMileage()));
        distanceView.setText(noDigitsFormat.format((data.getDistance() == null) ? 0L: data.getDistance()));
        chargedKwPaidView.setText(decimalFormat.format((data.getChargedKwPaid() == null) ? 0D : data.getChargedKwPaid()));
        priceView.setText(decimalFormat.format((data.getPrice() == null) ? 0D : data.getPrice()).concat(" €"));
        chargeTypView.setText(data.getChargeTyp());
        bcConsumptionView.setText(decimalFormat.format((data.getBcConsumption() == null) ? 0D : data.getBcConsumption()));
        socView.setText(noDigitsFormat.format((data.getTargetSoc() == null) ? 0L : data.getTargetSoc()).concat(" %"));

        return v;
    }

}
