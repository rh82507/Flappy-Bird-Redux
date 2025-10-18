package com.badlogic.flappybird_redux;

import com.badlogic.flappybird_redux.ref.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.*;
import jdk.internal.org.jline.utils.Display;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    // Custom Screens
    private Level level;
    private Title title;
    private Score score;
    private Options options;
    private Help help;
    // Variables
    public static Stage stage, stage2;
    public static InputMultiplexer im;
    public static Vector2 initScreenSize = new Vector2(1366, 768);
    public static OrthographicCamera cam;
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    public static FitViewport viewport;
    private int rTimer = 1, rTimerPrev = 0;
    // Game
    public FontBase fontBase;
    private static boolean paused = false;
    public static boolean playerPaused = false;
    public static boolean muted = false;
    public static Data data = new Data();
    public static boolean isScreenResizing = false;
    public static boolean showFPS = false;
    // Game Music
    public Music idleSong, mainSong;
    private float isInitVol = 0.7f, msInitVol = 0.7f;
    private float msInLevel = 0;

    @Override
    public void create() {
        Help.page = 0;
        // Init Camera
        IdleObstacle.scrollSpd = IdleObstacle.scrollSpdInit = 4;
        cam = new OrthographicCamera();
        cam.setToOrtho(false, 1366, 768);
        // Init Viewport
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), cam);
        viewport.setScreenBounds(0, 0, viewport.getScreenWidth(), viewport.getScreenHeight());
        stage = new Stage(viewport);
        stage2 = new Stage(viewport);
        im = new InputMultiplexer(stage, stage2);
        Gdx.input.setInputProcessor(im);

        muted = Main.data.muted;

        // Init SpriteBatch
        spriteBatch = new SpriteBatch();
        // Init ShapeRenderer
        shapeRenderer = new ShapeRenderer();

        // Initialize Screen
        level = new Level(spriteBatch, shapeRenderer);
        title = new Title(spriteBatch);
        score = new Score(spriteBatch);
        options = new Options(spriteBatch);
        help = new Help(spriteBatch, level);
        resize((int)(1280/1.3), (int)(720/1.3));
        // Set Screen
        setScreen(title);
        ScreenManager.setScreen(Screens.Title);
        getScreen().show();
        if (data.getTheme() != null && data.getTheme() == Theme.Night) level.setBackground("night");
        else level.setBackground("day");

        // Init Font
        fontBase = new FontBase();

        // Init Music
        idleSong = Gdx.audio.newMusic(Gdx.files.internal("music/IdleMusic.mp3"));
        mainSong = Gdx.audio.newMusic(Gdx.files.internal("music/MainMusic.mp3"));
    }

    @Override
    public void render() {
        super.render();

        // Set Theme
        if (!options.haveSetTheme) {
            if (options.theme == Theme.Day) {
                Level.background = "default";
                level.setBackground("default");
                title.setBackground();
                options.setBackground();
            }
            else if (options.theme == Theme.Night) {
                Level.background = "night";
                level.setBackground("night");
                title.setBackground();
                options.setBackground();
            }

            options.haveSetTheme = true;
        }

        if (!(getScreen() instanceof Level) && level.player.playerLockTime != 0) {
            level.lockedPlayerOnce = false;
            level.player.playerLockTime = 0;
        }
        if (getScreen() instanceof Level && Title.cBack.getPosition().x != 0) {
            Title.sky.setPosition(0, 0);
            Title.cBack.setPosition(0, -50);
            Title.cFront.setPosition(0, -70);
            Title.buildings.setPosition(0, -600);
            options.buildings.setPosition(0, -125);
            options.treeBack.setPosition(0, -125);
            options.treeMid.setPosition(0, -125);
            options.treeFront.setPosition(0, -125);
        }

        //Pause Screen
        if (getScreen() instanceof Level || Level.gameDebug) {
            if (((getScreen() instanceof Level && Level.initialUnpause) || !(getScreen() instanceof Level)) && (Gdx.input.isKeyJustPressed(Input.Keys.P) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))) {
                if (Level.gameDebug || (getScreen() instanceof Level && (level.player.state == PlayerState.NORMAL || Level.noPlayerMode)) || !(getScreen() instanceof Level)) {
                    Gdx.graphics.setForegroundFPS((!paused) ? 50 : 70);
                    level.pauseTimer = 0.3f;

                    if (paused) {
                        level.doCountDown = true;
                    }
                    else {
                        paused = true;
                        playerPaused = true;
                    }
                }
            }
        }
        if (getScreen() instanceof Level && level.player.state != PlayerState.DEAD && !Level.helpBtn.isOver()) Gdx.input.setCursorCatched(!paused);
        else if (Gdx.input.isCursorCatched()) Gdx.input.setCursorCatched(false);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        stage2.act(Gdx.graphics.getDeltaTime());
        stage2.draw();

        // Screen Management
        if (ScreenManager.currentScreen() == Screens.Title && !(getScreen() instanceof Title)) {
            setScreen(title);
        }
        else if (ScreenManager.currentScreen() == Screens.Level && !(getScreen() instanceof Level)) {
            setScreen(level);
        }
        else if (ScreenManager.currentScreen() == Screens.Score && !(getScreen() instanceof Score)) {
            setScreen(score);
        }
        else if (ScreenManager.currentScreen() == Screens.Options && !(getScreen() instanceof Options)) {
            setScreen(options);
        }
        else if (ScreenManager.currentScreen() == Screens.HelpMenu && !(getScreen() instanceof Help)) {
            setScreen(help);
            if (Data.load() == null) data.save();
        }

        //Render Screen
        getScreen().render(Gdx.graphics.getDeltaTime());
        cam.update();
        //Set Viewport
        viewport.apply();

        // Volume
        if (muted) {
            idleSong.setVolume(0);
            mainSong.setVolume(0);
        }
        else if (idleSong.getVolume() != data.getMusicVol()*isInitVol || mainSong.getVolume() != data.getMusicVol()*msInitVol) {
            idleSong.setVolume(data.getMusicVol()*isInitVol);
            mainSong.setVolume(data.getMusicVol()*msInitVol);
        }
        // Music Stuff
        if (!muted) {
            if (getScreen() instanceof Level && !Level.practiceParryMode) {
                if (!mainSong.isPlaying()) {
                    mainSong.play();
                    idleSong.stop();
                }

                if (isPaused() && Level.initialUnpause && mainSong.getVolume() != data.getMusicVol() * 0.25f * msInitVol) {
                    msInLevel = data.getMusicVol() * 0.25f;
                    mainSong.setVolume(msInitVol * msInLevel);
                }
                else if (!isPaused() && mainSong.getVolume() != data.getMusicVol() * msInitVol) {
                    msInLevel = data.getMusicVol();
                    mainSong.setVolume(Title.lerp(mainSong.getVolume(), msInLevel, 0.05f * Gdx.graphics.getDeltaTime() * 100));
                }
            }
            else if ((getScreen() instanceof Options || getScreen() instanceof Help || (getScreen() instanceof Level && Level.practiceParryMode)) && !idleSong.isPlaying()) {
                idleSong.play();
                mainSong.pause();
            }
            else if (!(getScreen() instanceof Options || getScreen() instanceof Help || (getScreen() instanceof Level && Level.practiceParryMode)) && !mainSong.isPlaying()) {
                idleSong.pause();
                mainSong.play();
            }
        }

        //Resize Screen
        if (rTimer != rTimerPrev) {
            isScreenResizing = true;
            rTimerPrev++;
        }
        else isScreenResizing = false;

        //SpriteBatch for Level
        if (getScreen() instanceof Level || showFPS) {
            spriteBatch.begin();
            if (getScreen() instanceof Level) {
                level.draw(spriteBatch);
                level.drawUI(spriteBatch);
            }
            if (showFPS) {
                FontBase.scoreFont.getData().setScale(0.2f);
                FontBase.scoreFont.draw(spriteBatch, String.valueOf(Gdx.graphics.getFramesPerSecond()), 20, 750);
            }
            spriteBatch.end();
        }

        //Shape Renderer
        if (Level.gameDebug) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            if (getScreen() instanceof Level && Level.gameDebug) level.debug();

            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    @Override
    public void pause() {
        super.pause();
        getScreen().pause();
        pauseGame(true);
    }
    @Override
    public void resume() {
        super.resume();
        getScreen().resume();
        pauseGame(false);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        level.dispose();
        title.dispose();
        score.dispose();
        stage.dispose();
        stage2.dispose();
        mainSong.dispose();
        idleSong.dispose();
    }

    @Override
    public void resize(int width, int height) {
        if (getScreen() != null) getScreen().resize(width, height);
        Gdx.graphics.setWindowedMode(width, height);
        viewport.update(width, height);
        stage.setViewport(viewport);
        stage2.setViewport(viewport);
        isScreenResizing = true;
        rTimer++;
    }

    // Misc
    public static void pauseGame(boolean state) {
        if (state) {
            paused = true;
        }
        else {
            if (!playerPaused) paused = false;
        }
    }
    public static boolean isPaused() {
        return paused;
    }
}

