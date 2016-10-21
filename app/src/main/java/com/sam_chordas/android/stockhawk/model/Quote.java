package com.sam_chordas.android.stockhawk.model;

/**
 * Quote.java.
 *
 * @author Rodrigo Cericatto
 * @since Oct 21, 2016
 */
public class Quote {

    //--------------------------------------------------
    // Attributes
    //--------------------------------------------------

    private String Symbol;
    private String Date;
    private double Open;
    private double High;
    private double Low;
    private double Close;
    private String Volume;
    private double Adj_Close;

    //--------------------------------------------------
    // Constructor
    //--------------------------------------------------

    public Quote() {}

    public Quote(String symbol, String date, double open, double high, double low, double close,
        String volume, double adj_Close) {
        Symbol = symbol;
        Date = date;
        Open = open;
        High = high;
        Low = low;
        Close = close;
        Volume = volume;
        Adj_Close = adj_Close;
    }

    //--------------------------------------------------
    // To String
    //--------------------------------------------------

    @Override
    public String toString() {
        return "Quote{" +
            "Symbol='" + Symbol + '\'' +
            ", Date='" + Date + '\'' +
            ", Open=" + Open +
            ", High=" + High +
            ", Low=" + Low +
            ", Close=" + Close +
            ", Volume='" + Volume + '\'' +
            ", Adj_Close=" + Adj_Close +
            '}';
    }

    //--------------------------------------------------
    // Getters
    //--------------------------------------------------

    public String getSymbol() {
        return Symbol;
    }

    public String getDate() {
        return Date;
    }

    public double getOpen() {
        return Open;
    }

    public double getHigh() {
        return High;
    }

    public double getLow() {
        return Low;
    }

    public double getClose() {
        return Close;
    }

    public String getVolume() {
        return Volume;
    }

    public double getAdjClose() {
        return Adj_Close;
    }
}