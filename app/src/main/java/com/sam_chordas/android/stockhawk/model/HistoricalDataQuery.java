package com.sam_chordas.android.stockhawk.model;

/**
 * HistoricalData.java.
 *
 * @author Rodrigo Cericatto
 * @since Oct 21, 2016
 */
public class HistoricalDataQuery {

    //--------------------------------------------------
    // Attributes
    //--------------------------------------------------

    private int count;
    private String created;
    private String lang;
    private QuoteQuery results;

    //--------------------------------------------------
    // Constructor
    //--------------------------------------------------

    public HistoricalDataQuery(int count, String created, String lang, QuoteQuery results) {
        this.count = count;
        this.created = created;
        this.lang = lang;
        this.results = results;
    }

    //--------------------------------------------------
    // To String
    //--------------------------------------------------

    @Override
    public String toString() {
        return "HistoricalData{" +
            "count=" + count +
            ", created='" + created + '\'' +
            ", lang='" + lang + '\'' +
            ", results=" + results +
            '}';
    }

    //--------------------------------------------------
    // Getters
    //--------------------------------------------------

    public int getCount() {
        return count;
    }

    public String getCreated() {
        return created;
    }

    public String getLang() {
        return lang;
    }

    public QuoteQuery getResults() {
        return results;
    }
}