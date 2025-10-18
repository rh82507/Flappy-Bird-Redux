package com.badlogic.flappybird_redux.obstacles;

import com.badlogic.flappybird_redux.Level;
import com.badlogic.flappybird_redux.ref.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Pillar extends IdleObstacle {
    // Components
    private Obstacle top, bottom;
    public static Texture topTex, bottomTex;
    private Sprite topSprite, bottomSprite;
    private Trigger middle;
    private SpriteBatch batch;
    private ShapeRenderer sr;
    private Player player;
    // Variables
    private float x, y, width, height;
    public float gap;
    private final float dt = Gdx.graphics.getDeltaTime();

    public Pillar(Player p, SpriteBatch b, ShapeRenderer s, float x, float y, float width, float height, float gap) {
        player = p;
        batch = b;
        sr = s;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.gap = gap;

        bottom = new Obstacle(p, b, s, x, y, width, height);
        if (player.invincibilityTimer == 0)
            middle = new Trigger(p, b, s,x+Math.signum(scrollSpdInit)*gap/3.2f, y+height, width, gap);
        else
            middle = new Trigger(p, b, s, x+Math.signum(scrollSpdInit)*gap/3.2f, y, width, height*3+gap);
        top = new Obstacle(p, b, s, x, y+height+gap, width, Level.realSize.y-height);

        Pillar.topTex = new Texture(Gdx.files.internal("level/default/pillar_top.png"));
        Pillar.bottomTex = new Texture(Gdx.files.internal("level/default/pillar_bottom.png"));

        init();
    }
    public Pillar(Player p, SpriteBatch b, ShapeRenderer s, float x, float y, float width, float height) {
        this(p, b, s, x, y, width, height, 250);
    }

    public void init() {
        // Set Sprites
        topSprite = new Sprite(topTex, 0, 0, topTex.getWidth(), topTex.getHeight());
        bottomSprite = new Sprite(bottomTex, 0, 0, bottomTex.getWidth(), bottomTex.getHeight());
        // Sprite Properties
        ///     Top
        topSprite.setOrigin(0, 0);
        topSprite.setScale(0.1f);
        topSprite.setOriginBasedPosition(x, y+gap+height);
        ///     Bottom
        bottomSprite.setOrigin(0, topTex.getHeight()-50f);
        bottomSprite.setScale(0.1f);
        bottomSprite.setOriginBasedPosition(x, y+height-5f);

        topSprite.setFlip(!(IdleObstacle.scrollSpd < 0), false);
        bottomSprite.setFlip(!(IdleObstacle.scrollSpd < 0), false);
    }
    public void update() {
        //Update obstacles / triggers
        bottom.update();
        middle.update();
        top.update();

        //Add Speed
        setProperties(x - scrollSpd*100*Gdx.graphics.getDeltaTime(), y, width, height);
        topSprite.setOriginBasedPosition(x-18f, top.getPosition().y);
        bottomSprite.setOriginBasedPosition(x-18f, bottom.getPosition().y+height-5f);
    }
    public void updateOutOfLoop() {
        if (!topSprite.getTexture().equals(topTex)) {
            topSprite.setTexture(topTex);
            bottomSprite.setTexture(bottomTex);
        }
    }

    public void draw() {
        topSprite.draw(batch);
        bottomSprite.draw(batch);
    }
    public void debug() {
        if (player.invincibilityTimer == 0) bottom.debug();
        middle.debug();
        if (player.invincibilityTimer == 0) top.debug();
    }
    public void dispose() {
        topTex.dispose();
        bottomTex.dispose();
    }

    public void setHeight(float h) {
        height = h;
        bottom = new Obstacle(player, batch, sr, x, y, width, h);
        middle = new Trigger(player, batch, sr, x, y+h, width, gap);
        top = new Obstacle(player, batch, sr, x, y+h+gap, width, Level.realSize.y-h);
    }
    public void setProperties(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        width = w;
        height = h;

        bottom.setProperties(x, y, width, height);
        if (player.invincibilityTimer == 0)
            middle.setProperties(x+Math.signum(scrollSpdInit)*gap/3.2f, y+height, width, gap);
        else
            middle.setProperties(x+Math.signum(scrollSpdInit)*gap/3.2f, y, width, height*3+gap);
        top.setProperties(x, y+height+gap, width, Level.realSize.y-height);

        topSprite.setOriginBasedPosition(x-18f, top.getPosition().y);
        bottomSprite.setOriginBasedPosition(x-18f, bottom.getPosition().y+height-5f);
    }
    public Vector2 getPosition() {
        return new Vector2(x, y);
    }
    public Vector2 getScale() {
        return new Vector2(width, height);
    }

    public boolean passed() {
        return middle.enteredTrigger();
    }
}
