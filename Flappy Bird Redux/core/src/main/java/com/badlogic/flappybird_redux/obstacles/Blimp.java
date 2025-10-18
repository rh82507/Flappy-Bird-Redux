package com.badlogic.flappybird_redux.obstacles;

import com.badlogic.flappybird_redux.Level;
import com.badlogic.flappybird_redux.Main;
import com.badlogic.flappybird_redux.Options;
import com.badlogic.flappybird_redux.ref.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import jdk.jfr.internal.PlatformEventType;

public class Blimp extends Obstacle {
    // Components
    public static Texture blimpTex, rTex;
    private Sprite blimpSpr, reticle;
    private ParryBox parryBox;
    private Music reticleSfx;
    // Variables
    public float angle = 0;
    public boolean blimpDispatched = false, fullyDispatched = false;
    private float yspeed = 0;
    private float parryArea = 260;
    private float htwRatio = 0;
    private float blimpScale;
    private float parryTimer = 0.2f, ptInit = 0.2f;
    public float spawnX, spawnY;
    private float yBefore = 0, yAfter = 0;
    private boolean blimpGoingUp = false, blimpStraight = false;
    private boolean playedReticleSfx = false;
    private float reticleVolInit = 0.52f;

    public Blimp(Player p, SpriteBatch b, ShapeRenderer s, float x, float y, float w, float h) {
        super(p, b, s, x, y, w * 1.4f, h * 1.4f);
        blimpScale = w;
        parryBox = new ParryBox(p, b, s, x - parryArea/2f, y - parryArea/2f, w + parryArea, h + parryArea);
        spawnY = y;
        spawnX = x;
        init();
    }
    public Blimp(Player p, SpriteBatch b, ShapeRenderer s, float x, float y, float scale) {
        super(p, b, s, x, y, scale * 1.4f, scale * 1.4f);
        blimpScale = scale;
        parryBox = new ParryBox(p, b, s, x - parryArea/2f, y, scale + parryArea, scale + parryArea);
        spawnY = y;
        spawnX = x;
        init();
    }
    public Blimp(Player p, SpriteBatch b, ShapeRenderer s, float x, float y) {
        this(p, b, s, x, y, 500, 500);
    }
    public Blimp(Player p, SpriteBatch b, ShapeRenderer s) {
        this(p, b, s, 0, 0);
    }

    public void init() {
        if (Level.background.equalsIgnoreCase("default")) {
            blimpTex = new Texture(Gdx.files.internal("level/default/blimp.png"));
            rTex = new Texture(Gdx.files.internal("level/default/reticle.png"));
        }
        else {
            blimpTex = new Texture(Gdx.files.internal("level/night/blimp.png"));
            rTex = new Texture(Gdx.files.internal("level/night/reticle.png"));
        }

        // Set Hurtbox Scale
        htwRatio = (float)blimpTex.getHeight() /blimpTex.getWidth();
        height = width * blimpTex.getHeight()/blimpTex.getWidth();
        parryBox.setProperties(parryBox.getPosition().x, parryBox.getPosition().y - parryBox.getSize().y/2.75f * htwRatio - 20, parryBox.getSize().x * 1.1f, parryBox.getSize().y * blimpTex.getHeight()/blimpTex.getWidth() * 1.2f);

        // Set Sprite
        blimpSpr = new Sprite(blimpTex, 0, 0, blimpTex.getWidth(), blimpTex.getHeight());
        blimpSpr.setOriginCenter();
        blimpSpr.setScale(blimpScale / 70 * 0.1f);
        //blimpSpr.setSize(blimpScale * 2.5f, blimpScale* blimpTex.getHeight()/blimpTex.getWidth() * 2.5f);
        blimpSpr.setOriginBasedPosition(x + 50f, y + 25f);

        reticle = new Sprite(rTex, 0, 0, rTex.getWidth(), rTex.getHeight());
        reticle.setOriginCenter();
        reticle.setScale(0.075f);
        reticle.setColor(1f, 1f, 1f, 0.8f);
        reticle.setOriginBasedPosition(x + 52.5f, y + 25f);

        // Stun Hitbox
        stunHitbox = true;

        reticleSfx = Gdx.audio.newMusic(Gdx.files.internal("level/reticle.mp3"));
        reticleSfx.setLooping(false);
    }
    @Override
    public void update() {
        super.update();

        // Check Blimp Direction
        if (player.state == PlayerState.NORMAL || Level.noPlayerMode) {
            yAfter = y;
            blimpStraight = Math.abs(yAfter - yBefore) < 2f;
            blimpGoingUp = yAfter - yBefore > 0;
            yBefore = y;
        }

        // Set Textures
        if (!blimpSpr.getTexture().equals(blimpTex)) {
            blimpSpr.setTexture(blimpTex);
            reticle.setTexture(rTex);
        }

        // Draw & Update Stuff
        blimpSpr.draw(batch);
        parryBox.update();
        if (parryBox.withinTrigger() && player.state == PlayerState.NORMAL && !blimpDispatched) {
            if (!reticleSfx.isPlaying() && !playedReticleSfx) {
                reticleSfx.play();
                playedReticleSfx = true;
            }
            reticle.draw(batch);
        }
        else playedReticleSfx = false;

        if (Main.muted && reticleSfx.getVolume() != 0) reticleSfx.setVolume(0);
        else if (!Main.muted && reticleSfx.getVolume() != reticleVolInit * Options.sfxVolume) {
            reticleSfx.setVolume(reticleVolInit * Options.sfxVolume);
        }

        // Parry Logic
        ///     Parry Timer
        if (parryBox.withinTrigger()) {
            if (parryTimer > 0) parryTimer -= Gdx.graphics.getDeltaTime() * ((Math.abs(player.getVelocity().y) >= 11) ? (Math.abs(player.getVelocity().y) / 5) : 1f);
            stunHitbox = (parryTimer > 0);
        }
        ///     Hitbox Stun
        if (parryBox.didParry() && player.state == PlayerState.NORMAL) {
            player.parrySfx.play();
            blimpDispatched = true;
            parried = true;
            parryBox.parried = false;
        }
        if (!parryBox.withinTrigger() && parryTimer != ptInit) {
            parryBox.parried = false;
            stunHitbox = true;
            parryTimer = ptInit;
        }

        if (!Main.isPaused()) {
            if (blimpDispatched && player.state == PlayerState.NORMAL) dispatch();
            else if (player.state == PlayerState.NORMAL || Level.noPlayerMode) {
                // Move Blimp Left
                setPosition(x - IdleObstacle.scrollSpd * 2, y);

                // Set Rotation
                blimpSpr.setRotation(Player.lerpAngle(blimpSpr.getRotation(), angle, 0.05f * Gdx.graphics.getDeltaTime() * 100));
                if (blimpStraight) angle = 0;
                else if (blimpGoingUp) angle = 335;
                else angle = 25;
            }
        }
    }
    public void reset() {
        setPosition(spawnX, spawnY);
        parryBox.parried = false;
        parried = false;
        setDispatchSpeed = false;
        blimpDispatched = false;
        blimpSpr.setRotation(0);
        blimpSpr.setColor(1f, 1f, 1f, 1f);
    }
    @Override
    public void debug() {
        parryBox.debug();
        super.debug();

        if (blimpDispatched || player.state != PlayerState.NORMAL) boxColor.set(boxColor.r, boxColor.g, boxColor.b, 0f);
        else if (stunHitbox) boxColor.set(boxColor.r, boxColor.g, boxColor.b, 0.2f);
        else boxColor.set(boxColor.r, boxColor.g, boxColor.b, 0.6f);
    }
    public void dispose() {
        blimpTex.dispose();
        rTex.dispose();
    }

    //Misc
    private boolean setDispatchSpeed = false;
    private void dispatch() {
        if (blimpDispatched && !setDispatchSpeed) {
            blimpSpr.setColor(1f, 1f, 1f, 0.8f);
            yspeed = 12;
            setDispatchSpeed = true;
        }
        if (yspeed > -24) yspeed -= 0.5f;

        blimpSpr.rotate(-15f);

        if (y < getSize().y - 100) fullyDispatched = true;

        setPosition(x, y + yspeed);
    }
    public void setPosition(float X, float Y) {
        super.setProperties(X, Y, width, height);
        blimpSpr.setOriginBasedPosition(super.x + 50f, super.y + 25f);
        parryBox.setProperties(super.x - parryArea/2f, super.y - parryArea/2f * htwRatio - 20, parryBox.getSize().x, parryBox.getSize().y);
        reticle.setOriginBasedPosition(super.x + 52.5f, super.y + 25f);
    }
    public Vector2 getSize() {
        return new Vector2(width, height);
    }
}
