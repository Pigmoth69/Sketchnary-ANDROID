package com.game.sketchnary.sketchnary.Main;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.game.sketchnary.sketchnary.Main.Room.RoomLobby;
import com.game.sketchnary.sketchnary.R;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import com.game.sketchnary.sketchnary.Main.Room.Room;
import com.game.sketchnary.sketchnary.R;

import java.util.ArrayList;

public class PlayerRoomListFragment extends ListFragment implements AdapterView.OnItemClickListener {
    OnHeadlineSelectedListener mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_findgame, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("Entrei no create!");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1);
        for(int i = 0; i < RoomLobby.players.size();i++)
            adapter.add("PLAYERS: "+MainMenuActivity.rooms.get(i).getCurrentPlayers().size()+"/10"+" ROOM: "+ RoomLobby.player.get(i).getRoomName());

        //ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), , android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mCallback.onArticleSelected(MainMenuActivity.rooms.get(position).getRoomName());

    }

    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void onArticleSelected(String roomName);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
}