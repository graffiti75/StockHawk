package com.sam_chordas.android.stockhawk.model;

import java.util.List;

/**
 * Query.java.
 *
 * @author Rodrigo Cericatto
 * @since Oct 21, 2016
 */
public class Query {

    //--------------------------------------------------
    // Attributes
    //--------------------------------------------------

    private int count;
    private String created;
    private String lang;
    private List<Quote> results;

    //--------------------------------------------------
    // Constructor
    //--------------------------------------------------

    public Query(int count, String created, String lang, List<Quote> results) {
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
        return "Query{" +
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

    public List<Quote> getResults() {
        return results;
    }
}