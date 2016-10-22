package com.sam_chordas.android.stockhawk.model;

/**
 * HistoricalData.java.
 *
 * @author Rodrigo Cericatto
 * @since Oct 21, 2016
 */
public class HistoricalData {

    //--------------------------------------------------
    // Attributes
    //--------------------------------------------------

    private HistoricalDataQuery query;

    //--------------------------------------------------
    // Constructor
    //--------------------------------------------------

    public HistoricalData(HistoricalDataQuery query) {
        this.query = query;
    }

    //--------------------------------------------------
    // To String
    //--------------------------------------------------

    @Override
    public String toString() {
        return "HistoricalData{" +
            "query=" + query +
            '}';
    }

    //--------------------------------------------------
    // Getters
    //--------------------------------------------------

    public HistoricalDataQuery getQuery() {
        return query;
    }
}