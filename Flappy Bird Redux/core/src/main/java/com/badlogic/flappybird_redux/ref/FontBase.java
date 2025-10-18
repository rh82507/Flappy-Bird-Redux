package com.badlogic.flappybird_redux.ref;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FontBase {
    public static BitmapFont scoreFont, gameFont, resultFont;
    private static GlyphLayout layout = new GlyphLayout();

    public FontBase() {
        scoreFont = new BitmapFont(Gdx.files.internal("fonts/score/scoreFont.fnt"));
        scoreFont.getData().setScale(0.4f);
        scoreFont.setUseIntegerPositions(false);

        gameFont = new BitmapFont(Gdx.files.internal("fonts/buttonFont/tempo.fnt"));
        gameFont.getData().setScale(0.4f);
        gameFont.setUseIntegerPositions(false);

        resultFont = new BitmapFont(Gdx.files.internal("fonts/result/resultFont.fnt"));
        resultFont.getData().setScale(0.4f);
        resultFont.setUseIntegerPositions(false);
    }

    public static void display(String text, BitmapFont f, SpriteBatch b, float x, float y, float scale, float alpha) {
        if (f.getData().scaleX != scale) f.getData().setScale(scale);
        float fontX = x + (-layout.width) / 2;
        float fontY = y + (layout.height) / 2;
        layout.setText(f, text);
        f.setColor(1f, 1f, 1f, alpha);
        f.draw(b, layout, fontX, fontY);
    }
    public static void display(String text, BitmapFont f, SpriteBatch b, float x, float y, float scale) {
        if (f.getData().scaleX != scale) f.getData().setScale(scale);
        float fontX = x + (-layout.width) / 2;
        float fontY = y + (layout.height) / 2;
        layout.setText(f, text);
        f.draw(b, layout, fontX, fontY);
    }
    public static void display(String text, BitmapFont f, SpriteBatch b, float x, float y) {
        float fontX = x + (-layout.width) / 2;
        float fontY = y + (layout.height) / 2;
        layout.setText(f, text);
        f.draw(b, layout, fontX, fontY);
    }
}
