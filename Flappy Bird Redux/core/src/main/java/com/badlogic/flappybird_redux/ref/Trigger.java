package com.badlogic.flappybird_redux.ref;

import com.badlogic.flappybird_redux.ref.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Trigger extends Collision {
    // Variables
    protected boolean inTrigger = false;
    private boolean wasInTrigger = false;
    private boolean leftTrigger = false;
    private boolean sensedTrigger = false;

    public Trigger(Player p, SpriteBatch b, ShapeRenderer s, float x, float y, float w, float h) {
        super(p, b, s, x, y, w, h);
        boxColor = new Color(1f, 0.55f, 0f, 0.6f);
    }
    public Trigger(Player p, SpriteBatch b, ShapeRenderer s, float x, float y) {
        this(p, b, s, x, y, 500, 500);
    }
    public Trigger(Player p, SpriteBatch b, ShapeRenderer s) {
        this(p, b, s, 0, 0);
    }

    // SpriteBatch
    @Override
    public void update() {
        if (isColliding()) { inTrigger = true; wasInTrigger = true; }
        else inTrigger = false;

        if (!isColliding() && wasInTrigger) { leftTrigger = true; wasInTrigger = false; }
        if (leftTrigger) {
            sensedTrigger = false;
            leftTrigger = false;
        }
    }

    public boolean withinTrigger() { return inTrigger; }

    public boolean enteredTrigger() {
        if (inTrigger && !sensedTrigger) {
            sensedTrigger = true;
            return true;
        }
        else return false;
    }
}
