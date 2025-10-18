package com.badlogic.flappybird_redux;

import com.badlogic.flappybird_redux.ref.Theme;

import java.io.FileOutputStream;
import java.io.*;
import java.util.ArrayList;
import java.util.Locale;

public class Data implements Serializable {
    // Top 5 High Scores
    private ArrayList<Integer> highScores = new ArrayList<Integer>();
    // Settings
    public static boolean muted = false;
    private float musicVol = 1, sfxVol = 1;
    private Theme savedTheme;

    public Data() {
        //Defaults
        for (int i=0; i < 5; i++) highScores.add(0);
        sfxVol = musicVol = 1;
        muted = false;
        savedTheme = Theme.Day;

        //Check for Save Data
        if (load() != null) {
            checkVariable("highScores");
            highScores = load().highScores;

            checkVariable("musicVol");
            musicVol = load().musicVol;

            checkVariable("sfxVol");
            sfxVol = load().sfxVol;

            checkVariable("muted");
            muted = load().muted;

            checkVariable("savedTheme");
            savedTheme = load().savedTheme;
        }
    }
    public Data(int scoreNew) {
        this();

        for (int i=0; i < 5; i++) {
            if (scoreNew > highScores.get(i)) {
                highScores.add(i, scoreNew);
                break;
            }
        }
    }

    public void addScore(int val) {
        boolean addedSomething = false;

        for (int i=0; i < 5; i++) {
            if (val > highScores.get(i) && (i != 0 && val < highScores.get(i-1) || i == 0)) {
                highScores.add(i, val);
                addedSomething = true;
                break;
            }
        }

        if (addedSomething) highScores.remove(highScores.size()-1);
    }
    public ArrayList<Integer> getScore() {
        return highScores;
    }

    public void setVolumes(float m, float s) {
        musicVol = m;
        sfxVol = s;
    }
    public float getMusicVol() { return musicVol; }
    public float getSfxVol() { return sfxVol; }

    public void setTheme(Theme t) {
        savedTheme = t;
    }
    public Theme getTheme() { return savedTheme; }

    public void reset() {
        highScores = new ArrayList<Integer>();
        for (int i=0; i < 5; i++) highScores.add(0);
        musicVol = 1;
        sfxVol = 1;
        muted = false;
        savedTheme = Theme.Day;
    }
    public void save() {
        try (
            FileOutputStream fos = new FileOutputStream("Save.az");
            ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(this);
        }
        catch (IOException e) {}
    }
    public static Data load() {
        Data temp = null;

        try(
            FileInputStream fis = new FileInputStream("Save.az");
            ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            temp = (Data)ois.readObject();
        }
        catch(IOException | ClassNotFoundException e) { e.printStackTrace(); }

        return temp;
    }
    private static void checkVariable(String varName) {
        try { load().getClass().getField(varName); }
        catch (NoSuchFieldException e) {
            System.out.println("NO PREVIOUS DETECTIONS OF " + varName.toUpperCase());
        }
    }

    @Override
    public String toString() {
        String temp = "[";

        for (int i=0; i < highScores.size(); i++) {
            if (i == highScores.size()-1) temp += highScores.get(i) + "]";
            else temp += highScores.get(i) + ", ";
        }

        return temp;
    }
}
