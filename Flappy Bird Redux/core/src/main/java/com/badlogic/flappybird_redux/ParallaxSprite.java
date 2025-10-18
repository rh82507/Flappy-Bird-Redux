package com.badlogic.flappybird_redux;

import com.badlogic.flappybird_redux.ref.IdleObstacle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;

public class ParallaxSprite {
    // Properties
    public int spriteAmt = 2;
    private Sprite[] sprites = new Sprite[spriteAmt];
    private Vector2 initPos;
    private float scale = 0;
    private int layer;
    // Variables
    public float pOffset = 0, nOffset = 0;

    public ParallaxSprite(Texture t, float x, float y, float scale, int layer) {
        for (int i=0; i < sprites.length; i++) {
            sprites[i] = new Sprite(t, 0, 0, t.getWidth(), t.getHeight());
            sprites[i].setOrigin(0, 0);
            sprites[i].setScale(scale);
        }

        initPos = new Vector2(x, y);
        this.scale = scale;
        this.layer = layer;

        sprites[0].setOriginBasedPosition(x, y);
        sprites[1].setOriginBasedPosition(x - sprites[0].getWidth()*scale, y);
        for (int i=2; sprites.length > 2 && i < sprites.length; i++) sprites[i].setOriginBasedPosition(x + sprites[0].getWidth()*scale, y);
    }

    public void draw(SpriteBatch b) {
        for (int i=0; i < sprites.length; i++)
            sprites[i].draw(b);

        if (!Main.isPaused()) update();
    }

    public void update() {
        for (int i=0; i < sprites.length; i++) {
            sprites[i].setPosition(scrollMovement(layer, sprites[i].getX()), sprites[i].getY());

            if (IdleObstacle.scrollSpd > 0 && sprites[i].getX() < -sprites[i].getWidth()*scale - pOffset) {
                sprites[i].setPosition(rightmostSprite(sprites).getX() + rightmostSprite(sprites).getWidth()*scale-IdleObstacle.scrollSpd, sprites[i].getY());
            }
            else if (IdleObstacle.scrollSpd < 0 && sprites[i].getX() > Gdx.graphics.getWidth() + nOffset) {
                sprites[i].setPosition(leftmostSprite(sprites).getX() - leftmostSprite(sprites).getWidth()*scale+IdleObstacle.scrollSpd, sprites[i].getY());
            }
        }
    }
    public void reset() {
        sprites[0].setOriginBasedPosition(initPos.x, initPos.y);
        sprites[1].setOriginBasedPosition(initPos.x - sprites[0].getWidth()*scale, initPos.y);
    }

    float pos;
    private float scrollMovement(int layer, float x) {
        pos = x - IdleObstacle.scrollSpd * (1f / layer) * 83 * Gdx.graphics.getDeltaTime();

        return pos;
    }

    int t1 = 0, t2 = 0;
    private Sprite rightmostSprite(Sprite[] s) {
        int t1 = 0;

        for (int i=0; i < s.length; i++) {
            if (s[i].getX() > s[t1].getX()) t1 = i;
        }

        return s[t1];
    }
    private Sprite leftmostSprite(Sprite[] s) {
        int t2 = 0;

        for (int i=0; i < s.length; i++) {
            if (s[i].getX() < s[t2].getX()) t2 = i;
        }

        return s[t2];
    }

    public Vector2 getSize() {
        return new Vector2(sprites[0].getWidth()*scale, sprites[0].getHeight()*scale);
    }
    public Sprite getSprite(int index) {
        return sprites[index];
    }

    public void setTexture(Texture t) {
        for (int i=0; i < sprites.length; i++)
            sprites[i].setTexture(t);
    }
    public void setPosition(float x, float y) {
        for (int i=0; i < sprites.length; i++)
            sprites[i].setOriginBasedPosition(x + sprites[0].getWidth()*scale*i, y);
    }
    public Vector2 getPosition() {
        return new Vector2(sprites[0].getX(), sprites[0].getY());
    }
    public void setColor(float r, float g, float b, float a) {
        for (int i=0; i < sprites.length; i++)
            sprites[i].setColor(r, g, b, a);
    }
}
