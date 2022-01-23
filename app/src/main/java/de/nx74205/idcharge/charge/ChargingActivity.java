package de.nx74205.idcharge.charge;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import de.nx74205.idcharge.R;
import de.nx74205.idcharge.charge.helper.InputFilterMinMax;
import de.nx74205.idcharge.charge.helper.InputFilterNoSign;
import de.nx74205.idcharge.model.LocalChargeData;

public class ChargingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText timeStampInput;
    EditText mileageInput;
    EditText distanceInput;
    EditText chargedKwPaidInput;
    EditText priceInput;
    EditText socInput;
    Spinner chargeTypeInput;
    EditText bcConsumptionInput;

    LocalChargeData data;
    Long oldMileage;
    Long oldDistance;
    String chargeTypInputSelected;

    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charging_data);

        Intent intent = getIntent();
        data = (LocalChargeData)intent.getSerializableExtra("DATA");
        oldMileage = data.getMileage();
        oldDistance = (data.getDistance() == null) ? 0L : data.getDistance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Eintrag bearbeiten");
        actionBar.setDisplayHomeAsUpEnabled(true);


        createChargeTypeInputSpinner();

        timeStampInput = findViewById((R.id.timeStampInput));
        mileageInput = findViewById(R.id.mileageInput);
        distanceInput = findViewById(R.id.distanceInput);
        chargedKwPaidInput = findViewById(R.id.chargedKwPaidInput);
        priceInput = findViewById(R.id.priceInput);
        socInput = findViewById(R.id.socInput);
        bcConsumptionInput = findViewById(R.id.bcConsumptionInput);

        distanceInput.setFilters(new InputFilter[] {new InputFilterMinMax(1, 2000)});
        distanceInput.setText(convertToDisplayNum(oldDistance, ""));
        socInput.setFilters(new InputFilter[] {new InputFilterMinMax("1", "100")});
        priceInput.setFilters(new InputFilter[]{new InputFilterNoSign()});

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

        timeStampInput.setInputType(InputType.TYPE_NULL);
        timeStampInput.setFocusableInTouchMode(false);
        timeStampInput.setText(data.getTimeStamp().format(DATE_FORMAT));
        timeStampInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimeDialog(timeStampInput);
            }
        });

        chargedKwPaidInput.setText(convertToDisplayNum(data.getChargedKwPaid(), ""));
        priceInput.setText(convertToDisplayNum(data.getPrice(), ""));
        socInput.setText(convertToDisplayNum(data.getTargetSoc(), ""));
        bcConsumptionInput.setText(convertToDisplayNum(data.getBcConsumption(), null));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.charge_data_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Intent resultIntent = new Intent();
        switch (item.getItemId()) {
            case R.id.save_page:
                resultIntent.putExtra("DATA", createRecord());

                setResult(RESULT_OK, resultIntent);
                finish();

                break;
            case R.id.delete_entry:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Auswahl").setMessage("Datensatz wirklich löschen?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        data.setMileage(-1L);
                        resultIntent.putExtra("DATA", data);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                });
                builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog myDialog=builder.create();
                myDialog.show();
        }

        return super.onOptionsItemSelected(item);
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

                new TimePickerDialog(ChargingActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), true).show();
            }
        };

        new DatePickerDialog(ChargingActivity.this, dateSetListener, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chargeTypInputSelected = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        chargeTypInputSelected = adapterView.getItemAtPosition(0).toString();

    }

    private void createChargeTypeInputSpinner() {

        chargeTypeInput = findViewById(R.id.chargeTypeInput);
        ArrayAdapter<CharSequence> chargeTypeAdapter = ArrayAdapter.createFromResource(this, R.array.ladetypen,
                android.R.layout.simple_spinner_item);
        chargeTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chargeTypeInput.setAdapter(chargeTypeAdapter);
        chargeTypeInput.setOnItemSelectedListener(this);

        for(int i = 0; i < chargeTypeInput.getCount(); i++) {
            if (chargeTypeInput.getItemAtPosition(i).toString().equals(data.getChargeTyp())) {
                chargeTypeInput.setSelection(i);
                break;
            }
        }
    }

    private LocalChargeData createRecord() {

        data.setTimeStamp(LocalDateTime.parse(timeStampInput.getText().toString(), DATE_FORMAT));
        data.setMileage(Long.parseLong(convertToNum(mileageInput.getText(), "0")));
        data.setDistance(Long.parseLong(convertToNum(distanceInput.getText(), "0")));
        data.setChargedKwPaid(Double.parseDouble(convertToNum(chargedKwPaidInput.getText(), "0")));
        data.setPrice(Double.parseDouble(convertToNum(priceInput.getText(), "0")));
        data.setTargetSoc(Integer.parseInt(convertToNum(socInput.getText(), "0")));
        data.setChargeTyp(chargeTypInputSelected);
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

