package com.sam_chordas.android.stockhawk.service;

import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.globals.Globals;
import com.sam_chordas.android.stockhawk.rest.RestUtils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by sam_chordas on 9/30/15.
 * The GCMTask service is primarily for periodic tasks. However, OnRunTask can be called directly
 * and is used for the initialization and adding task as well.
 */
public class StockTaskService extends GcmTaskService {

    //--------------------------------------------------
    // Constants
    //--------------------------------------------------

    /**
     * Http constants.
     */

    private static final String V1_URL = "v1/public/yql?q=";

    private static final String POSFIX = "&format=" + Globals.FORMAT + "&diagnostics=true" +
        "&env=" + Globals.ENV + "&callback=";

    private static final String QUERY_PREFIX = "select * from yahoo.finance.quotes where symbol in (";

    private static final String STOCK_COMPANIES = "\"YHOO\",\"AAPL\",\"GOOG\",\"MSFT\")";

    //--------------------------------------------------
    // Attributes
    //--------------------------------------------------

    private OkHttpClient mClient = new OkHttpClient();
    private Context mContext;
    private StringBuilder mStoredSymbols = new StringBuilder();
    private boolean mIsUpdate;

    //--------------------------------------------------
    // Constructor
    //--------------------------------------------------

    public StockTaskService() {}

    public StockTaskService(Context context) {
        mContext = context;
    }

    //--------------------------------------------------
    // Methods
    //--------------------------------------------------

    private String fetchData(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }

    private StringBuilder setCompleteUrl(StringBuilder url) {
        Cursor cursor;
        mIsUpdate = true;
        cursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
            new String[] { "Distinct " + QuoteColumns.SYMBOL }, null, null, null);
        if (cursor.getCount() == 0 || cursor == null) {
            // Init task. Populates DB with quotes for the symbols seen below
            try {
                url.append(URLEncoder.encode(STOCK_COMPANIES, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (cursor != null) {
            DatabaseUtils.dumpCursor(cursor);
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                mStoredSymbols.append("\"" + cursor.getString(cursor.getColumnIndex("symbol")) + "\",");
                cursor.moveToNext();
            }
            mStoredSymbols.replace(mStoredSymbols.length() - 1, mStoredSymbols.length(), ")");
            try {
                url.append(URLEncoder.encode(mStoredSymbols.toString(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return url;
    }

    private int setResult(StringBuilder url) {
        String urlString;
        String response;
        int result = GcmNetworkManager.RESULT_FAILURE;
        if (url != null) {
            urlString = url.toString();
            try {
                response = fetchData(urlString);
                result = GcmNetworkManager.RESULT_SUCCESS;
                try {
                    ContentValues contentValues = new ContentValues();
                    // Update ISCURRENT to 0 (false) so new data is current.
                    if (mIsUpdate) {
                        contentValues.put(QuoteColumns.ISCURRENT, 0);
                        mContext.getContentResolver().update(QuoteProvider.Quotes.CONTENT_URI, contentValues, null, null);
                    }
                    mContext.getContentResolver().applyBatch(QuoteProvider.AUTHORITY,
                        RestUtils.quoteJsonToContentVals(mContext, response));
                } catch (RemoteException | OperationApplicationException e) {
                    Log.e(Globals.LOG_TAG, mContext.getString(R.string.stock_task_service__error_batch), e);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    //--------------------------------------------------
    // GcmTaskService
    //--------------------------------------------------

    @Override
    public int onRunTask(TaskParams params) {
        // Parameters.
        StringBuilder url = new StringBuilder();
        if (mContext == null) {
            mContext = this;
        }

        // Sets the url.
        try {
            // Base URL for the Yahoo query.
            url.append(Globals.BASE_URL + V1_URL);
            url.append(URLEncoder.encode(QUERY_PREFIX, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (params.getTag().equals("init") || params.getTag().equals("periodic")) {
            setCompleteUrl(url);
        } else if (params.getTag().equals("add")) {
            mIsUpdate = false;
            // Gets symbol from params.getExtra and build query.
            String stockInput = params.getExtras().getString("symbol");
            try {
                url.append(URLEncoder.encode("\"" + stockInput + "\")", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        // Finalize the URL for the API query.
        url.append(POSFIX);
        int result = setResult(url);
        return result;
    }
}