package com.sam_chordas.android.stockhawk.rest;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.touch_helper.ItemTouchHelperAdapter;
import com.sam_chordas.android.stockhawk.touch_helper.ItemTouchHelperViewHolder;
import com.sam_chordas.android.stockhawk.ui.StocksActivity;

/**
 * Created by sam_chordas on 10/6/15.
 * Credit to skyfishjy gist:
 * https://gist.github.com/skyfishjy/443b7448f59be978bc59
 * for the code structure
 */
public class QuoteCursorAdapter extends CursorRecyclerViewAdapter<QuoteCursorAdapter.ViewHolder>
    implements ItemTouchHelperAdapter {

    //--------------------------------------------------
    // Attributes
    //--------------------------------------------------

    private static Context mContext;
    private static Typeface mRobotoLight;

    //--------------------------------------------------
    // Constructor
    //--------------------------------------------------

    public QuoteCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    //--------------------------------------------------
    // RecyclerView.Adapter
    //--------------------------------------------------

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mRobotoLight = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_quote, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    //--------------------------------------------------
    // CursorRecyclerViewAdapter
    //--------------------------------------------------

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final Cursor cursor) {
        // Activity.
        StocksActivity activity = (StocksActivity)mContext;

        // Sets symbol.
        String symbol = cursor.getString(cursor.getColumnIndex("symbol"));
        activity.addToSymbolList(symbol);
        viewHolder.symbolTextView.setText(symbol);

        // Sets bid price.
        viewHolder.bidPriceTextView.setText(cursor.getString(cursor.getColumnIndex("bid_price")));

        // Sets change.
        setChangeTextView(viewHolder, cursor);
    }

    //--------------------------------------------------
    // Methods
    //--------------------------------------------------

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    private void setChangeTextView(ViewHolder viewHolder, Cursor cursor) {
        // Sets change.
        TextView changeTextView = viewHolder.changeTextView;
        Drawable drawable = mContext.getResources().getDrawable(R.drawable.percent_change_pill_green);
        if (cursor.getInt(cursor.getColumnIndex("is_up")) == 1) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                drawable = mContext.getResources().getDrawable(R.drawable.percent_change_pill_green);
                changeTextView.setBackgroundDrawable(drawable);
            } else {
                changeTextView.setBackground(drawable);
            }
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                drawable = mContext.getResources().getDrawable(R.drawable.percent_change_pill_red);
                changeTextView.setBackgroundDrawable(drawable);
            } else {
                drawable = mContext.getResources().getDrawable(R.drawable.percent_change_pill_red);
                changeTextView.setBackground(drawable);
            }
        }

        // Sets percent.
        if (RestUtils.sShowPercent) {
            viewHolder.changeTextView.setText(cursor.getString(cursor.getColumnIndex("percent_change")));
        } else {
            viewHolder.changeTextView.setText(cursor.getString(cursor.getColumnIndex("change")));
        }
    }

    //--------------------------------------------------
    // ItemTouchHelperAdapter
    //--------------------------------------------------

    @Override
    public void onItemDismiss(int position) {
        Cursor c = getCursor();
        c.moveToPosition(position);
        String symbol = c.getString(c.getColumnIndex(QuoteColumns.SYMBOL));
        mContext.getContentResolver().delete(QuoteProvider.Quotes.withSymbol(symbol), null, null);
        notifyItemRemoved(position);
    }

    //--------------------------------------------------
    // View Holder
    //--------------------------------------------------

    public static class ViewHolder extends RecyclerView.ViewHolder implements
        ItemTouchHelperViewHolder {

        public final TextView symbolTextView;
        public final TextView bidPriceTextView;
        public final TextView changeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            symbolTextView = (TextView) itemView.findViewById(R.id.stock_symbol);
            symbolTextView.setTypeface(mRobotoLight);
            bidPriceTextView = (TextView) itemView.findViewById(R.id.bid_price);
            changeTextView = (TextView) itemView.findViewById(R.id.change);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}