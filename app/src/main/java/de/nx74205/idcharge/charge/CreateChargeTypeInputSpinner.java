package de.nx74205.idcharge.charge;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import de.nx74205.idcharge.R;

public class CreateChargeTypeInputSpinner implements AdapterView.OnItemSelectedListener {

    private String chargeTypInputSelected;

    public Spinner createSpinner(Context context, Spinner spinner, String chargeType) {
        ArrayAdapter<CharSequence> chargeTypeAdapter = ArrayAdapter.createFromResource(context, R.array.ladetypen,
                android.R.layout.simple_spinner_item);
        chargeTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(chargeTypeAdapter);
        spinner.setOnItemSelectedListener(this);

        for(int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(chargeType)) {
                spinner.setSelection(i);
                break;
            }
        }

        return spinner;
    }

    public String getChargeTypInputSelected() {
        return chargeTypInputSelected;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chargeTypInputSelected = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        chargeTypInputSelected = adapterView.getItemAtPosition(0).toString();

    }

}
