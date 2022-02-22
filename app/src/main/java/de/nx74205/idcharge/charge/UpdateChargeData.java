package de.nx74205.idcharge.charge;

import android.content.Context;
import com.google.gson.*;
import de.nx74205.idcharge.R;
import de.nx74205.idcharge.database.RemoteChargeRepository;
import de.nx74205.idcharge.model.RemoteChargeData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

public class UpdateChargeData {

    private final Context context;
    private final RemoteChargeRepository repository;

    private boolean updateFinished;


    public UpdateChargeData(Context context) {
        this.context = context;
        repository = new RemoteChargeRepository(context);
    }

    public void update() {

        updateFinished = false;

        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return LocalDateTime.parse(json.getAsJsonPrimitive().getAsString());
            }
        }).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getResources().getString(R.string.baseuri))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ChargeDataApi chargeDataApi = retrofit.create(ChargeDataApi.class);

        callData(retrofit, chargeDataApi);
    }

    private void callData(Retrofit retrofit, ChargeDataApi chargeDataApi) {
        Call<List<RemoteChargeData>> call = chargeDataApi.getNewChargeData(context.getResources().getString(R.string.vin), "NOT_SYNCED");
        call.enqueue(new Callback<List<RemoteChargeData>>() {
            @Override
            public void onResponse(Call<List<RemoteChargeData>> call, Response<List<RemoteChargeData>> response) {

                if (response.body() != null) {
                    List<RemoteChargeData> chargeDataList = response.body();
                    for (RemoteChargeData chargeData : chargeDataList) {
                        repository.update(chargeData);
                    }
                }

                updateFinished = true;

            }

            @Override
            public void onFailure(Call<List<RemoteChargeData>> call, Throwable t) {

                updateFinished = true;


            }
        });
    }

    public boolean checkUpdateFinished() {
        return updateFinished;
    };



}