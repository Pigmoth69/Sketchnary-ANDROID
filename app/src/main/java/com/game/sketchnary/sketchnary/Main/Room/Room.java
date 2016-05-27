package com.game.sketchnary.sketchnary.Main.Room;

import java.util.ArrayList;

/**
 * Created by danny on 23/05/2016.
 */
public class Room {
    private String roomName;
    private ArrayList<String> currentPlayers;

    public Room(String roomName, ArrayList<String> currentPlayers) {
        this.roomName=roomName;
        this.currentPlayers = currentPlayers;
    }

    public String getRoomName() {
        return roomName;
    }

    public ArrayList<String> getCurrentPlayers() {
        return currentPlayers;
    }
}