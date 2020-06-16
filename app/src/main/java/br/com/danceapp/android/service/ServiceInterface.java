package br.com.danceapp.android.service;

import java.util.Date;
import java.util.List;

import br.com.danceapp.android.model.Event;
import br.com.danceapp.android.model.Owner;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Define os endpoints da API exposta no Backend do DanceApp
 */

public interface ServiceInterface {

    @GET("owners/{id}")
    Call<Owner> onwerDetail(@Path("id") String id);

    @GET("near_events/")
    Call<List<Event>> nearEventsList(@Query("city_name") String cityName,
                                     @Query("cli_dt") String clientDateTime);

    @GET("near_events/")
    Call<List<Event>> nearEventsList(@Query("latitude") Double latitude,
                                     @Query("longitude") Double longitude,
                                     @Query("distance") Integer distance,
                                     @Query("cli_dt") String clientDateTime);

    @GET("events/{id}")
    Call<Event> eventDetail(@Path("id") String id);

    @GET("cities/")
    Call<List<String>> citiesList();
}