package com.game.sketchnary.sketchnary.Main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.game.sketchnary.sketchnary.Main.FindGameFragment.OnListFragmentInteractionListener;
import com.game.sketchnary.sketchnary.Main.Room.Room;
import com.game.sketchnary.sketchnary.R;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Room} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyFindGameRecyclerViewAdapter extends RecyclerView.Adapter<MyFindGameRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<Room> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyFindGameRecyclerViewAdapter(ArrayList<Room> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_findgame, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mRoomNumberView.setText(mValues.get(position).roomNumber);
        holder.mCategoryView.setText(mValues.get(position).roomCategory);
        holder.mNumberPlayersView.setText(mValues.get(position).currentPlayers);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mRoomNumberView;
        public final TextView mCategoryView;
        public final TextView mNumberPlayersView;
        public Room mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mRoomNumberView = (TextView) view.findViewById(R.id.roomNumber);
            mCategoryView = (TextView) view.findViewById(R.id.roomCategory);
            mNumberPlayersView = (TextView) view.findViewById(R.id.currentPlayers);
        }


        @Override
        public String toString() {
            return super.toString() + " '" + mCategoryView.getText() + "'";
        }
    }
}
