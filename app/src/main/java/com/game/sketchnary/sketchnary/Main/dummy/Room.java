package com.game.sketchnary.sketchnary.Main.dummy;

/**
 * Created by danny on 23/05/2016.
 */
public class Room {
    public final String roomNumber;
    public final String roomCategory;
    public final String currentPlayers;

    public Room(String roomNumber, String roomCategory, String currentPlayers) {
        this.roomNumber = roomNumber;
        this.roomCategory = roomCategory;
        this.currentPlayers = currentPlayers;
    }

    @Override
    public String toString() {
        return roomCategory;
    }
}