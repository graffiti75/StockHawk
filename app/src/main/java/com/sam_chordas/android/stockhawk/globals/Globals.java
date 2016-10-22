package com.sam_chordas.android.stockhawk.globals;

/**
 * Globals.java.
 *
 * @author Rodrigo Cericatto
 * @since Oct 21, 2016
 */
public class Globals {

    //--------------------------------------------------
    // App Constants
    //--------------------------------------------------

    /**
     * Log Tag.
     */

    public static final String LOG_TAG = "StockHawk";

    /**
     * Http.
     */

    public static final String BASE_URL = "https://query.yahooapis.com/";

//    public static final String QUERY = "select * from yahoo.finance.historicaldata where symbol in " +
//        "(\"YHOO\") and startDate=\"2016-01-01\" and endDate=\"2016-10-20\"";
    public static final String FORMAT = "json";
    public static final String ENV = "store://datatables.org/alltableswithkeys";
}