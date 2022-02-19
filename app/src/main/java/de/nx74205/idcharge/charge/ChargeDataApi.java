package de.nx74205.idcharge.charge;

import de.nx74205.idcharge.model.RemoteChargeData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface ChargeDataApi {

    @GET("getcharge/{vin}")
    Call<List<RemoteChargeData>> getNewChargeData(@Path("vin") String vin,
                                                  @Query("operation") String operation);
    /*
    @Query
    @Path
     */
}
