package com.sam_chordas.android.stockhawk.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.api.HistoricalDataService;
import com.sam_chordas.android.stockhawk.globals.Globals;
import com.sam_chordas.android.stockhawk.model.HistoricalData;
import com.sam_chordas.android.stockhawk.model.Quote;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * LineGraphActivity.java.
 *
 * @author Rodrigo Cericatto
 * @since Oct 21, 2016
 */
public class LineGraphActivity extends AppCompatActivity {

    //--------------------------------------------------
    // Constants
    //--------------------------------------------------

    private static final int LIMIT = 5;

    //--------------------------------------------------
    // Attributes
    //--------------------------------------------------

    /**
     * Context.
     */

    private LineGraphActivity mActivity = LineGraphActivity.this;

    /**
     * Chart values.
     */

    private String[] mLabels = new String[LIMIT];
    private float[] mValues = new float[LIMIT];

    private String mSymbol;

    //--------------------------------------------------
    // Activity Life Cycle
    //--------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        getExtras();
        getHistoricalData();
    }

    //--------------------------------------------------
    // Methods
    //--------------------------------------------------

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mSymbol = extras.getString("symbol");
        }
    }

    public void getHistoricalData() {
        // Retrofit.
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Globals.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        HistoricalDataService service = retrofit.create(HistoricalDataService.class);

        // Call.
        String today = getTodayDate();
        String query = getString(R.string.graph_query, mSymbol, today);
        Log.i(Globals.LOG_TAG, "LineGraphActivity.getHistoricalData() -> Query: " + query);
        Call<HistoricalData> call = service.getHistoricalData(query, Globals.FORMAT, Globals.ENV);
        call.enqueue(new Callback<HistoricalData>() {
            @Override
            public void onResponse(Call<HistoricalData> call, Response<HistoricalData> response) {
                if (response.isSuccessful()) {
                    HistoricalData query = response.body();
                    List<Quote> list = query.getQuery().getResults().getQuote();
                    setChart(list);
                } else {
                    setUnsuccessfulResponse(response);
                }
            }

            @Override
            public void onFailure(Call<HistoricalData> call, Throwable t) {
                Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUnsuccessfulResponse(Response response) {
        int statusCode = response.code();
        ResponseBody errorBody = response.errorBody();
        String text = getString(R.string.retrofit__error, statusCode, errorBody);
        Toast.makeText(mActivity, text, Toast.LENGTH_SHORT).show();
    }

    private void setChart(List<Quote> list) {
        // Gets values.
        int size = list.size();
        int unit = size / LIMIT;
        int index = 0;
        for (int i = index; index < LIMIT; i += unit) {
            mLabels[index] = list.get(i).getDate();
            mValues[index++] = (float)list.get(i).getHigh();
        }

        // Sets chart.
        LineChartView chart = (LineChartView)findViewById(R.id.linechart);

        LineSet dataset = new LineSet(reverseString(mLabels), reverse(mValues));
        dataset.setColor(ContextCompat.getColor(mActivity, R.color.blue_700))
            .setFill(ContextCompat.getColor(mActivity, R.color.blue_100))
            .setDotsColor(ContextCompat.getColor(mActivity, R.color.blue_500))
            .setThickness(4)
            .setDashed(new float[] { 10f, 10f });
        chart.addData(dataset);
        chart.show();
    }

    private String getTodayDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String[] reverseString(String[] strings) {
        String[] reversed = new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            reversed[i] = strings[strings.length - 1 - i];
        }
        return reversed;
    }

    private float[] reverse(float[] nums) {
        float[] reversed = new float[nums.length];
        for (int i = 0; i < nums.length; i++) {
            reversed[i] = nums[nums.length - 1 - i];
        }
        return reversed;
    }
}