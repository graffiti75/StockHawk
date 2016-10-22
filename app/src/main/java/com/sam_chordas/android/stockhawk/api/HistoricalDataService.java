package com.sam_chordas.android.stockhawk.api;

import com.sam_chordas.android.stockhawk.model.HistoricalData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * HistoricalDataService.java.
 *
 * @author Rodrigo Cericatto
 * @since Oct 7, 2016
 */
public interface HistoricalDataService {
    @GET("v1/public/yql")
    Call<HistoricalData> getHistoricalData(@Query("q") String q,
        @Query("format") String format, @Query("env") String env);
}