package com.game.sketchnary.sketchnary.Main.Room.Game;

/**
 * Created by danny on 28/05/2016.
 */
public class Player {
    private String email;
    private int points;

    public Player(String email,int points){
        this.email=email;
        this.points=points;
    }

    public String getEmail() {
        return email;
    }

    public int getPoints() {
        return points;
    }
}
