package com.badlogic.flappybird_redux.ref;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class ParryBox extends Trigger {
    public boolean parried = false;

    public ParryBox(Player p, SpriteBatch b, ShapeRenderer s, float x, float y, float w, float h) {
        super(p, b, s, x, y, w, h);
        boxColor = new Color(245/255f, 66/255f, 227/255f, 0.5f);
    }
    public ParryBox(Player p, SpriteBatch b, ShapeRenderer s, float x, float y) {
        this(p, b, s, x, y, 500, 500);
    }
    public ParryBox(Player p, SpriteBatch b, ShapeRenderer s) {
        this(p, b, s, 0, 0);
    }

    @Override
    public void update() {
        super.update();

        if (withinTrigger() && parryInputted()) {
            parried = true;
        }
    }

    public boolean didParry() {
        return parried;
    }

    public Vector2 getSize() {
        return new Vector2(width, height);
    }

    private boolean parryInputted() {
        // Right Click
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) return true;

        // Arrow Keys
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN))
            return true;

        // WASD
        if (Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.A))
            return true;

        return false;
    }
}
