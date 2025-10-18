package com.badlogic.flappybird_redux.ref;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Obstacle extends Trigger {
    public boolean parried = false;
    public boolean stunHitbox = false;

    public Obstacle(Player p, SpriteBatch b, ShapeRenderer s, float x, float y, float w, float h) {
        super(p, b, s, x, y, w, h);
        boxColor = new Color(1f, 0f, 0f, 0.6f);
    }
    public Obstacle(Player p, SpriteBatch b, ShapeRenderer s, float x, float y) {
        this(p, b, s, x, y, 500, 500);
    }
    public Obstacle(Player p, SpriteBatch b, ShapeRenderer s) {
        this(p, b, s, 0, 0);
    }

    @Override
    public void update() {
        super.update();
        if (withinTrigger() && !parried && !stunHitbox && player.invincibilityTimer == 0) {
            player.state = PlayerState.HURT;
        }
    }
}
