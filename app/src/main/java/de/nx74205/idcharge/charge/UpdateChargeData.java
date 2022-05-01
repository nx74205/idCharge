package de.nx74205.idcharge.charge;

import android.content.Context;
import android.widget.Toast;
import com.google.gson.*;
import de.nx74205.idcharge.R;
import de.nx74205.idcharge.database.LocalChargeRepository;
import de.nx74205.idcharge.database.RemoteChargeRepository;
import de.nx74205.idcharge.model.LocalChargeData;
import de.nx74205.idcharge.model.RemoteChargeData;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UpdateChargeData {

    private final ChargeDataApi chargeDataApi;
    private final Context context;
    private final RemoteChargeRepository remoteChargeRepository;
    private final LocalChargeRepository localChargeRepository;
    private final String vin;

    private boolean getFinished;
    private boolean postFinished;


    public UpdateChargeData(Context context) {
        this.context = context;

        vin = context.getResources().getString(R.string.vin);

        remoteChargeRepository = new RemoteChargeRepository(context);
        localChargeRepository = new LocalChargeRepository(context);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext)
                    throws JsonParseException {
                return LocalDateTime.parse(json.getAsJsonPrimitive().getAsString());
            }
                })
                .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                    public JsonElement serialize(LocalDateTime date, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    }
                })
                .create();

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .callTimeout(10, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(context.getResources().getString(R.string.baseuri))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        chargeDataApi = retrofit.create(ChargeDataApi.class);


    }

    public void getRemoteData() {

        getFinished = false;

        Call<List<RemoteChargeData>> call = chargeDataApi.getNewChargeData(vin, "NOT_SYNCED");
        call.enqueue(new Callback<List<RemoteChargeData>>() {
            @Override
            public void onResponse(Call<List<RemoteChargeData>> call, Response<List<RemoteChargeData>> response) {

                if (response.body() != null) {
                    List<RemoteChargeData> chargeDataList = response.body();
                    if (chargeDataList.size() > 0) {
                        remoteChargeRepository.deleteUnUsed();
                        for (RemoteChargeData chargeData : chargeDataList) {
                            remoteChargeRepository.update(chargeData);
                        }
                    }
                }
                getFinished = true;
            }

            @Override
            public void onFailure(Call<List<RemoteChargeData>> call, Throwable t) {

                getFinished = true;
            }
        });

    }

    public void postMobileData(LocalChargeData localChargeData) {
        localChargeData.setVin(vin);
        Call call = chargeDataApi.postChargeData(localChargeData);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    postFinished = true;
                    localChargeRepository.delete(localChargeData);
                } else {
                    switch (response.code()) {
                        case 404:
                            Toast.makeText(context, "Sync: Charge-Record not found", Toast.LENGTH_SHORT).show();
                            remoteChargeRepository.deleteById(localChargeData.getChargeDataId());
                            localChargeData.setChargeDataId(null);
                            break;
                        case 400:
                            Toast.makeText(context, "Sync: Vehicle Fin Missing or wrong", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(context, "Sync: Unkwown Error", Toast.LENGTH_SHORT).show();
                    }
                    postFinished = true;
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                postFinished = true;

            }
        });


    }

    public boolean checkUpdateFinished() {
        return getFinished;
    }
}