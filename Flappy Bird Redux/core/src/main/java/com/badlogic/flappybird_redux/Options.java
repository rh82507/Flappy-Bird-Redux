package com.badlogic.flappybird_redux;

import com.badlogic.flappybird_redux.ref.FontBase;
import com.badlogic.flappybird_redux.ref.IdleObstacle;
import com.badlogic.flappybird_redux.ref.Screens;
import com.badlogic.flappybird_redux.ref.Theme;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;

public class Options implements Screen {
    // Components
    private SpriteBatch batch;
    public static boolean enteredFromTitle = true;
    ///     Buttons
    private Skin buttonSkin;
    private TextButton muteBtn, back, fpsBtn, helpBtn;
    private float btnGap = 105f;
    ///     Background
    public static ParallaxSprite treeBack, treeMid, treeFront;
    public static ParallaxSprite buildings;
    ///     Audio
    public static float musicVolume = 1, sfxVolume = 1;
    private TextButton plusM, minusM, plusS, minusS;
    private Texture barT, darkT, lightT;
    private Sprite bar, darkSlide, lightSlide;
    private Sprite bar2, darkSlide2, lightSlide2;
    ///     Visual
    public boolean haveSetTheme = false;
    private Vector3 touchPoint = new Vector3();
    public Theme theme = Theme.Day;
    private Texture _day, _dayS, _night, _nightS;
    private Sprite day, dayS, night, nightS;

    public Options(SpriteBatch b) {
        batch = b;

        // Load Data
        musicVolume = Main.data.getMusicVol();
        sfxVolume = Main.data.getSfxVol();
        theme = Main.data.getTheme();

        // Bar
        barT = new Texture(Gdx.files.internal("title/bar.png"));
        bar = new Sprite(barT, 0, 0, barT.getWidth(), barT.getHeight());
        bar.setOrigin(0, 0);
        bar.setScale(0.1f);
        bar.setOriginBasedPosition(Level.realSize.x/2f - bar.getWidth()*0.1f/2f + 125f, Level.realSize.y-65f - btnGap);
        bar2 = new Sprite(barT, 0, 0, barT.getWidth(), barT.getHeight());
        bar2.setOrigin(0, 0);
        bar2.setScale(0.1f);
        bar2.setOriginBasedPosition(Level.realSize.x/2f - bar.getWidth()*0.1f/2f + 125f, Level.realSize.y-65f - btnGap*2f);
        // Dark
        darkT = new Texture(Gdx.files.internal("title/sliderDark.png"));
        darkSlide = new Sprite(darkT, 0, 0, darkT.getWidth(), darkT.getHeight());
        darkSlide.setOrigin(0, 0);
        darkSlide.setScale(bar.getScaleX());
        darkSlide.setOriginBasedPosition(bar.getX(), bar.getY());
        darkSlide2 = new Sprite(darkT, 0, 0, darkT.getWidth(), darkT.getHeight());
        darkSlide2.setOrigin(0, 0);
        darkSlide2.setScale(bar2.getScaleX());
        darkSlide2.setOriginBasedPosition(bar2.getX(), bar2.getY());
        // Light
        lightT = new Texture(Gdx.files.internal("title/sliderLight.png"));
        lightSlide = new Sprite(lightT, 0, 0, darkT.getWidth(), darkT.getHeight());
        lightSlide.setOrigin(0, 0);
        lightSlide.setScale(bar.getScaleX());
        lightSlide.setOriginBasedPosition(bar.getX(), bar.getY());
        lightSlide2 = new Sprite(lightT, 0, 0, darkT.getWidth(), darkT.getHeight());
        lightSlide2.setOrigin(0, 0);
        lightSlide2.setScale(bar2.getScaleX());
        lightSlide2.setOriginBasedPosition(bar2.getX(), bar2.getY());

        // Icons
        ///     Day
        _day = new Texture(Gdx.files.internal("theme_icons/day.png"));
        day = new Sprite(_day, 0, 0, _day.getWidth(), _day.getHeight());
        day.setOrigin(0, 0);
        day.setScale(0.5f);
        day.setOriginBasedPosition(80, 0);
        _dayS = new Texture(Gdx.files.internal("theme_icons/dayOutline.png"));
        dayS = new Sprite(_dayS, 0, 0, _dayS.getWidth(), _dayS.getHeight());
        dayS.setOrigin(0, 0);
        dayS.setScale(0.5f);
        dayS.setOriginBasedPosition(80, 0);
        ///     Night
        _night = new Texture(Gdx.files.internal("theme_icons/night.png"));
        night = new Sprite(_night, 0, 0, _night.getWidth(), _night.getHeight());
        night.setOrigin(0, 0);
        night.setScale(0.5f);
        night.setOriginBasedPosition(670, 0);
        _nightS = new Texture(Gdx.files.internal("theme_icons/nightOutline.png"));
        nightS = new Sprite(_nightS, 0, 0, _nightS.getWidth(), _nightS.getHeight());
        nightS.setOrigin(0, 0);
        nightS.setScale(0.5f);
        nightS.setOriginBasedPosition(670, 0);

        // Background
        buildings = new ParallaxSprite(Level.buildingTex, 0, -125, 2.05f * 0.35f * 1.7f, 13);
        treeBack = new ParallaxSprite(Level.treeBackTex, 0, -125, 2.05f * 0.35f * 1.7f, 10);
        treeMid = new ParallaxSprite(Level.treeMidTex, 0, -125, 2.05f * 0.35f * 1.7f, 7);
        treeFront = new ParallaxSprite(Level.treeFrntTex, 0, -125, 2.05f * 0.35f * 1.7f, 5);
    }

    @Override
    public void show() {
        Gdx.graphics.setForegroundFPS(70);
        IdleObstacle.scrollSpd = 4;
        //Unpause Game
        Main.pauseGame(false);
        Main.playerPaused = false;

        // Initialize Button
        buttonSkin = new Skin(Gdx.files.internal("skins/button_temp/buttonTemplate.json"));
        // Mute
        muteBtn = new TextButton((Main.muted) ? "UNMUTE" : "MUTE", buttonSkin);
        muteBtn.getLabel().setFontScale(1.65f);
        muteBtn.setTransform(true);
        muteBtn.setScale(0.11f);
        muteBtn.setPosition(Level.realSize.x - 150, Level.realSize.y - 50f);
        Main.stage.addActor(muteBtn);
        // Back
        back = new TextButton("BACK", buttonSkin);
        back.getLabel().setFontScale(1.65f);
        back.setTransform(true);
        back.setScale(0.11f);
        back.setPosition(30, Level.realSize.y - 50f);
        Main.stage.addActor(back);
        // FPS Button
        fpsBtn = new TextButton(Main.showFPS ? "ON" : "OFF", buttonSkin);
        fpsBtn.getLabel().setFontScale(1.65f * 1/0.5f, 1.65f);
        fpsBtn.setTransform(true);
        fpsBtn.setScale(0.11f * 0.5f, 0.11f);
        fpsBtn.setPosition(Level.realSize.x/2f + 165 - 300, Level.realSize.y/2f - 25f);
        Main.stage.addActor(fpsBtn);
        // How to Play
        helpBtn = new TextButton("HOW TO PLAY", buttonSkin);
        helpBtn.getLabel().setFontScale(1.35f);
        helpBtn.setTransform(true);
        helpBtn.setScale(0.12f);
        helpBtn.setPosition(Level.realSize.x/2f + 165 + 50, Level.realSize.y/2f - 25f);
        Main.stage.addActor(helpBtn);

        // Audio
        plusM = new TextButton("+", buttonSkin);
        plusM.getLabel().setFontScale(3f * 1/0.4f, 3f);
        plusM.getLabelCell().pad(0, 0, 170, 0);
        plusM.setTransform(true);
        plusM.setScale(0.125f * 0.4f, 0.125f);
        plusM.setPosition(bar.getX() + bar.getWidth()*0.1f + 25f, Level.realSize.y-85f - btnGap);
        Main.stage.addActor(plusM);

        minusM = new TextButton("-", buttonSkin);
        minusM.getLabel().setFontScale(3f * 1/0.25f, 3f);
        minusM.getLabelCell().pad(0, 0, 170, 0);
        minusM.setTransform(true);
        minusM.setScale(0.125f * 0.4f, 0.125f);
        minusM.setPosition(bar.getX() - minusM.getWidth() * 0.125f * 0.4f - 25f, Level.realSize.y-85f - btnGap);
        Main.stage.addActor(minusM);

        plusS = new TextButton("+", buttonSkin);
        plusS.getLabel().setFontScale(3f * 1/0.4f, 3f);
        plusS.getLabelCell().pad(0, 0, 170, 0);
        plusS.setTransform(true);
        plusS.setScale(0.125f * 0.4f, 0.125f);
        plusS.setPosition(bar2.getX() + bar2.getWidth()*0.1f + 25f, Level.realSize.y-85f - btnGap*2f);
        Main.stage.addActor(plusS);

        minusS = new TextButton("-", buttonSkin);
        minusS.getLabel().setFontScale(3f * 1/0.25f, 3f);
        minusS.getLabelCell().pad(0, 0, 170, 0);
        minusS.setTransform(true);
        minusS.setScale(0.125f * 0.4f, 0.125f);
        minusS.setPosition(bar.getX() - minusS.getWidth() * 0.125f * 0.4f - 25f, Level.realSize.y-85f - btnGap*2f);
        Main.stage.addActor(minusS);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        // Music Volume Logic
        if (lightSlide.getScaleX() != musicVolume*0.1f) {
            lightSlide.setScale(musicVolume * 0.1f, 0.1f);
        }
        if (plusM.isPressed()) {
            if (musicVolume < 1) musicVolume += 0.01f * 30 * Gdx.graphics.getDeltaTime();
            else musicVolume = 1;
        }
        if (minusM.isPressed()) {
            if (musicVolume > 0.01f) musicVolume -= 0.01f * 30 * Gdx.graphics.getDeltaTime();
            else musicVolume = 0;
        }

        // SFX Volume Logic
        if (lightSlide2.getScaleX() != sfxVolume*0.1f) {
            lightSlide2.setScale(sfxVolume * 0.1f, 0.1f);
        }
        if (plusS.isPressed()) {
            if (sfxVolume < 1) sfxVolume += 0.01f * 30 * Gdx.graphics.getDeltaTime();
            else sfxVolume = 1;
        }
        if (minusS.isPressed()) {
            if (sfxVolume > 0.01f) sfxVolume -= 0.01f * 30 * Gdx.graphics.getDeltaTime();
            else sfxVolume = 0;
        }

        // Save Volume Data
        if (!plusM.isPressed() && !minusM.isPressed() && !minusS.isPressed() && !plusS.isPressed()) {
            if (musicVolume != Main.data.getMusicVol() || sfxVolume != Main.data.getSfxVol()) {
                Main.data.setVolumes(musicVolume, sfxVolume);
                Main.data.save();
            }
        }

        // Theme Logic
        if (Gdx.input.justTouched()) {
            Main.cam.unproject(touchPoint.set(Gdx.input.getX(),Gdx.input.getY(),0));

            if (day.getBoundingRectangle().contains(touchPoint.x,touchPoint.y)) {
                theme = Theme.Day;
                Main.data.setTheme(theme);
                Main.data.save();
                haveSetTheme = false;
            }
            if (night.getBoundingRectangle().contains(touchPoint.x,touchPoint.y)) {
                theme = Theme.Night;
                Main.data.setTheme(theme);
                Main.data.save();
                haveSetTheme = false;
            }
        }

        // Mute Logic
        if (muteBtn.isChecked()) {
            Main.muted = !Main.muted;
            muteBtn.getLabel().setText((Main.muted) ? "UNMUTE" : "MUTE");
            muteBtn.setChecked(false);
            Main.data.muted = Main.muted;
            Main.data.save();
        }

        // Back Logic
        if (back.isChecked()) {
            if (enteredFromTitle) ScreenManager.setScreen(Screens.Title);
            else ScreenManager.setScreen(Screens.Level);
            back.setChecked(false);
        }

        // FPS Logic
        if (fpsBtn.isChecked()) {
            Main.showFPS = !Main.showFPS;
            fpsBtn.getLabel().setText(Main.showFPS ? "ON" : "OFF");
            fpsBtn.setChecked(false);
        }

        // Help Logic
        if (helpBtn.isChecked()) {
            Help.enteredFromOptions = true;
            ScreenManager.setScreen(Screens.HelpMenu);
            helpBtn.setChecked(false);
        }

        batch.begin();
        Title.sky.draw(batch);
        buildings.draw(batch);
        treeBack.draw(batch);
        treeMid.draw(batch);
        treeFront.draw(batch);

        muteBtn.draw(batch, 1f);
        back.draw(batch, 1f);

        FontBase.resultFont.getData().setScale(0.23f);
        FontBase.resultFont.draw(batch, "Show FPS: ", fpsBtn.getX() - 350f, fpsBtn.getY() + 60f);
        fpsBtn.draw(batch, 1f);

        helpBtn.draw(batch, 1f);

        FontBase.display("OPTIONS", FontBase.resultFont, batch, Level.realSize.x/2f + 40, Level.realSize.y - 40f, 0.4f);

        FontBase.resultFont.getData().setScale(0.23f);
        FontBase.resultFont.draw(batch, "Music: ", minusM.getX() - 175f - 50f, bar.getY() + 45f);
        plusM.draw(batch, 1f);
        minusM.draw(batch, 1f);
        darkSlide.draw(batch);
        lightSlide.draw(batch);
        bar.draw(batch);

        FontBase.resultFont.draw(batch, "SFX: ", minusS.getX() - 175f - 50f, bar2.getY() + 45f);
        plusS.draw(batch, 1f);
        minusS.draw(batch, 1f);
        darkSlide2.draw(batch);
        lightSlide2.draw(batch);
        bar2.draw(batch);

        if (theme == Theme.Night) {
            day.draw(batch);
            nightS.draw(batch);
        }
        else {
            dayS.draw(batch);
            night.draw(batch);
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        Gdx.graphics.setWindowedMode(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        barT.dispose();
        darkT.dispose();
        lightT.dispose();
        _day.dispose();
        _dayS.dispose();
        _night.dispose();
        _nightS.dispose();
        buttonSkin.dispose();
    }

    //Misc
    public void setBackground() {
        treeBack.setTexture(Level.treeBackTex);
        treeMid.setTexture(Level.treeMidTex);
        treeFront.setTexture(Level.treeFrntTex);
        buildings.setTexture(Level.buildingTex);
    }
}
