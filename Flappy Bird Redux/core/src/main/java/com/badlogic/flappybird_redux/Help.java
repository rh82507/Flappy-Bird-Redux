package com.badlogic.flappybird_redux;

import com.badlogic.flappybird_redux.ref.FontBase;
import com.badlogic.flappybird_redux.ref.IdleObstacle;
import com.badlogic.flappybird_redux.ref.Screens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;

public class Help implements Screen {
    // Components
    private SpriteBatch batch;
    public static boolean enteredFromOptions = false, enteredFromPauseMenu = false;
    private Level level;
    // Input Images
    private Texture wTex, upTex, spaceTex, LDRTex, ASDTex;
    private Sprite W, Up, Space, ldr, asd;
    // Examples
    private Texture exJumpTex, exB1Tex, exB2Tex, exInvTex;
    private Sprite exJump, exB1, exB2, exInv;
    // Buttons
    private Skin buttonSkin;
    private TextButton nextBtn, exitBtn, backBtn, practiceBtn;
    private boolean alreadyPressedNext = false, alreadyPressedBack = false;
    // Variables
    public static int page = 1;

    public Help(SpriteBatch b, Level level) {
        batch = b;
        this.level = level;

        // Example Textures
        exJumpTex = new Texture(Gdx.files.internal("examples/example_jump.png"));
        exB1Tex = new Texture(Gdx.files.internal("examples/example_blimp1.png"));
        exB2Tex = new Texture(Gdx.files.internal("examples/example_blimp2.png"));
        exInvTex = new Texture(Gdx.files.internal("examples/example_inv.png"));
        // Example Sprites
        /// Jump
        exJump = new Sprite(exJumpTex, 0, 0, exJumpTex.getWidth(), exJumpTex.getHeight());
        exJump.setOrigin(0, 0);
        exJump.setScale(0.8f);
        exJump.setOriginBasedPosition(Level.realSize.x/4f - 15f, 10f);
        /// Parry 1
        exB1 = new Sprite(exB1Tex, 0, 0, exB1Tex.getWidth(), exB1Tex.getHeight());
        exB1.setOrigin(0, 0);
        exB1.setScale(0.5f);
        exB1.setOriginBasedPosition(Level.realSize.x/4f - 25f - 100f*2f, 100f);
        // Parry 2
        exB2 = new Sprite(exB2Tex, 0, 0, exB2Tex.getWidth(), exB2Tex.getHeight());
        exB2.setOrigin(0, 0);
        exB2.setScale(0.5f);
        exB2.setOriginBasedPosition(Level.realSize.x/4f - 25f + 100f*3f, 100f);
        // Invincibility
        exInv = new Sprite(exInvTex, 0, 0, exInvTex.getWidth(), exInvTex.getHeight());
        exInv.setOrigin(0, 0);
        exInv.setScale(1.02f);
        exInv.setOriginBasedPosition(Level.realSize.x/4f - 14f, 10f);

        // Init Input Images
        wTex = new Texture(Gdx.files.internal("inputs/W.png"));
        upTex = new Texture(Gdx.files.internal("inputs/Up.png"));
        spaceTex = new Texture(Gdx.files.internal("inputs/Space.png"));
        LDRTex = new Texture(Gdx.files.internal("inputs/LDR.png"));
        ASDTex = new Texture(Gdx.files.internal("inputs/ASD.png"));
        // Init Input Sprites
        ///     W
        W = new Sprite(wTex, 0, 0, wTex.getWidth(), wTex.getHeight());
        W.setScale(0.1f);
        W.setOrigin(0, 0);
        W.setOriginBasedPosition(Level.realSize.x/2f + 116*1.72f, Level.realSize.y - 205f+50f);
        ///     Up
        Up = new Sprite(upTex, 0, 0, upTex.getWidth(), upTex.getHeight());
        Up.setScale(0.1f);
        Up.setOrigin(0, 0);
        Up.setOriginBasedPosition(Level.realSize.x/2f + 116*1.72f, Level.realSize.y - 205f-50);
        ///      Space
        Space = new Sprite(spaceTex, 0, 0, spaceTex.getWidth(), spaceTex.getHeight());
        Space.setScale(0.1f);
        Space.setOrigin(0, 0);
        Space.setOriginBasedPosition(Level.realSize.x/2f + 116, Level.realSize.y - 205f);
        ///     LDR
        ldr = new Sprite(LDRTex, 0, 0, LDRTex.getWidth(), LDRTex.getHeight());
        ldr.setScale(0.08f);
        ldr.setOrigin(0, 0);
        ldr.setOriginBasedPosition(Level.realSize.x/2f + 145*2.35f, Level.realSize.y - 212f + 50);
        ///     ASD
        asd = new Sprite(ASDTex, 0, 0, ASDTex.getWidth(), ASDTex.getHeight());
        asd.setScale(0.08f);
        asd.setOrigin(0, 0);
        asd.setOriginBasedPosition(Level.realSize.x/2f + 145*2.35f, Level.realSize.y - 212f);
    }

    @Override
    public void show() {
        // Initialize Button
        buttonSkin = new Skin(Gdx.files.internal("skins/button_temp/buttonTemplate.json"));
        // Next
        nextBtn = new TextButton("NEXT", buttonSkin);
        nextBtn.getLabel().setFontScale(1.65f);
        nextBtn.setTransform(true);
        nextBtn.setScale(0.11f);
        nextBtn.setPosition(Level.realSize.x - 150, 20f);
        Main.stage.addActor(nextBtn);
        // Exit
        exitBtn = new TextButton("EXIT", buttonSkin);
        exitBtn.getLabel().setFontScale(1.65f);
        exitBtn.setTransform(true);
        exitBtn.setScale(0.11f);
        exitBtn.setPosition(30, Level.realSize.y - 50f);
        Main.stage.addActor(exitBtn);
        // Back
        backBtn = new TextButton("BACK", buttonSkin);
        backBtn.getLabel().setFontScale(1.65f);
        backBtn.setTransform(true);
        backBtn.setScale(0.11f);
        backBtn.setPosition(30f, 20f);
        Main.stage.addActor(backBtn);
        // Practice Parry
        practiceBtn = new TextButton("PRACTICE\nPARRY", buttonSkin);
        practiceBtn.getLabel().setFontScale(1.65f, 1.65f * 0.11f/0.16f * 0.7f);
        practiceBtn.setTransform(true);
        practiceBtn.setScale(0.11f, 0.22f);
        practiceBtn.setPosition(Level.realSize.x - 185, 150f);
        Main.stage.addActor(practiceBtn);
    }

    @Override
    public void render(float v) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        if (IdleObstacle.scrollSpd != 4) IdleObstacle.scrollSpd = 4;

        batch.begin();
        Title.sky.draw(batch);
        Options.buildings.draw(batch);
        Options.treeBack.draw(batch);
        Options.treeMid.draw(batch);
        Options.treeFront.draw(batch);

        FontBase.display("How To Play", FontBase.resultFont, batch, Level.realSize.x/2f + 40, Level.realSize.y - 20f, 0.4f);

        // Jump Example
        if (page == 0) {
            FontBase.resultFont.getData().setScale(0.21f);
            FontBase.resultFont.draw(batch, "Left Click or Press                to Jump", Level.realSize.x/8f + 32, Level.realSize.y - 170f);
            Space.draw(batch);
            Up.draw(batch);
            W.draw(batch);
            exJump.draw(batch);
        }
        else if (page == 1) {
            FontBase.resultFont.getData().setScale(0.15f);
            FontBase.resultFont.draw(batch, "Right Click or Press any of the following              to Parry", Level.realSize.x/8f - 32, Level.realSize.y - 160f);
            FontBase.resultFont.getData().setScale(0.18f);
            FontBase.resultFont.draw(batch, "You are able to parry blimps when\n         a reticle appears on it.", Level.realSize.x/8f + 32*2.75f, Level.realSize.y - 160f*1.5f);
            ldr.draw(batch);
            asd.draw(batch);
            exB1.draw(batch);
            exB2.draw(batch);
            practiceBtn.draw(batch, (!enteredFromPauseMenu) ? 1f : 0.6f);

            if (enteredFromPauseMenu) practiceBtn.setTouchable(Touchable.disabled);
            else practiceBtn.setTouchable(Touchable.enabled);
            FontBase.resultFont.getData().setScale(0.12f);
            if (enteredFromPauseMenu) FontBase.resultFont.draw(batch, "You Cannot Practice When Already Playing Level", Level.realSize.x/8f + 32*2.75f + 35f, 70f);
        }
        else {
            FontBase.resultFont.getData().setScale(0.19f);
            FontBase.resultFont.draw(batch, "After parrying, you become invincible\n   to all obstacles for a few seconds", Level.realSize.x/8f + 32, Level.realSize.y - 150f);
            exInv.draw(batch);
        }

        if (page != 1) practiceBtn.setTouchable(Touchable.disabled);
        if (!enteredFromOptions) exitBtn.setTouchable(Touchable.disabled);
        else exitBtn.setTouchable(Touchable.enabled);

        if (page != 2 || !enteredFromOptions) nextBtn.draw(batch, 1f);
        if (page != 0) backBtn.draw(batch, 1f);
        if (enteredFromOptions) exitBtn.draw(batch, 1f);

        // Button Logic
        // Next / Play
        if (!enteredFromOptions && page == 2 && !nextBtn.getText().equals("PLAY")) nextBtn.setText("PLAY");
        else if (!nextBtn.getText().equals("NEXT")) nextBtn.setText("NEXT");
        if (enteredFromOptions || page != 2) {
            if (!nextBtn.isChecked() && alreadyPressedNext) alreadyPressedNext = false;
            if (nextBtn.isChecked() && !alreadyPressedNext) {
                if (page < 2) page++;
                alreadyPressedNext = true;
            }
            if (!nextBtn.isPressed() && alreadyPressedNext) nextBtn.setChecked(false);
        }
        else {
            if (!nextBtn.isChecked() && alreadyPressedNext) alreadyPressedNext = false;
            if (nextBtn.isChecked() && !alreadyPressedNext) {
                Level.practiceParryMode = false;
                page = 0;
                ScreenManager.setScreen(Screens.Level);
                nextBtn.setChecked(false);
                alreadyPressedNext = true;
            }
            if (!nextBtn.isPressed() && alreadyPressedNext) nextBtn.setChecked(false);
        }
        // Back
        if (!backBtn.isChecked() && alreadyPressedBack) alreadyPressedBack = false;
        if (backBtn.isChecked() && !alreadyPressedBack) {
            if (page > 0) page--;
            alreadyPressedBack = true;
        }
        if (!backBtn.isPressed() && alreadyPressedBack) backBtn.setChecked(false);
        // Exit
        if (exitBtn.isChecked()) {
            page = 0;
            Level.practiceParryMode = false;
            if (enteredFromOptions) ScreenManager.setScreen(Screens.Options);
            else ScreenManager.setScreen(Screens.Level);
            exitBtn.setChecked(false);
        }
        // Practice Parry
        if (!enteredFromPauseMenu && practiceBtn.isChecked() && ScreenManager.currentScreen() != Screens.Level) {
            Level.practiceParryMode = true;
            level.reset();
            ScreenManager.setScreen(Screens.Level);
            practiceBtn.setChecked(false);
        }

        batch.end();
    }

    @Override
    public void resize(int i, int i1) {
        Gdx.graphics.setWindowedMode(i, i1);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        buttonSkin.dispose();
        wTex.dispose();
        upTex.dispose();
        spaceTex.dispose();
        LDRTex.dispose();
        ASDTex.dispose();
        exJumpTex.dispose();
        exB1Tex.dispose();
        exB2Tex.dispose();
        exInvTex.dispose();
    }
}
