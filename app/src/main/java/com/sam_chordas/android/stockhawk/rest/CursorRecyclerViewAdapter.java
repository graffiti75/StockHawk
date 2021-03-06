package com.sam_chordas.android.stockhawk.rest;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;

import com.sam_chordas.android.stockhawk.R;

/**
 * Created by sam_chordas on 10/6/15.
 * Credit to skyfishjy gist:
 * https://gist.github.com/skyfishjy/443b7448f59be978bc59
 * for the CursorRecyclerViewApater.java code and idea.
 */
public abstract class CursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    //--------------------------------------------------
    // Attributes
    //--------------------------------------------------

    private Context mContext;
    private Cursor mCursor;
    private boolean mDataIsValid;
    private int mRowIdColumn;
    private DataSetObserver mDataSetObserver;

    //--------------------------------------------------
    // Constructor
    //--------------------------------------------------

    public CursorRecyclerViewAdapter(Context context, Cursor cursor) {
        mCursor = cursor;
        mDataIsValid = cursor != null;
        mRowIdColumn = mDataIsValid ? mCursor.getColumnIndex("_id") : -1;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (mDataIsValid) {
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
        mContext = context;
    }

    //--------------------------------------------------
    // Methods
    //--------------------------------------------------

    public Cursor getCursor() {
        return mCursor;
    }

    public abstract void onBindViewHolder(VH viewHolder, Cursor cursor);

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        final Cursor oldCursor = mCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (mCursor != null) {
            if (mDataSetObserver != null) {
                mCursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataIsValid = true;
            notifyDataSetChanged();
        } else {
            mRowIdColumn = -1;
            mDataIsValid = false;
            notifyDataSetChanged();
        }
        return oldCursor;
    }

    //--------------------------------------------------
    // RecyclerView.Adapter
    //--------------------------------------------------

    @Override
    public int getItemCount() {
        if (mDataIsValid && mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (mDataIsValid && mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor.getLong(mRowIdColumn);
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        if (!mDataIsValid) {
            throw new IllegalStateException(mContext.getString(R.string.cursor_adapter__data_not_valid));
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException(mContext.getString(R.string.cursor_adapter__can_not_move_cursor) + " " + position);
        }
        onBindViewHolder(viewHolder, mCursor);
    }

    //--------------------------------------------------
    // NotifyingDataSetObserver
    //--------------------------------------------------

    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mDataIsValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataIsValid = false;
            notifyDataSetChanged();
        }
    }
}