package com.badlogic.flappybird_redux.ref;

import com.badlogic.flappybird_redux.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;

import java.util.Arrays;

public class Player {
    public static float lerpAngle(float a, float b, float f) {
        if (a < 180 && b > 180) b = b-360;
        else if (a > 180 && b < 180) b = 360+b;

        return a * (1.0f - f) + (b * f);
    }
    // Components
    private final SpriteBatch batch;
    private Texture tex;
    private Sprite sprite;
    public final TextureRegion[] birdFrames = new TextureRegion[5];
    public SpriteAnimation birdAnim;
    private float birdTime = 0;
    private final ShapeRenderer shapeRender;
    private Hitbox hitbox;
    // Variables
    ////    Collision
    public boolean hitDown = false, hitUp = false;
    public boolean grounded = false;
    public float invincibilityTimer = 0;
    ////    Visual
    public boolean flipX = false;
    public PlayerState state = PlayerState.NORMAL;
    private float realAngle = 0, visualAngle = 0;
    private float angleAlpha = 0.5f;
    private boolean pauseAnim = false;
    private float birdDt = Gdx.graphics.getDeltaTime();
    ////    Movement
    public int playerLockTime = 0;
    private Vector2 position, size, realPivot;
    private float xspeed = 0, yspeed = 0;
    private float grv = 0.478f * 100f;
    private float jmp = 14f; //12.5
    public boolean playerJumped = false;
    private boolean clickedJump = false;
    // Hitbox
    private Vector2 hitboxSize;
    // Explosion
    private int trickValue = 0;
    private boolean setExplosionPos = false, playedExplodeSound = false;
    private Vector2 explosionPosition = new Vector2();
    private final TextureRegion[] explosionFrames = new TextureRegion[20];
    private SpriteAnimation explosionAnim;
    private float elapsedTime = 0;
    private float explodeSfxInitVol = 1;
    private Music explodeSfx;
    // Parry Smack
    public Music parrySfx;
    private float parrySfxInitVol = 0.55f;
    private float colorAlpha = 1f;

    public Player(SpriteBatch b, ShapeRenderer s) {
        batch = b;
        shapeRender = s;
        size = new Vector2(120, 120 * (67/105f));
        position = new Vector2(Gdx.graphics.getWidth()/2f-size.x/2f, Gdx.graphics.getHeight()/2f-size.y/2f);
        setPivot(position);

        init();
    }
    public Player(SpriteBatch b, ShapeRenderer s, float x, float y) {
        this(b, s);
        setPosition(x, y);
    }
    public Player(SpriteBatch b, ShapeRenderer s, float x, float y, float scale) {
        this(b, s, x, y);
        setScale(scale);
    }
    public Player(SpriteBatch b, ShapeRenderer s, float scale) {
        this(b, s);
        setScale(scale);
    }

    public void init() {
        hitboxSize = new Vector2(size.y * 0.7f, size.y * 0.7f);

        Texture birdSpritesheet = null;
        if (Level.background.equalsIgnoreCase("default"))
            birdSpritesheet = new Texture(Gdx.files.internal("player/bird_spritesheet.png"));
        else if (Level.background.equalsIgnoreCase("night"))
            birdSpritesheet = new Texture(Gdx.files.internal("player/bird_spritesheet-night.png"));
        TextureRegion[][] birdSheetRaw = TextureRegion.split(birdSpritesheet, 1604, 1246);
        int birdIndex = 0;
        for (int r=0; r < birdSheetRaw.length; r++) {
            for (int c=0; c < birdSheetRaw[r].length; c++) {
                birdFrames[birdIndex++] = birdSheetRaw[r][c];
            }
        }
        birdAnim = new SpriteAnimation(0.065f, birdFrames);
        birdAnim.setScaling(0.1f);
        birdAnim.flipX = flipX;

        //tex = new Texture(Gdx.files.internal("player/player_bird.png"));
        //sprite = new Sprite(tex, 0, 0, tex.getWidth(), tex.getHeight());
        //sprite.setBounds(position.x, position.y, size.x, size.y);
        //sprite.setFlip(flipX, sprite.isFlipY());
        //sprite.setOriginCenter();

        //Explosion
        explodeSfx = Gdx.audio.newMusic(Gdx.files.internal("player/explosion_sfx.mp3"));
        Texture explosionSpritesheet = new Texture(Gdx.files.internal("player/explosion.png"));
        TextureRegion[][] explosionFramesRaw = TextureRegion.split(explosionSpritesheet, 200, 282);
        int index = 0;
        for (int r=0; r < explosionFramesRaw.length; r++) {
            for (int c=0; c < explosionFramesRaw[r].length; c++) {
                if (explosionFramesRaw[r][c] != null) explosionFrames[index++] = explosionFramesRaw[r][c];
            }
        }
        explosionAnim = new SpriteAnimation(0.075f, explosionFrames);
        explosionAnim.setScaling(1.25f);

        setTrickValue();

        parrySfx = Gdx.audio.newMusic(Gdx.files.internal("player/smack.mp3"));
        parrySfx.setLooping(false);
    }
    public void update(float dt) {
        if (!Main.isPaused()) {
            if (playerLockTime > 0) playerLockTime--;
            setPosition(position.x + xspeed, position.y + yspeed);
            setPivot(getPosition());
        }

        // Bird Invincible State
        if (state == PlayerState.NORMAL) {
            birdAnim.setColor(1f, 1f, 1f, Title.lerp(birdAnim.getAlpha(), colorAlpha, 0.08f * dt * 100));

            if (invincibilityTimer > 0 && colorAlpha != 0.15f) colorAlpha = 0.15f;
            else if (colorAlpha != 1f) colorAlpha = 1f;
        }

        if (!Main.isPaused() && state == PlayerState.NORMAL && !pauseAnim) birdTime += birdDt;
        birdAnim.angle = visualAngle * ((state == PlayerState.NORMAL) ? 0.85f : 1f);
        birdAnim.drawSprite(birdTime, batch, position.x, position.y, true);

        // Volume - Explosion
        if (explodeSfx.getVolume() != explodeSfxInitVol * Options.sfxVolume && !Main.muted)
            explodeSfx.setVolume(explodeSfxInitVol * Options.sfxVolume);
        else if (Main.muted && explodeSfx.getVolume() != 0) explodeSfx.setVolume(0);
        // Volume - Parry
        if (parrySfx.getVolume() != parrySfxInitVol * Options.sfxVolume && !Main.muted)
            parrySfx.setVolume(parrySfxInitVol * Options.sfxVolume);
        else if (Main.muted && parrySfx.getVolume() != 0) parrySfx.setVolume(0);

        // Invincible Frames
        if (invincibilityTimer > 0) invincibilityTimer -= dt;
        else invincibilityTimer = 0;

        if (state == PlayerState.HURT && invincibilityTimer > 0) state = PlayerState.NORMAL;

        // Game Loop
        if (state == PlayerState.NORMAL) {
            // Explosion Reset
            if (elapsedTime > 0) elapsedTime = 0f;
            if (setExplosionPos) setExplosionPos = false;
            if (playedExplodeSound) playedExplodeSound = false;
            if (explodeSfx.isPlaying()) explodeSfx.stop();

            if (hitDown) {
                grounded = true;
                hitDown = false;
            }
            if (!Main.isPaused()) {
                // Gravity
                if (!grounded && yspeed > -1600f * dt) yspeed -= grv * dt;

                //Jump
                jump();

                // Grounded
                if (grounded) {
                    setVelocity(0, 0);
                }

                birdDt = dt;
                //Angle Stuff
                visualAngle = lerpAngle(visualAngle, realAngle, ((!grounded) ? angleAlpha : 0.2f));
                if (!flipX) {
                    if (yspeed > 0) {
                        realAngle = 33;
                        angleAlpha = 0.2f;
                    }
                    else if (yspeed < 0 && !grounded) {
                        if (realAngle > 315 || realAngle < 180) realAngle = 315;
                        else if (realAngle > 270) realAngle -= 1.1f;
                        angleAlpha = 0.07f;
                        if (yspeed < -2) birdDt = dt * 0.7f;
                    }
                    else { realAngle = 0; }
                }
                else {
                    if (yspeed > 0) {
                        realAngle = 360 - 33;
                        angleAlpha = 0.2f;
                    }
                    else if (yspeed < 0 && !grounded) {
                        if (realAngle < 45 || realAngle > 180) realAngle = 45;
                        else if (realAngle < 90) realAngle += 1.1f;
                        angleAlpha = 0.07f;
                        if (yspeed < -2) birdDt = dt * 0.7f;
                    }
                    else { realAngle = 0; }
                }
            }
        }
        if (state == PlayerState.HURT) {
            if (!Main.isPaused()) {
                grounded = false;
                hitDown = hitUp = false;
                if (!setExplosionPos) {
                    setVelocity(0, 0);
                    explosionPosition = new Vector2(position.x - size.x/1.7f, position.y - size.y/0.66f);
                    setExplosionPos = true;
                }
                if (explosionAnim.getKeyFrameIndex(elapsedTime) >= 5) hurtSystem(dt);
            }

            // Explode
            if (!Main.isPaused()) elapsedTime += dt;
            explosionAnim.drawTexture(elapsedTime, batch, explosionPosition.x, explosionPosition.y, false);
            if (!playedExplodeSound) {
                explodeSfx.play();
                explodeSfx.setVolume((!Main.muted) ? 0.5f : 0f);
                explodeSfx.setLooping(false);
                playedExplodeSound = true;
            }
            if (Main.isPaused() && explodeSfx.isPlaying()) explodeSfx.pause();
            else if (!Main.isPaused() && !explodeSfx.isPlaying()) explodeSfx.play();

            if (position.y < -size.y*3.5f && explosionAnim.isAnimationFinished(elapsedTime)) state = PlayerState.DEAD;
        }
        if (state == PlayerState.DEAD) {
            setVelocity(0, 0);
        }
    }
    public void trash() {
        //tex.dispose();
        for (TextureRegion tr : birdFrames) tr.getTexture().dispose();
        for (TextureRegion ef : explosionFrames) ef.getTexture().dispose();
        explodeSfx.dispose();
        birdAnim.dispose();
    }

    public void debug() {
        shapeRender.setColor(0f, 1f, 0f, 0.6f);
        shapeRender.rect(realPivot.x-hitboxSize.x/2f, realPivot.y - hitboxSize.y/2f, hitboxSize.x, hitboxSize.y);
    }
    private void vertVelDT(float val) {
        if (Gdx.graphics.getFramesPerSecond() > 1f && Math.round(Gdx.graphics.getFramesPerSecond() / 10f) * 10f != 60f && Gdx.graphics.getFramesPerSecond() < 60)
            setVelocity(getVelocity().x, val + (float) Math.pow(2, 60f / Gdx.graphics.getFramesPerSecond()) - 3f * (Gdx.graphics.getFramesPerSecond() / 30f - 1f));
        else if (Gdx.graphics.getFramesPerSecond() > 60)
            setVelocity(getVelocity().x, val - (float) Math.pow(1.74f, Gdx.graphics.getFramesPerSecond() / 60f));
        else setVelocity(getVelocity().x, val);
    }
    public void jump() {
        //Jump
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            clickedJump = playerJumped = true;
        }
        if (playerJumped && clickedJump) {
            hitDown = grounded = false;
            if (playerLockTime == 0) vertVelDT(jmp);
            clickedJump = false;
        }
    }

    private boolean didHurtStuff = false;
    private void hurtSystem(float dt) {
        // Gravity
        if (!grounded && yspeed > -1600f * dt) yspeed -= grv * dt;

        if (!didHurtStuff) {
            if (trickValue == 1f) birdAnim.setColor(1f, 1f, 1f, 0f); //Disappearing Trick
            birdTime = 0;
            setVelocity(0, 0);
            vertVelDT(24.5f);
            didHurtStuff = true;
        }

        // Explosion Tricks
        /// Nose Dive
        if (trickValue == 0f) {
            if (!flipX && visualAngle < 270) visualAngle += 6f;
            else if (flipX && visualAngle > -270) visualAngle -= 6f;
        }
        /// The 360
        if (trickValue == 2f) visualAngle += ((!flipX) ? 35f : -35f);

        realAngle = visualAngle;
    }

    public float bounds(Hitbox h) {
        switch (h) {
            case LEFT:
                return realPivot.x - hitboxSize.x/2;
            case RIGHT:
                return realPivot.x + hitboxSize.x/2;
            case UP:
                return realPivot.y + hitboxSize.y/2;
            case DOWN:
                return realPivot.y - hitboxSize.y/2;
        }

        return 0f;
    }

    public void reset() {
        // Reset Animation
        birdTime = 0;
        // Player Stuff
        birdAnim.setColor(1f, 1f, 1f, 1f);
        state = PlayerState.NORMAL;
        didHurtStuff = false;
        setPosition(Main.initScreenSize.x/2f-size.x/2f, Main.initScreenSize.y/2f-size.y/2f);
        visualAngle = realAngle = 0;
        setVelocity(0, 0);
        // Pause Game
        Main.pauseGame(true);
        // Set Trick Value
        int curVal = trickValue;
        while (trickValue == curVal) setTrickValue();
    }

    // Pivot
    public void setPivot(Vector2 p) {
        realPivot = new Vector2(p.x + size.x/2f, p.y + size.y/2f);
    }
    public void setPivot(float x, float y) {
        realPivot = new Vector2(x + size.x/2f, y + size.y/2f);
    }
    public Vector2 getPivot() { return realPivot; }
    // Position
    public void setPosition(float x, float y) {
        position = new Vector2(x, y);
        setPivot(position);
    }
    public void setPosition(Vector2 p) {
        position = new Vector2(p.x + size.x/2f, p.y + size.y/2f);
        setPivot(position);
    }
    public Vector2 getPosition() {
        return position;
    }
    // Scale
    public void setScale(float s) {
        size = new Vector2(size.x * s, size.y * s);
    }
    public Vector2 getScale() {
        return size;
    }
    public void flipPlayer(boolean flip) {
        birdAnim.flipX = flipX = flip;
    }
    // Velocity
    public void setVelocity(float x, float y) {
        xspeed = x;
        yspeed = y;
    }
    public Vector2 getVelocity() {
        return new Vector2(xspeed, yspeed);
    }

    private void setTrickValue() {
        if (Math.random() <= 0.33) trickValue = 0;
        else if (Math.random() <= 0.66) trickValue = 1;
        else trickValue = 2;
    }
}
