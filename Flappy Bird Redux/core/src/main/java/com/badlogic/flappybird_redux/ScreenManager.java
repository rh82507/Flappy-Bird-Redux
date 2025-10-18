package com.badlogic.flappybird_redux;

import com.badlogic.flappybird_redux.ref.Screens;

public class ScreenManager {
    private static Screens curScreen = Screens.Title;

    public static void setScreen(Screens s) {
        curScreen = s;
    }

    public static Screens currentScreen() {
        return curScreen;
    }
}
