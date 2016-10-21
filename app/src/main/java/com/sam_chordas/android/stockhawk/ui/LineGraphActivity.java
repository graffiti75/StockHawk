package com.sam_chordas.android.stockhawk.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;

/**
 * LineGraphActivity.java.
 *
 * @author Rodrigo Cericatto
 * @since Oct 21, 2016
 */
public class LineGraphActivity extends AppCompatActivity {

    //--------------------------------------------------
    // Attributes
    //--------------------------------------------------

    private final String[] mLabels = {"Jan", "Fev", "Mar", "Apr", "Jun", "May", "Jul", "Aug", "Sep"};

    private final float[][] mValues = {
        {3.5f, 4.7f, 4.3f, 8f, 6.5f, 9.9f, 7f, 8.3f, 7.0f},
        {4.5f, 2.5f, 2.5f, 9f, 4.5f, 9.5f, 5f, 8.3f, 1.8f}
    };

    //--------------------------------------------------
    // Activity Life Cycle
    //--------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        LineChartView chart = (LineChartView)findViewById(R.id.linechart);


        LineSet dataset = new LineSet(mLabels, mValues[0]);
        dataset.setColor(Color.parseColor("#758cbb"))
            .setFill(Color.parseColor("#2d374c"))
            .setDotsColor(Color.parseColor("#758cbb"))
            .setThickness(4)
            .setDashed(new float[] {10f, 10f})
            .beginAt(5);
        chart.addData(dataset);
        chart.show();
    }

    //--------------------------------------------------
    // Methods
    //--------------------------------------------------


}