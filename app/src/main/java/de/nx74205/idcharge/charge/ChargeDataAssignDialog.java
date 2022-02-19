package de.nx74205.idcharge.charge;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import de.nx74205.idcharge.R;
import de.nx74205.idcharge.model.RemoteChargeData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;

public class ChargeDataAssignDialog extends DialogFragment {

    public interface OnInInputListener {
        void sendInput(String input);
    }

    public OnInInputListener inputListener;

    private EditText someText;
    private Button okButton;
    private Button cancelButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.charging_data_assign, container,false);
        someText = view.findViewById(R.id.someText);
        okButton = view.findViewById(R.id.chargeAssignOkButton);
/*
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.baseuri))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ChargeDataApi chargeDataApi = retrofit.create(ChargeDataApi.class);
        Call<List<RemoteChargeData>> call = chargeDataApi.getNewChargeData(getResources().getString(R.string.vin), "NOT_SYNCED");
        call.enqueue(new Callback<List<RemoteChargeData>>() {
            @Override
            public void onResponse(Call<List<RemoteChargeData>> call, Response<List<RemoteChargeData>> response) {

            }

            @Override
            public void onFailure(Call<List<RemoteChargeData>> call, Throwable t) {

            }
        });
*/
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputText =someText.getText().toString();
                if (!inputText.equals("")) {
                    inputListener.sendInput(inputText);
                    getDialog().dismiss();
                }

            }
        });

        cancelButton = view.findViewById(R.id.chargeAssignCancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            inputListener = (OnInInputListener) getActivity();
        } catch (ClassCastException e) {
            throw e;
        }
    }
}
