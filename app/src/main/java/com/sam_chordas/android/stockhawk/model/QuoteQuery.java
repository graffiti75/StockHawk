package com.sam_chordas.android.stockhawk.model;

import java.util.List;

/**
 * HistoricalData.java.
 *
 * @author Rodrigo Cericatto
 * @since Oct 21, 2016
 */
public class QuoteQuery {

    //--------------------------------------------------
    // Attributes
    //--------------------------------------------------

    private List<Quote> quote;

    //--------------------------------------------------
    // Constructor
    //--------------------------------------------------

    public QuoteQuery(List<Quote> quote) {
        this.quote = quote;
    }

    //--------------------------------------------------
    // To String
    //--------------------------------------------------

    @Override
    public String toString() {
        return "QuoteQuery{" +
            "quote=" + quote +
            '}';
    }

    //--------------------------------------------------
    // Getters
    //--------------------------------------------------

    public List<Quote> getQuote() {
        return quote;
    }
}