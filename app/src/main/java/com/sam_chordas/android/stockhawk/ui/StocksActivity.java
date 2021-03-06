package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.melnykov.fab.FloatingActionButton;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.QuoteCursorAdapter;
import com.sam_chordas.android.stockhawk.rest.RecyclerViewItemClickListener;
import com.sam_chordas.android.stockhawk.rest.RestUtils;
import com.sam_chordas.android.stockhawk.service.StockIntentService;
import com.sam_chordas.android.stockhawk.service.StockTaskService;
import com.sam_chordas.android.stockhawk.touch_helper.SimpleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.List;

public class StocksActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //--------------------------------------------------
    // Constants
    //--------------------------------------------------

    private static final int CURSOR_LOADER_ID = 0;

    //--------------------------------------------------
    // Attributes
    //--------------------------------------------------

    /**
     * Context.
     */

    private StocksActivity mActivity = StocksActivity.this;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */

    private Intent mServiceIntent;
    private ItemTouchHelper mItemTouchHelper;
    private QuoteCursorAdapter mCursorAdapter;
    boolean mIsConnected;

    /**
     * Adapter data.
     */

    private List<String> mSymbolList = new ArrayList<>();

    //--------------------------------------------------
    // Activity Life Cycle
    //--------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocks);

        checkConnection();
        setServiceIntent(savedInstanceState);

        RecyclerView recyclerView = setRecyclerView();
        setFloatingActionButton(recyclerView);
        setItemTouchHelper(recyclerView);
        setPeriodicTask();
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }

    //--------------------------------------------------
    // Menu
    //--------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stocks, menu);
        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_change_units:
                // This is for changing stock changes from percent value to dollar value.
                RestUtils.sShowPercent = !RestUtils.sShowPercent;
                this.getContentResolver().notifyChange(QuoteProvider.Quotes.CONTENT_URI, null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //--------------------------------------------------
    // Methods
    //--------------------------------------------------

    private void checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
            mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        mIsConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void setServiceIntent(Bundle savedInstanceState) {
        // The intent service is for executing immediate pulls from the Yahoo API.
        // GCMTaskService can only schedule tasks, they cannot execute immediately.
        mServiceIntent = new Intent(this, StockIntentService.class);
        if (savedInstanceState == null) {
            // Run the initialize task service so that some stocks appear upon an empty database
            mServiceIntent.putExtra("tag", "init");
            if (mIsConnected) {
                startService(mServiceIntent);
            } else {
                networkToast();
            }
        }
    }

    private RecyclerView setRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        mCursorAdapter = new QuoteCursorAdapter(this, null);
        recyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this,
            new RecyclerViewItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    String symbol = mSymbolList.get(position);
                    Intent intent = new Intent(mActivity, LineGraphActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("symbol", symbol);
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            })
        );
        recyclerView.setAdapter(mCursorAdapter);
        return recyclerView;
    }

    private void setFloatingActionButton(RecyclerView recyclerView) {
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.attachToRecyclerView(recyclerView);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsConnected) {
                    setMaterialDialogBuilder().show();
                } else {
                    networkToast();
                }
            }
        });
    }

    private MaterialDialog.Builder setMaterialDialogBuilder() {
        return new MaterialDialog.Builder(mActivity).title(R.string.stocks_activity__symbol_search)
            .content(R.string.stocks_activity__content_test)
            .inputType(InputType.TYPE_CLASS_TEXT)
            .input(R.string.stocks_activity__input_hint, R.string.stocks_activity__input_prefill, new MaterialDialog.InputCallback() {
                @Override
                public void onInput(MaterialDialog dialog, CharSequence input) {
                    // On FAB click, receive user input. Make sure the stock doesn't already exist
                    // in the DB and proceed accordingly
                    Cursor cursor = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        new String[] { QuoteColumns.SYMBOL }, QuoteColumns.SYMBOL + "= ?",
                        new String[] { input.toString() }, null);
                    if (cursor.getCount() != 0) {
                        Toast toast = Toast.makeText(mActivity,
                            getString(R.string.stocks_activity__stock_already_saved), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, Gravity.CENTER, 0);
                        toast.show();
                        return;
                    } else {
                        // Add the stock to DB
                        mServiceIntent.putExtra("tag", "add");
                        mServiceIntent.putExtra("symbol", input.toString());
                        startService(mServiceIntent);
                    }
                }
            });
    }

    private void setItemTouchHelper(RecyclerView recyclerView) {
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mCursorAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void setPeriodicTask() {
        if (mIsConnected) {
            long period = 3600L;
            long flex = 10L;
            String periodicTag = "periodic";

            // Create a periodic task to pull stocks once every hour after the app has been opened.
            // This is so Widget data stays up to date.
            PeriodicTask periodicTask = new PeriodicTask.Builder()
                .setService(StockTaskService.class)
                .setPeriod(period)
                .setFlex(flex)
                .setTag(periodicTag)
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .setRequiresCharging(false)
                .build();
            // Schedule task with tag "periodic." This ensure that only the stocks present in the DB
            // are updated.
            GcmNetworkManager.getInstance(this).schedule(periodicTask);
        }
    }

    private void networkToast() {
        Toast.makeText(mActivity, getString(R.string.stocks_activity__network_toast), Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("deprecation")
    private void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getTitle());
    }

    //--------------------------------------------------
    // Callbacks
    //--------------------------------------------------

    public void addToSymbolList(String symbol) {
        mSymbolList.add(symbol);
    }

    //--------------------------------------------------
    // LoaderManager.LoaderCallbacks
    //--------------------------------------------------

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This narrows the return to only the stocks that are most current.
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
            new String[] { QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
            QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP },
            QuoteColumns.ISCURRENT + " = ?", new String[] { "1" }, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}