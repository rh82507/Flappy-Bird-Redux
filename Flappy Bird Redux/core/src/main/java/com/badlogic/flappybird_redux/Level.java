package com.badlogic.flappybird_redux;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.flappybird_redux.ref.*;
import com.badlogic.flappybird_redux.obstacles.*;
import com.sun.java.swing.action.HelpMenu;
import elemental2.dom.Touch;

import java.awt.*;

public class Level implements Screen {
    public static boolean gameDebug = true;
    public static boolean noPlayerMode = false;
    public static boolean practiceParryMode = false;
    // Screen Size
    private Vector2 windowSize, oldWindowSize;
    public static Vector2 realSize = new Vector2(1280, 720);
    // Variables
    private boolean didScreenResize = false;
    private boolean initialized = false;
    public static boolean initialUnpause = false;
    public boolean lockedPlayerOnce = false;
    public static int score = 0;
    // Level Props
    public Player player;
    public Collision floor, roof;
    public Pillar[] pillars = new Pillar[5]; // max 425, min 75
    public float spaceBetweenPillars = 400f;
    public float playerPosX = 0;
    public Blimp blimp;
    // Blimp
    public float blimpYPos = 0;
    private float blimpSpawnRNG = 1, spawnReq = 0.57f;
    // Result Screen
    private Texture resultTex;
    public Sprite results;
    private boolean showResults = false;
    public TextButton restartBtn, titleBtn;
    public float resultTimer = 0.5f;
    // Pause Menu
    private boolean dontReset = false;
    private Texture darkTex;
    public Sprite dark;
    public TextButton resumeBtn, prBtn, optionsBtn, exitBtn;
    public static TextButton helpBtn;
    public float pauseTimer = 0;
    private float countDown = 3f;
    public boolean doCountDown = false;
    // Background Props
    public static String background = "default";
    public static Texture treeFrntTex, treeMidTex, treeBackTex;
    public ParallaxSprite treeFront, treeMiddle, treeBack;
    public static Texture skyTex, buildingTex, cloudFTex, cloudBTex;
    public Sprite sky;
    public ParallaxSprite buildings, cloudsFront, cloudsBack;
    ///     NIGHT - Background Special
    public static Texture starTex;
    public ParallaxSprite stars;

    public Level(SpriteBatch b, ShapeRenderer s) {
        //Initialize Level Props
        player = new Player(b, s);
        floor = new Collision(player, b, s, 0, -85, Gdx.graphics.getWidth(), 100);
        roof = new Collision(player, b, s, 0, 768-20, Gdx.graphics.getWidth(), 100);
        for (int i=0; i < pillars.length; i++) {
            pillars[i] = new Pillar(player, b, s, Gdx.graphics.getWidth()+spaceBetweenPillars*(i+1), 0, 100, (int)(Math.random() * 354) + 75f);
        }
        blimp = new Blimp(player, b, s, ((!practiceParryMode) ? Gdx.graphics.getWidth()+spaceBetweenPillars*6 : realSize.x + 200), 400, 70);
        // Result
        resultTex = new Texture(Gdx.files.internal("level/result.png"));
        results = new Sprite(resultTex, 0, 0, resultTex.getWidth(), resultTex.getHeight());
        results.setOrigin(0, 0);
        results.setScale(0.14f);
        results.setOriginBasedPosition(realSize.x/2f - results.getWidth()*0.13f/2f + 25f, realSize.y/2f - results.getHeight()*0.13f/2f + 20f);

        //Initialize Background Props
        setBackground(background);

        init();
    }

    @Override
    public void show() {
        ///     Initialize Button
        Skin buttonSkin = new Skin(Gdx.files.internal("skins/button_temp/buttonTemplate.json"));
        ///     Restart
        restartBtn = new TextButton("RESTART", buttonSkin);
        restartBtn.getLabel().setFontScale(1.65f);
        restartBtn.setTransform(true);
        restartBtn.setScale(0.125f);
        restartBtn.setPosition(results.getX() + 75f*1.5f, results.getY()+75f);
        Main.stage.addActor(restartBtn);
        ///     Back to Title
        titleBtn = new TextButton("RETURN TO TITLE", buttonSkin);
        titleBtn.getLabel().setFontScale(1.5f * 0.125f/0.175f, 1.5f);
        titleBtn.setTransform(true);
        titleBtn.setScale(0.175f, 0.125f);
        titleBtn.setPosition(results.getX() + 75f, results.getY()+75f*3f);
        Main.stage.addActor(titleBtn);
        // Pause Menu
        darkTex = new Texture(Gdx.files.internal("level/darkness.png"));
        dark = new Sprite(darkTex, 0, 0, darkTex.getWidth(), darkTex.getHeight());
        dark.setOrigin(0, 0);
        dark.setScale(0.73f);
        dark.setColor(1f, 1f, 1f, 0.5f);
        ///     Resume
        resumeBtn = new TextButton("RESUME", buttonSkin);
        resumeBtn.getLabel().setFontScale(1.65f);
        resumeBtn.getLabel().setTouchable(Touchable.disabled);
        resumeBtn.setTransform(true);
        resumeBtn.setScale(0.125f);
        resumeBtn.setPosition(1280/2f-76f, 720/2f+150f);
        Main.stage2.addActor(resumeBtn);
        ///     Pause Menu Restart
        prBtn = new TextButton("RESTART", buttonSkin);
        prBtn.getLabel().setFontScale(1.65f);
        prBtn.getLabel().setTouchable(Touchable.disabled);
        prBtn.setTransform(true);
        prBtn.setScale(0.125f);
        prBtn.setPosition(1280/2f-76f, resumeBtn.getY() - 120f);
        Main.stage2.addActor(prBtn);
        ///     Options
        optionsBtn = new TextButton("OPTIONS", buttonSkin);
        optionsBtn.getLabel().setFontScale(1.65f);
        optionsBtn.setTransform(true);
        optionsBtn.setScale(0.125f);
        optionsBtn.setPosition(1280/2f-76f, resumeBtn.getY() - 120f*2);
        Main.stage2.addActor(optionsBtn);
        ///     Exit
        exitBtn = new TextButton("EXIT", buttonSkin);
        exitBtn.getLabel().setFontScale(1.65f);
        exitBtn.setTransform(true);
        exitBtn.setScale(0.125f);
        exitBtn.setPosition(1280/2f-76f, resumeBtn.getY() - 120*3);
        Main.stage2.addActor(exitBtn);
        ///     How To Play
        helpBtn = new TextButton("HOW TO PLAY", buttonSkin);
        helpBtn.getLabel().setFontScale(1.35f);
        helpBtn.setTransform(true);
        helpBtn.setScale(0.12f);
        helpBtn.setPosition(568f, 560f);
        Main.stage2.addActor(helpBtn);

        //Initialize 2
        //resize((int)(1280/1.3), (int)(720/1.3));
        windowSize = oldWindowSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (!dontReset) init();

        // Pause Game
        Main.pauseGame(true);
        Main.playerPaused = true;

        dontReset = false;
    }

    @Override
    public void render(float delta) {
        for (int i=0; i < pillars.length; i++)
            pillars[i].updateOutOfLoop();

        // Change Blimp Spawn Point
        if (practiceParryMode && blimp.spawnX != realSize.x + 200) {
            blimp.spawnX = realSize.x + 200;
            blimp.setPosition(blimp.spawnX, blimp.spawnY);
        }
        else if (!practiceParryMode && blimp.spawnX != Gdx.graphics.getWidth()+spaceBetweenPillars*6) {
            blimp.spawnX = Gdx.graphics.getWidth()+spaceBetweenPillars*6;
            blimp.setPosition(blimp.spawnX, blimp.spawnY);
        }
    }
    //Update outside of Game Logic
    public void draw(SpriteBatch b) {
        // Outside of Game Logic
        if (oldWindowSize.equals(windowSize)) {
            didScreenResize = false;
            Main.pauseGame(false);
        }
        windowSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (!oldWindowSize.equals(windowSize)) {
            didScreenResize = true;
            Main.pauseGame(true);
            oldWindowSize.set(windowSize);
        }

        if (!initialized) {
            initialized = true;
        }
        update(b);
    }
    //UI Update Layer
    public void drawUI(SpriteBatch b) {
        //UI Layer
        if (player.state != PlayerState.DEAD && !practiceParryMode) {
            if (!Main.isPaused() || !initialUnpause) {
                FontBase.resultFont.getData().setScale(0.11f);
                FontBase.resultFont.setColor(1f, 1f, 1f, (!initialUnpause) ? 0.7f : 0.3f);
                FontBase.resultFont.draw(b, "Press 'P' or 'ESC' to Pause", 20, 30);
                FontBase.resultFont.setColor(1f, 1f, 1f, 1f);
            }

            if (!initialUnpause && initialized && !noPlayerMode) {
                helpBtn.draw(b, 1f);
            }

            if (!initialUnpause && helpBtn.isOver() && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && ScreenManager.currentScreen() != Screens.HelpMenu) {
                Help.page = 0;
                Help.enteredFromOptions = false;
                Main.pauseGame(false);
                Main.playerPaused = false;
                ScreenManager.setScreen(Screens.HelpMenu);
            }

            FontBase.scoreFont.setColor(1f, 1f, 1f, 1f);
            if (initialUnpause && !noPlayerMode)
                FontBase.display(String.valueOf(score), FontBase.scoreFont, b, 680f, 700f, 0.3f);
            else if (initialized && !noPlayerMode) FontBase.display("Jump to Start", FontBase.scoreFont, b, 680f, 700f, 0.3f);
            else FontBase.display("DISPLAY  MODE", FontBase.scoreFont, b, 680f, 700f, 0.3f, (!Main.isPaused()) ? 0.2f : 1f);
        }
        else if (player.state != PlayerState.DEAD) {
            FontBase.display("PRACTICE THE PARRY!", FontBase.scoreFont, b, 680f, 720f, 0.3f, (!Main.isPaused()) ? 0.4f : 1f);
            FontBase.resultFont.getData().setScale(0.23f);
            FontBase.resultFont.setColor(1f, 1f, 1f, (!Main.isPaused()) ? 0.4f : 1f);
            FontBase.resultFont.draw(b, "Dispatch the Blimps", 373f, 670f);
            FontBase.resultFont.getData().setScale(0.13f);
            FontBase.resultFont.draw(b, "(Exit through the Pause Menu\n when you get the hang of it.)", 423f, 90f);

            FontBase.resultFont.getData().setScale(0.11f);
            FontBase.resultFont.setColor(1f, 1f, 1f, (!initialUnpause) ? 0.7f : 0.3f);
            FontBase.resultFont.draw(b, "Press 'P' or 'ESC' to Pause", 20, 30);
            FontBase.resultFont.setColor(1f, 1f, 1f, 1f);

        }

        if (showResults) {
            results.draw(b);

            // New High Score
            if (score > Main.data.getScore().get(0)) {
                Main.data.addScore(score);
                Main.data.save();
            }

            FontBase.resultFont.getData().setScale(0.25f);
            FontBase.resultFont.draw(b, String.valueOf(score), results.getX() + 640f, results.getY() + 275);
            FontBase.resultFont.draw(b, String.valueOf(Main.data.getScore().get(0)), results.getX() + 640f, results.getY() + 125);

            titleBtn.draw(b, 1f);
            restartBtn.draw(b, 1f);

            if (practiceParryMode && !titleBtn.getText().equals("RETURN TO GUIDE")) titleBtn.setText("RETURN TO GUIDE");
            else if (!practiceParryMode && !titleBtn.getText().equals("RETURN TO TITLE")) titleBtn.setText("RETURN TO TITLE");

            if (resultTimer == 0) {
                if (!titleBtn.isTouchable()) {
                    titleBtn.setTouchable(Touchable.enabled);
                    resumeBtn.setTouchable(Touchable.enabled);
                }
                if ((titleBtn.isChecked() && ScreenManager.currentScreen() != Screens.HelpMenu && practiceParryMode) || (titleBtn.isChecked() && ScreenManager.currentScreen() != Screens.Title && !practiceParryMode)) {
                    reset();
                    if (!practiceParryMode) ScreenManager.setScreen(Screens.Title);
                    else {
                        Help.page = 1;
                        Main.pauseGame(false);
                        Main.playerPaused = false;
                        ScreenManager.setScreen(Screens.HelpMenu);
                    }
                    titleBtn.setChecked(false);
                    restartBtn.setChecked(false);
                    showResults = false;
                }
                // FIX THIS | SOMETIMES AUTOCLICKS
                if (restartBtn.isChecked()) {
                    reset();
                    titleBtn.setChecked(false);
                    restartBtn.setChecked(false);
                    showResults = false;
                }
            }

            if (resultTimer > 0.05f) resultTimer -= Gdx.graphics.getDeltaTime();
            else resultTimer = 0;
        }
        else if (resultTimer != 0.5f) resultTimer = 0.5f;
        else {
            titleBtn.setTouchable(Touchable.disabled);
            resumeBtn.setTouchable(Touchable.disabled);
        }

        if (Main.isPaused() && initialUnpause && initialized && lockedPlayerOnce && Main.playerPaused) {
            Help.enteredFromPauseMenu = true;
            if (player.getVelocity().y < 0) player.setVelocity(0, 0);

            dark.draw(b);
            if (!doCountDown) {
                if (!resumeBtn.isTouchable()) {
                    resumeBtn.setTouchable(Touchable.enabled);
                    prBtn.setTouchable(Touchable.enabled);
                    optionsBtn.setTouchable((!practiceParryMode) ? Touchable.enabled : Touchable.disabled);
                    exitBtn.setTouchable(Touchable.enabled);
                }

                resumeBtn.draw(b, 1f);
                prBtn.draw(b, 1f);
                optionsBtn.draw(b, (!practiceParryMode) ? 1f : 0.6f);
                exitBtn.draw(b, 1f);
            }
            else {
                resumeBtn.setTouchable(Touchable.disabled);
                prBtn.setTouchable(Touchable.disabled);
                optionsBtn.setTouchable(Touchable.disabled);
                exitBtn.setTouchable(Touchable.disabled);
            }

            // Button Logic
            if (pauseTimer == 0) {
                // Resume
                if(resumeBtn.isChecked()) {
                    Help.enteredFromPauseMenu = false;
                    doCountDown = true;
                    resumeBtn.setChecked(false);
                }
                else if (resumeBtn.isChecked()) resumeBtn.setChecked(false);

                // Restart
                if (prBtn.isOver() && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                    reset();
                    Gdx.graphics.setForegroundFPS(70);
                }

                // Options
                if (!practiceParryMode && optionsBtn.isOver() && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && ScreenManager.currentScreen() != Screens.Options) {
                    ScreenManager.setScreen(Screens.Options);
                    dontReset = true;
                    Options.enteredFromTitle = false;
                }

                // Exit
                if (exitBtn.isOver() && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && ((practiceParryMode && ScreenManager.currentScreen() != Screens.HelpMenu) || (!practiceParryMode && ScreenManager.currentScreen() != Screens.Title))) {
                    reset();
                    Help.enteredFromPauseMenu = false;
                    if (!practiceParryMode) ScreenManager.setScreen(Screens.Title);
                    else {
                        Help.page = 1;
                        ScreenManager.setScreen(Screens.HelpMenu);
                    }
                    Gdx.graphics.setForegroundFPS(70);
                    Main.pauseGame(false);
                    Main.playerPaused = false;
                }
            }

            if (pauseTimer > 0) pauseTimer -= Gdx.graphics.getDeltaTime();
            else pauseTimer = 0;

            // Unpause Countdown
            if (doCountDown) {
                FontBase.gameFont.getData().setScale(1f);
                FontBase.gameFont.draw(b, String.valueOf((int)Math.ceil(countDown)), realSize.x/2f, realSize.y/2f + 100f);

                if (countDown > 0.05f) countDown -= Gdx.graphics.getDeltaTime() * 1.1f;
                else countDown = 0;

                if (countDown == 0) {
                    doCountDown = false;
                    countDown = 3;
                    Gdx.graphics.setForegroundFPS(70);
                    Main.pauseGame(false);
                    Main.playerPaused = false;
                }
            }
        }
    }

    //Initial Game Logic
    private void init() {
        // Init Player
        player.init();

        // Init Level
        IdleObstacle.scrollSpd = 4.4f;
        IdleObstacle.scrollSpdInit = 4.4f;
        if (!setPNPonce) {
            setPillarsAndPlayer(IdleObstacle.scrollSpd, 4, 16);
            setPNPonce = true;
        }
        for (Pillar p : pillars) p.init();
        for (int i=0; i < pillars.length; i++) {
            pillars[i].setProperties((IdleObstacle.scrollSpd >= 0) ? (realSize.x+spaceBetweenPillars*(i+1)) : (-spaceBetweenPillars*(i+1)), pillars[i].getPosition().y, pillars[i].getScale().x, pillars[i].getScale().y);
        }
        //Init Player
        player.flipPlayer(IdleObstacle.scrollSpd < 0);
    }
    //Update inside of Game Logic
    private void update(SpriteBatch b) {
        //Background
        runBackground(background, b);

        if (!practiceParryMode) for (int i=0; i < pillars.length; i++) pillars[i].draw();
        //Player
        if (!noPlayerMode) player.update(Gdx.graphics.getDeltaTime());
        if (initialUnpause && !lockedPlayerOnce) {
            player.playerLockTime = 25;
            lockedPlayerOnce = true;
        }

        // Run Blimp
        if (blimp.getPosition().x < -blimp.getSize().x - 100f || (blimp.blimpDispatched && blimp.getPosition().y < -blimp.getSize().y - 100f)) {
            blimp.reset();
            player.invincibilityTimer = 0;
        }
        if (practiceParryMode || (score > 20 && blimpSpawnRNG >= spawnReq))
            blimp.update();

        //Start Game
        if (!helpBtn.isOver() && !initialUnpause && (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT))) {
            Main.pauseGame(false);
            Main.playerPaused = false;
            player.jump();
            initialUnpause = true;
        }

        // Practice Mode
        if (practiceParryMode && blimp.spawnX != realSize.x + 200) blimp.spawnX = realSize.x + 200;

        //Update Level
        if (!Main.isPaused()) {
            // All objects in level MUST update
            floor.update();
            roof.update();
            if (!practiceParryMode) for (int i=0; i < pillars.length; i++) pillars[i].update();

            // Pillar Logic
            for (int i=0; IdleObstacle.scrollSpd > 0 && i < pillars.length; i++) {
                if (pillars[i].getPosition().x <= -pillars[i].getScale().x-spaceBetweenPillars) {
                    pillars[i].setProperties(rightmostPillar(pillars).getPosition().x + spaceBetweenPillars, pillars[i].getPosition().y, pillars[i].getScale().x, (int)(Math.random() * 354) + 75f);
                }
            }
            for (int i=0; IdleObstacle.scrollSpd < 0 && i < pillars.length; i++) {
                if (pillars[i].getPosition().x >= Gdx.graphics.getWidth() + pillars[i].getScale().x + spaceBetweenPillars) {
                    pillars[i].setProperties(leftmostPillar(pillars).getPosition().x - spaceBetweenPillars, pillars[i].getPosition().y, pillars[i].getScale().x, (int)(Math.random() * 354) + 75f);
                }
            }

            // Blimp Logic
            blimpYPos = leftClosestPillarFromBlimp(pillars).getPosition().y +
                leftClosestPillarFromBlimp(pillars).getScale().y +
                leftClosestPillarFromBlimp(pillars).gap/2f;
            if (blimp.blimpDispatched && player.invincibilityTimer == 0) {
                player.invincibilityTimer = 0.4f;
            }
            if (!blimp.blimpDispatched && (player.state == PlayerState.NORMAL || noPlayerMode) && !practiceParryMode)
                blimp.setPosition(blimp.getPosition().x, Title.lerp(blimp.getPosition().y, blimpYPos, 0.02f * Gdx.graphics.getDeltaTime() * 100));

            // Score
            for (int i=0; !noPlayerMode && player.state == PlayerState.NORMAL && i < pillars.length; i++) {
                if (pillars[i].passed()) {
                    score++;
                    if (blimp.fullyDispatched) blimpSpawnRNG = (float)Math.random();
                    if (blimpSpawnRNG >= spawnReq) blimp.fullyDispatched = false;
                }
            }

            // Game End
            if (!noPlayerMode) {
                if (player.state == PlayerState.HURT && player.invincibilityTimer == 0) {
                    IdleObstacle.scrollSpd = 0;
                }
                if (player.state == PlayerState.DEAD) {
                    showResults = true;
                }
            }
        }
    }
    //Game Reset
    public void reset() {
        // Reset Pillars
        for (int i=0; i < pillars.length; i++) {
            pillars[i].setProperties((IdleObstacle.scrollSpd >= 0) ? (realSize.x+spaceBetweenPillars*(i+1)) : (-spaceBetweenPillars*(i+1)), pillars[i].getPosition().y, pillars[i].getScale().x, (int)(Math.random() * 354) + 75f);
        }

        // Reset Variables
        lockedPlayerOnce = false;
        initialized = false;

        // Reset Player
        player.reset();
        player.setPosition(playerPosX, player.getPosition().y);

        // Reset Pause
        Main.pauseGame(true);
        Main.playerPaused = true;
        initialUnpause = false;

        // Reset & Save com.badlogic.flappybird_redux.Score
        Main.data.addScore(score);
        Main.data.save();
        score = 0;

        // Reset Background
        cloudsBack.reset();
        cloudsFront.reset();
        buildings.reset();
        treeBack.reset();
        treeMiddle.reset();
        treeFront.reset();

        // Reset Scroll Speed
        IdleObstacle.scrollSpd = IdleObstacle.scrollSpdInit;

        // Reset Blimp
        blimp.reset();
    }
    //Draw Debug Layer
    public void debug() {
        player.debug();
        floor.debug();
        roof.debug();
        for (Pillar p : pillars) p.debug();
        blimp.debug();
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
        // Unpause game if paused
        Main.pauseGame(false);
        Main.playerPaused = false;
    }

    @Override
    public void dispose() {
        player.trash();
        for (Pillar pillar : pillars) pillar.dispose();
        skyTex.dispose();
        cloudBTex.dispose();
        cloudFTex.dispose();
        buildingTex.dispose();
        treeFrntTex.dispose();
        treeMidTex.dispose();
        treeBackTex.dispose();
        resultTex.dispose();
        blimp.dispose();
    }

    //Misc.
    private float clamp(float val, float min, float max) {
        if (val >= min && val <= max) return val;
        else if (val < min) return min;
        else return max;
    }
    int ti1 = 0, ti2 = 0;
    public Pillar rightmostPillar(Pillar[] p) {
        int ti1 = 0;

        for (int i=0; i < p.length; i++) {
            if (p[i].getPosition().x > p[ti1].getPosition().x) ti1 = i;
        }

        return p[ti1];
    }
    public Pillar leftmostPillar(Pillar[] p) {
        int ti2 = 0;

        for (int i=0; i < p.length; i++) {
            if (p[i].getPosition().x < p[ti2].getPosition().x) ti2 = i;
        }

        return p[ti2];
    }
    float pspeed = 0;
    boolean setPNPonce = false;
    public void setPillarsAndPlayer(float speed, float min, float max) {
        pspeed = Math.abs(speed);

        if (pspeed >= min)
            spaceBetweenPillars = spaceBetweenPillars * (pspeed / 4f) + (pspeed * 0.25f - 1f);
        else
            spaceBetweenPillars = spaceBetweenPillars * (min / 4f) + (min * 0.25f - 1f);


        if (pspeed <= max)
            playerPosX = player.getPosition().x - (speed*1.5f * (speed / 4 - 1));
        else
            playerPosX = player.getPosition().x - (50f * (50 / 4 - 1));
        player.setPosition(playerPosX, player.getPosition().y);
    }

    float bsize = 2.05f * 0.35f;
    public void setBackground(String skin) {
        if (skin.equalsIgnoreCase("default")) {
            //Initialize Background Props
            ///     Trees
            treeFrntTex = new Texture(Gdx.files.internal("level/default/trees_front.png"));
            treeFront = new ParallaxSprite(treeFrntTex, 0, 0, bsize, 1);
            //treeFront.nOffset = 380f;
            treeMidTex = new Texture(Gdx.files.internal("level/default/trees_middle.png"));
            treeMiddle = new ParallaxSprite(treeMidTex, 0, 0, bsize, 2);
            treeBackTex = new Texture(Gdx.files.internal("level/default/trees_back.png"));
            treeBack = new ParallaxSprite(treeBackTex, 0, 0, bsize, 3);
            ///     Sky
            skyTex = new Texture(Gdx.files.internal("level/default/sky.png"));
            sky = new Sprite(skyTex, 0, 0, skyTex.getWidth(), skyTex.getHeight());
            sky.setOrigin(0, 0);
            sky.setScale(0.35f);
            sky.setOriginBasedPosition(0, 0);
            ///     Buildings
            buildingTex = new Texture(Gdx.files.internal("level/default/buildings.png"));
            buildings = new ParallaxSprite(buildingTex, 0, 0, bsize, 3);
            ///     Clouds
            cloudFTex = new Texture(Gdx.files.internal("level/default/clouds_front.png"));
            cloudsFront = new ParallaxSprite(cloudFTex, 0, 0, bsize, 4);
            cloudBTex = new Texture(Gdx.files.internal("level/default/clouds_back.png"));
            cloudsBack = new ParallaxSprite(cloudBTex, 0, 0, bsize, 5);
            ///     Stars
            starTex = new Texture(Gdx.files.internal("level/night/stars.png"));
            stars = new ParallaxSprite(starTex, 0, 0, 0.35f, 48);

            // Pillars
            Pillar.topTex = new Texture(Gdx.files.internal("level/default/pillar_top.png"));
            Pillar.bottomTex = new Texture(Gdx.files.internal("level/default/pillar_bottom.png"));

            // Blimp
            Blimp.blimpTex = new Texture(Gdx.files.internal("level/default/blimp.png"));
            Blimp.rTex = new Texture(Gdx.files.internal("level/default/reticle.png"));

            // Player
            Texture birdSpritesheet = new Texture(Gdx.files.internal("player/bird_spritesheet.png"));
            TextureRegion[][] birdSheetRaw = TextureRegion.split(birdSpritesheet, 1604, 1246);
            int birdIndex = 0;
            for (int r=0; r < birdSheetRaw.length; r++) {
                for (int c=0; c < birdSheetRaw[r].length; c++) {
                    player.birdFrames[birdIndex++] = birdSheetRaw[r][c];
                }
            }
        }
        else if (skin.equalsIgnoreCase("night")) {
            //Initialize Background Props
            ///     Trees
            treeFrntTex = new Texture(Gdx.files.internal("level/night/trees_front.png"));
            treeFront = new ParallaxSprite(treeFrntTex, 0, 0, bsize, 1);
            //treeFront.nOffset = 380f;
            treeMidTex = new Texture(Gdx.files.internal("level/night/trees_middle.png"));
            treeMiddle = new ParallaxSprite(treeMidTex, 0, 0, bsize, 2);
            treeBackTex = new Texture(Gdx.files.internal("level/night/trees_back.png"));
            treeBack = new ParallaxSprite(treeBackTex, 0, 0, bsize, 3);
            ///     Sky
            skyTex = new Texture(Gdx.files.internal("level/night/sky.png"));
            sky = new Sprite(skyTex, 0, 0, skyTex.getWidth(), skyTex.getHeight());
            sky.setOrigin(0, 0);
            sky.setScale(0.35f);
            sky.setOriginBasedPosition(0, 0);
            ///     Buildings
            buildingTex = new Texture(Gdx.files.internal("level/night/buildings.png"));
            buildings = new ParallaxSprite(buildingTex, 0, 0, bsize, 3);
            ///     Clouds
            cloudFTex = new Texture(Gdx.files.internal("level/night/clouds_front.png"));
            cloudsFront = new ParallaxSprite(cloudFTex, 0, 0, bsize, 4);
            cloudBTex = new Texture(Gdx.files.internal("level/night/clouds_back.png"));
            cloudsBack = new ParallaxSprite(cloudBTex, 0, 0, bsize, 5);
            ///     Stars
            starTex = new Texture(Gdx.files.internal("level/night/stars.png"));
            stars = new ParallaxSprite(starTex, 0, 0, 0.35f, 48);

            // Pillars
            Pillar.topTex = new Texture(Gdx.files.internal("level/night/pillar_top.png"));
            Pillar.bottomTex = new Texture(Gdx.files.internal("level/night/pillar_bottom.png"));

            // Blimp
            Blimp.blimpTex = new Texture(Gdx.files.internal("level/night/blimp.png"));
            Blimp.rTex = new Texture(Gdx.files.internal("level/night/reticle.png"));

            // Player
            Texture birdSpritesheet = new Texture(Gdx.files.internal("player/bird_spritesheet-night.png"));
            TextureRegion[][] birdSheetRaw = TextureRegion.split(birdSpritesheet, 1604, 1246);
            int birdIndex = 0;
            for (int r=0; r < birdSheetRaw.length; r++) {
                for (int c=0; c < birdSheetRaw[r].length; c++) {
                    player.birdFrames[birdIndex++] = birdSheetRaw[r][c];
                }
            }
        }
    }
    public void runBackground(String skin, SpriteBatch b) {
        if (skin.equalsIgnoreCase("default")) {
            sky.draw(b);
            cloudsBack.draw(b);
            cloudsFront.draw(b);
            buildings.draw(b);
            treeBack.draw(b);
            treeMiddle.draw(b);
            treeFront.draw(b);
        }//
        else if (skin.equalsIgnoreCase("night")) {
            sky.draw(b);
            stars.draw(b);
            cloudsBack.draw(b);
            cloudsFront.draw(b);
            buildings.draw(b);
            treeBack.draw(b);
            treeMiddle.draw(b);
            treeFront.draw(b);
        }
    }

    public Pillar leftClosestPillarFromBlimp(Pillar[] p) {
        int tempIndex = 0;

        for (int i=4; i >= 0; i--) {
            float curDis = Math.abs(blimp.getPosition().x - p[tempIndex].getPosition().x);
            float newDis = Math.abs(blimp.getPosition().x - p[i].getPosition().x);

            if (p[i].getPosition().x < blimp.getPosition().x && newDis < curDis) {
                tempIndex = i;
            }
        }

        return p[tempIndex];
    }
}
