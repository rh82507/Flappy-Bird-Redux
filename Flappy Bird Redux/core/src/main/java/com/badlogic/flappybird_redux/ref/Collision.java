package com.badlogic.flappybird_redux.ref;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Collision {
    // Components
    protected Player player;
    protected SpriteBatch batch;
    protected ShapeRenderer sr;
    // Variables
    protected Color boxColor;
    protected float x, y, width, height;

    public Collision(Player p, SpriteBatch b, ShapeRenderer s, float x, float y, float w, float h) {
        player = p;
        batch = b;
        sr = s;
        this.x = x;
        this.y = y;
        width = w;
        height = h;
        boxColor = new Color(0f, 0f, 1f, 0.6f);
    }
    public Collision(Player p, SpriteBatch b, ShapeRenderer s, float x, float y) {
        this(p, b, s, x, y, 500, 500);
    }
    public Collision(Player p, SpriteBatch b, ShapeRenderer s) {
        this(p, b, s, 0, 0);
    }

    // SpriteBatch
    public void update() {
        while (isColliding() && !player.playerJumped && player.state == PlayerState.NORMAL) {
            if (collidingVert()) {
                if (isCloserTo(player.getPivot().y, y+height, y)) {
                    player.setPosition(player.getPosition().x, player.getPosition().y + 0.01f);
                    player.setVelocity(0, 0);
                    player.grounded = player.hitDown = true;
                }
                else {
                    player.setVelocity(0, -2);
                    player.setPosition(player.getPosition().x, player.getPosition().y - 0.01f);
                    player.hitUp = true;
                }
            }
        }

        if (!isColliding()) {
            player.playerJumped = player.hitUp = player.hitDown = false;
        }
    }
    // ShapeRenderer
    public void debug() {
        sr.setColor(boxColor);
        sr.rect(x, y, width, height);
    }

    protected boolean collidingVert() {
        return (player.bounds(Hitbox.UP) >= y && player.bounds(Hitbox.DOWN) <= y+height);
    }
    protected boolean isColliding() {
        if (player.bounds(Hitbox.LEFT) <= x+width && player.bounds(Hitbox.RIGHT) >= x) {
            if (player.bounds(Hitbox.UP) >= y && player.bounds(Hitbox.DOWN) <= y+height) {
                return true;
            }
        }

        return false;
    }

    protected boolean isCloserTo(double is, double valTrue, double valFalse) {
        if (Math.abs(is - valTrue) <= Math.abs(is - valFalse)) return true;
        else return false;
    }
    public void setProperties(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        width = w;
        height = h;
    }
    public Vector2 getPosition() {
        return new Vector2(x, y);
    }
}
