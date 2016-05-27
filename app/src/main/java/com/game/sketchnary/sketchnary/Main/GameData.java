package com.game.sketchnary.sketchnary.Main;

import java.util.ArrayList;

/**
 * Created by danny on 27/05/2016.
 */
public class GameData {

    private String role;
    private String host;
    private int port;
    private ArrayList<String> words; // it can be a String or a ArrayList<String>
    private String word;

    public GameData(String role,String host,int port, String word){
        this.role=role;
        this.host=host;
        this.port=port;
        this.word = word;
    }
    public GameData(String role, String host, int port, ArrayList<String> words){
        this.role=role;
        this.host=host;
        this.port=port;
        this.words = words;
    }

    public String getRole() {
        return role;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }


    public ArrayList<String> getWords() {
        return words;
    }

    public String getWord() {
        return word;
    }
}
