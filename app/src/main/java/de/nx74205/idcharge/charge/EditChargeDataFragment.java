package de.nx74205.idcharge.charge;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.nx74205.idcharge.R;
import de.nx74205.idcharge.charge.helper.InputFilterMinMax;
import de.nx74205.idcharge.charge.helper.InputFilterNoSign;
import de.nx74205.idcharge.model.LocalChargeData;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class EditChargeDataFragment extends Fragment {

    private View view;

    private EditText timeStampInput;
    private EditText mileageInput;
    private EditText distanceInput;
    private EditText chargedKwPaidInput;
    private EditText priceInput;
    private EditText socInput;

    CreateChargeTypeInputSpinner chargeTypeInputSpinner;
    Spinner chargeTypeInput;
    EditText bcConsumptionInput;

    private LocalChargeData data;
    private Long oldMileage;
    private Long oldDistance;

    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public EditChargeDataFragment(LocalChargeData data) {
        this.data = data;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_edit_charge_data, container, false);
        updateLocalChargeData(data);

        return view;
    }

    private void updateLocalChargeData(LocalChargeData data) {
        this.data = data;

        oldMileage = data.getMileage();
        oldDistance = (data.getDistance() == null) ? 0L : data.getDistance();

        createTimeStampInput();
        createMileageInput();
        createChargeTypeInputSpinner();

        distanceInput = view.findViewById(R.id.distanceInput);
        chargedKwPaidInput = view.findViewById(R.id.chargedKwPaidInput);
        priceInput = view.findViewById(R.id.priceInput);
        socInput = view.findViewById(R.id.socInput);
        bcConsumptionInput = view.findViewById(R.id.bcConsumptionInput);

        distanceInput.setFilters(new InputFilter[] {new InputFilterMinMax(1, 2000)});
        distanceInput.setText(convertToDisplayNum(oldDistance, ""));
        socInput.setFilters(new InputFilter[] {new InputFilterMinMax("1", "100")});
        priceInput.setFilters(new InputFilter[]{new InputFilterNoSign()});

        chargedKwPaidInput.setText(convertToDisplayNum(data.getChargedKwPaid(), ""));
        priceInput.setText(convertToDisplayNum(data.getPrice(), ""));
        socInput.setText(convertToDisplayNum(data.getTargetSoc(), ""));
        bcConsumptionInput.setText(convertToDisplayNum(data.getBcConsumption(), null));
    }

    public LocalChargeData getData() {
        return createRecord();
    }

    private void createTimeStampInput() {
        timeStampInput = view.findViewById((R.id.timeStampInput));
        timeStampInput.setInputType(InputType.TYPE_NULL);
        timeStampInput.setFocusableInTouchMode(false);
        timeStampInput.setText(data.getTimeStamp().format(DATE_FORMAT));
        timeStampInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimeDialog(timeStampInput);
            }
        });
    }

    private void createMileageInput() {
        mileageInput = view.findViewById(R.id.mileageInput);

        mileageInput.setFilters(new InputFilter[]{new InputFilterMinMax(1, 10000000)});
        mileageInput.setText(convertToDisplayNum(data.getMileage(), ""));
        mileageInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (!b) {
                    Long currentMileage = Long.parseLong(convertToNum(mileageInput.getText(), "0"));
                    if (currentMileage > oldMileage) {
                        oldDistance += (currentMileage - oldMileage);
                        distanceInput.setText(convertToDisplayNum(oldDistance, ""));
                    }
                }
            }
        });
    }

    private void createChargeTypeInputSpinner() {

        chargeTypeInputSpinner = new CreateChargeTypeInputSpinner();
        chargeTypeInput = chargeTypeInputSpinner.createSpinner(getActivity(), view.findViewById(R.id.chargeTypeInput), data.getChargeTyp());
    }

    private void showDateTimeDialog(EditText dateTimeIn) {

        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                        dateTimeIn.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };

                new TimePickerDialog(getActivity(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), true).show();
            }
        };

        new DatePickerDialog(getActivity(), dateSetListener, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private LocalChargeData createRecord() {

        data.setTimeStamp(LocalDateTime.parse(timeStampInput.getText().toString(), DATE_FORMAT));
        data.setMileage(Long.parseLong(convertToNum(mileageInput.getText(), "0")));
        data.setDistance(Long.parseLong(convertToNum(distanceInput.getText(), "0")));
        data.setChargedKwPaid(Double.parseDouble(convertToNum(chargedKwPaidInput.getText(), "0")));
        data.setPrice(Double.parseDouble(convertToNum(priceInput.getText(), "0")));
        data.setTargetSoc(Integer.parseInt(convertToNum(socInput.getText(), "0")));
        data.setChargeTyp(chargeTypeInputSpinner.getChargeTypInputSelected());
        data.setBcConsumption(Double.parseDouble(convertToNum(bcConsumptionInput.getText(), "0")));

        return data;

    }

    private String convertToDisplayNum(Object obj, String defaultReturn) {
        if(obj != null && !obj.toString().isEmpty() ) {
            return obj.toString().replace(".", ",");
        } else {
            return defaultReturn;
        }
    }

    private String convertToNum(Object obj, String defaultReturn) {
        if(obj != null && !obj.toString().isEmpty() ) {
            return obj.toString().replace(",", ".");
        } else {
            return defaultReturn;
        }
    }
}
