package com.badlogic.flappybird_redux;

import com.badlogic.flappybird_redux.ref.FontBase;
import com.badlogic.flappybird_redux.ref.IdleObstacle;
import com.badlogic.flappybird_redux.ref.Screens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.ScreenUtils;

public class Title implements Screen {
    public static float lerp(float a, float b, float f) {
        return a * (1.0f - f) + (b * f);
    }
    // Components
    public SpriteBatch batch;
    ///     Logo
    private Texture titleTex;
    public Sprite titleLogo;
    private final float logoScale = 0.17f;
    private Vector2 titlePos = new Vector2();
    private float initY = 0f;
    ///     Buttons
    private Skin buttonSkin;
    private TextButton playBtn, scoreBtn, optionsBtn, exitBtn;
    private float btnGap = 105f;
    ///     Background
    public static Sprite sky;
    public static ParallaxSprite cFront, cBack, buildings;
    public static ParallaxSprite stars;

    public Title(SpriteBatch b) {
        batch = b;

        // Initialize Logo
        titleTex = new Texture(Gdx.files.internal("title/logo.png"));
        titleLogo = new Sprite(titleTex, 0, 0, titleTex.getWidth(), titleTex.getHeight());
        titleLogo.setOriginCenter();
        float w = titleTex.getWidth()*logoScale;
        float h = titleTex.getHeight()*logoScale;
        titlePos = new Vector2(Gdx.graphics.getWidth()/2f-w/8.5f, Gdx.graphics.getHeight()-h/2.9f);
        initY = Gdx.graphics.getHeight()-h/2.9f;
        titleLogo.setBounds(titlePos.x, titlePos.y, w, h);

        // Initialize Button
        buttonSkin = new Skin(Gdx.files.internal("skins/button_temp/buttonTemplate.json"));
        // Play
        playBtn = new TextButton("START", buttonSkin);
        playBtn.getLabel().setFontScale(1.65f);
        playBtn.setTransform(true);
        playBtn.setScale(0.125f);
        playBtn.setPosition(titlePos.x+titleLogo.getWidth()/4f, titlePos.y - btnGap);
        Main.stage.addActor(playBtn);
        // Score
        scoreBtn = new TextButton("SCORE", buttonSkin);
        scoreBtn.getLabel().setFontScale(1.65f);
        scoreBtn.setTransform(true);
        scoreBtn.setScale(0.125f);
        scoreBtn.setPosition(titlePos.x+titleLogo.getWidth()/4f, titlePos.y - btnGap*2f);
        Main.stage.addActor(scoreBtn);
        // Options
        optionsBtn = new TextButton("OPTIONS", buttonSkin);
        optionsBtn.getLabel().setFontScale(1.65f);
        optionsBtn.setTransform(true);
        optionsBtn.setScale(0.125f);
        optionsBtn.setPosition(titlePos.x+titleLogo.getWidth()/4f, titlePos.y - btnGap*3f);
        Main.stage.addActor(optionsBtn);
        // Exit
        exitBtn = new TextButton("QUIT", buttonSkin);
        exitBtn.getLabel().setFontScale(1.65f);
        exitBtn.setTransform(true);
        exitBtn.setScale(0.125f);
        exitBtn.setPosition(titlePos.x+titleLogo.getWidth()/4f, titlePos.y - btnGap*4f);
        Main.stage.addActor(exitBtn);

        // Background
        sky = new Sprite(Level.skyTex, 0, 0, Level.skyTex.getWidth(), Level.skyTex.getHeight());
        sky.setOrigin(0, 0);
        sky.setScale(0.35f);
        sky.setOriginBasedPosition(0, 0);
        cFront = new ParallaxSprite(Level.cloudFTex, 0, -70, 2.05f * 0.35f, 9);
        cBack = new ParallaxSprite(Level.cloudBTex, 0, -50, 2.05f * 0.35f, 13);
        buildings = new ParallaxSprite(Level.buildingTex, 0, -600, 2.05f * 0.35f * 2f, 7);
        stars = new ParallaxSprite(Level.starTex, 0, 0, 0.35f, 18);
    }

    @Override
    public void show() {
        IdleObstacle.scrollSpd = 4;
        //Unpause Game
        Main.playerPaused = false;
        Main.pauseGame(false);

        // Initialize Logo
        titleTex = new Texture(Gdx.files.internal("title/logo.png"));
        titleLogo = new Sprite(titleTex, 0, 0, titleTex.getWidth(), titleTex.getHeight());
        titleLogo.setOriginCenter();
        float w = titleTex.getWidth()*logoScale;
        float h = titleTex.getHeight()*logoScale;
        titlePos = new Vector2(Level.realSize.x/2f-w/8.5f - 150f, Level.realSize.y-h/2.9f - 150f);
        initY = Level.realSize.y-h/2.9f - 150f;
        titleLogo.setBounds(titlePos.x, titlePos.y, w, h);

        // Initialize Button
        buttonSkin = new Skin(Gdx.files.internal("skins/button_temp/buttonTemplate.json"));
        // Play
        playBtn = new TextButton("START", buttonSkin);
        playBtn.getLabel().setFontScale(1.65f);
        playBtn.setTransform(true);
        playBtn.setScale(0.125f);
        playBtn.setPosition(titlePos.x+titleLogo.getWidth()/4f + 11, titlePos.y - btnGap);
        Main.stage.addActor(playBtn);
        // Score
        scoreBtn = new TextButton("SCORE", buttonSkin);
        scoreBtn.getLabel().setFontScale(1.65f);
        scoreBtn.setTransform(true);
        scoreBtn.setScale(0.125f);
        scoreBtn.setPosition(playBtn.getX(), titlePos.y - btnGap*2f);
        Main.stage.addActor(scoreBtn);
        // Options
        optionsBtn = new TextButton("OPTIONS", buttonSkin);
        optionsBtn.getLabel().setFontScale(1.65f);
        optionsBtn.setTransform(true);
        optionsBtn.setScale(0.125f);
        optionsBtn.setPosition(playBtn.getX(), titlePos.y - btnGap*3f);
        Main.stage.addActor(optionsBtn);
        // Exit
        exitBtn = new TextButton("QUIT", buttonSkin);
        exitBtn.getLabel().setFontScale(1.65f);
        exitBtn.setTransform(true);
        exitBtn.setScale(0.125f);
        exitBtn.setPosition(playBtn.getX(), titlePos.y - btnGap*4f);
        Main.stage.addActor(exitBtn);
    }

    @Override
    public void render(float delta) {
        //ScreenUtils.clear(0.17f, 0.64f, 1f, 1f);
        ScreenUtils.clear(0f, 0, 0f, 1f);

        batch.begin();
        sky.draw(batch);
        if (Level.background.equalsIgnoreCase("night")) stars.draw(batch);
        cBack.draw(batch);
        cFront.draw(batch);
        buildings.draw(batch);

        titleLogo.draw(batch);
        if (!Main.isPaused() && !Main.isScreenResizing) {
            titleLogo.setPosition(titlePos.x, titlePos.y);
            titleMove(initY-20, initY+10f, 0.01f * 100 * Gdx.graphics.getDeltaTime(), 20);
        }

        playBtn.draw(batch, 1f);
        scoreBtn.draw(batch, 1f);
        optionsBtn.draw(batch, 1f);
        exitBtn.draw(batch, 1f);
        batch.end();

        // Button Logic
        if (playBtn.isChecked()) {
            if (Data.load() != null) ScreenManager.setScreen(Screens.Level);
            else {

                ScreenManager.setScreen(Screens.HelpMenu);
                Help.enteredFromOptions = false;
                Help.enteredFromPauseMenu = false;
            }
            playBtn.setChecked(false);
        }
        if (scoreBtn.isChecked() && ScreenManager.currentScreen() != Screens.Score) {
            ScreenManager.setScreen(Screens.Score);
            scoreBtn.setChecked(false);
        }
        if (optionsBtn.isChecked() && ScreenManager.currentScreen() != Screens.Options) {
            ScreenManager.setScreen(Screens.Options);
            optionsBtn.setChecked(false);
            Options.enteredFromTitle = true;
        }
        if (exitBtn.isChecked()) {
            Gdx.app.exit();
            exitBtn.setChecked(false);
        }
    }

    // Misc.
    private long counter = 0;
    private boolean goToMin = true;
    private float speed;
    private int stopTimer = 0;
    private void titleMove(float yMin, float yMax, float acc) {
        titlePos.set(titlePos.x, titlePos.y + speed * 100 * Gdx.graphics.getDeltaTime());

        float dif = yMax - yMin;

        if (goToMin) {
            if (titlePos.y > yMin+dif/2f) speed -= acc;
            else speed += acc;
        }
        else {
            if (titlePos.y < yMax-dif/2f) speed += acc;
            else speed -= acc;
        }

        if (goToMin && titlePos.y < yMin) {
            goToMin = false;
            speed = 0;
        }
        if (!goToMin && titlePos.y > yMax) {
            goToMin = true;
            speed = 0;
        }
    }
    private void titleMove(float yMin, float yMax, float acc, int secondsAtPoints) {
        //titlePos.set(titlePos.x, lerp(titlePos.y, (float)(Math.pow(Math.sin(counter++/10) + 1, 1.0/9) * 50f + 300f), 0.1f));
        titlePos.set(titlePos.x, titlePos.y + speed);

        //titlePos.y + speed * 100 * Gdx.graphics.getDeltaTime()

        float dif = yMax - yMin;

        if (stopTimer == 0) {
            if (goToMin) {
                if (titlePos.y > yMin + dif / 2.5f) speed -= acc;
                else speed += acc;
            }
            else {
                if (titlePos.y < yMax - dif / 2.5f) speed += acc;
                else speed -= acc;
            }
    }
        else speed = lerp(speed, 0, 0.2f * 100 * Gdx.graphics.getDeltaTime());

        if (goToMin && titlePos.y < yMin) {
            //speed = 0;
            stopTimer = secondsAtPoints;
            goToMin = false;
        }
        if (!goToMin && titlePos.y > yMax) {
            //speed = 0;
            stopTimer = secondsAtPoints;
            goToMin = true;
        }

        if (speed == 0) speed += acc * ((goToMin) ? -1 :1);

        if (stopTimer > 0) stopTimer = (int)(stopTimer - Gdx.graphics.getDeltaTime());
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
        stopTimer = 0;
        speed = 0;
        goToMin = true;
        buttonSkin.dispose();
        dispose();
    }

    @Override
    public void dispose() {
        titleTex.dispose();
        buttonSkin.dispose();
    }

    // Misc
    public void setBackground() {
        sky.setTexture(Level.skyTex);
        cFront.setTexture(Level.cloudFTex);
        cBack.setTexture(Level.cloudBTex);
        buildings.setTexture(Level.buildingTex);
    }
}
