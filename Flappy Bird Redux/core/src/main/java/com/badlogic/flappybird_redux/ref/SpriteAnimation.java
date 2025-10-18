package com.badlogic.flappybird_redux.ref;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class SpriteAnimation extends Animation {
    public boolean flipX = false;
    float scaleX = 1;
    float scaleY = 1;
    public float angle = 0;
    private TextureRegion region;
    private Sprite s;
    private Color spriteColor = new Color(1f, 1f, 1f, 1f);

    public SpriteAnimation(float frameDuration, Array keyFrames) {
        super(frameDuration, keyFrames);
        s = new Sprite();
    }
    public SpriteAnimation(float frameDuration, Array keyFrames, PlayMode playMode) {
        super(frameDuration, keyFrames, playMode);
        s = new Sprite();

    }
    public SpriteAnimation(float frameDuration, Object[] keyFrames) {
        super(frameDuration, keyFrames);
        s = new Sprite();
    }

    public void setScaling(float scale){
        scaleX = (flipX) ? -scale : scale;
        scaleY = scale;
    }

    public void drawTexture(float stateTime, Batch batch, float x, float y, boolean loopAnim) {
        region = (TextureRegion)getKeyFrame(stateTime, loopAnim);
        batch.draw(region, x, y, region.getRegionWidth()*scaleX, region.getRegionHeight()*scaleY);
    }
    public void drawSprite(float stateTime, Batch batch, float x, float y, boolean loopAnim) {
        region = (TextureRegion)getKeyFrame(stateTime, loopAnim);
        s = new Sprite(region, 0, 0, region.getRegionWidth(), region.getRegionHeight());
        s.setOriginCenter();
        s.flip(flipX, false);
        if (!s.getColor().equals(spriteColor)) s.setColor(spriteColor);
        s.setOriginBasedPosition(x+60f, y+38f);
        s.setScale(scaleX*0.95f, scaleY*0.95f);
        s.setRotation(angle);
        s.draw(batch);
    }

    public void setColor(float r, float b, float g, float a) {
        spriteColor.set(r, g, b, a);
        s.setColor(spriteColor);
    }
    public float getAlpha() {
        return spriteColor.a;
    }

    public void dispose() {
        if (region != null) region.getTexture().dispose();
        if (s != null) s.getTexture().dispose();
    }
}
