package de.nx74205.idcharge.charge;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

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

public class ChargingActivity extends AppCompatActivity implements ChargeDataAssignDialog.OnInInputListener {

    private UpdateChargeData updateChargeData;

    EditText timeStampInput;
    EditText mileageInput;
    EditText distanceInput;
    EditText chargedKwPaidInput;
    EditText priceInput;
    EditText socInput;

    TextView fragmentText;

    CreateChargeTypeInputSpinner chargeTypeInputSpinner;
    Spinner chargeTypeInput;
    EditText bcConsumptionInput;
    Button chargeAssignButton;

    LocalChargeData data;
    Long oldMileage;
    Long oldDistance;

    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charging_data);

        updateChargeData = new UpdateChargeData(this);

        Intent intent = getIntent();
        data = (LocalChargeData)intent.getSerializableExtra("DATA");
        oldMileage = data.getMileage();
        oldDistance = (data.getDistance() == null) ? 0L : data.getDistance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Eintrag bearbeiten");
        actionBar.setDisplayHomeAsUpEnabled(true);

        createTimeStampInput();
        createMileageInput();
        createChargeTypeInputSpinner();
        createChargeAssignButton();

        distanceInput = findViewById(R.id.distanceInput);
        chargedKwPaidInput = findViewById(R.id.chargedKwPaidInput);
        priceInput = findViewById(R.id.priceInput);
        socInput = findViewById(R.id.socInput);
        bcConsumptionInput = findViewById(R.id.bcConsumptionInput);
        fragmentText = findViewById(R.id.fragmentText);

        distanceInput.setFilters(new InputFilter[] {new InputFilterMinMax(1, 2000)});
        distanceInput.setText(convertToDisplayNum(oldDistance, ""));
        socInput.setFilters(new InputFilter[] {new InputFilterMinMax("1", "100")});
        priceInput.setFilters(new InputFilter[]{new InputFilterNoSign()});

        chargedKwPaidInput.setText(convertToDisplayNum(data.getChargedKwPaid(), ""));
        priceInput.setText(convertToDisplayNum(data.getPrice(), ""));
        socInput.setText(convertToDisplayNum(data.getTargetSoc(), ""));
        bcConsumptionInput.setText(convertToDisplayNum(data.getBcConsumption(), null));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.charge_data_menu, menu);
        startRefreshAnimation(menu);
        updateChargeData.update();

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
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
                break;

            case R.id.refresh_data:
/*
                if(item.getActionView()!=null)
                {
                    // Remove the animation.
                    item.getActionView().clearAnimation();
                    item.setActionView(null);
                }
*/
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void sendInput(String input) {
        fragmentText.setText(input);

    }

    private void createTimeStampInput() {
        timeStampInput = findViewById((R.id.timeStampInput));
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
        mileageInput = findViewById(R.id.mileageInput);

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
        chargeTypeInput = chargeTypeInputSpinner.createSpinner(this, findViewById(R.id.chargeTypeInput), data.getChargeTyp());
    }

    private void createChargeAssignButton() {
        chargeAssignButton = findViewById(R.id.chargeAssignButton);
        chargeAssignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChargeDataAssignDialog dialog = new ChargeDataAssignDialog();
                dialog.show(getSupportFragmentManager(), "ChargeAssignDialog");
            }
        });
    }

    private void startRefreshAnimation(Menu menu) {
        MenuItem item = menu.findItem(R.id.refresh_data);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView iv = (ImageView)inflater.inflate(R.layout.iv_refresh_data,null);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!updateChargeData.checkUpdateFinished()) {
                    iv.startAnimation(rotation);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        iv.startAnimation(rotation);
        item.setActionView(iv);


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

