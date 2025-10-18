package com.badlogic.flappybird_redux;

import com.badlogic.flappybird_redux.ref.FontBase;
import com.badlogic.flappybird_redux.ref.IdleObstacle;
import com.badlogic.flappybird_redux.ref.Screens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;

public class Score implements Screen {
    // Components
    public SpriteBatch batch;
    ///     Board
    private Texture boardTex;
    public Sprite scoreBoard;
    ///     Back Button
    private Skin buttonSkin;
    private TextButton backBtn, resetScore;

    public Score(SpriteBatch b) {
        batch = b;
    }

    @Override
    public void show() {
        IdleObstacle.scrollSpd = 4;
        //Unpause Game
        //resize((int)(1280/1.3), (int)(720/1.3));
        Main.pauseGame(false);
        Main.playerPaused = false;

        // Board
        boardTex = new Texture(Gdx.files.internal("title/board.png"));
        scoreBoard = new Sprite(boardTex, 0, 0, boardTex.getWidth(), boardTex.getHeight());
        scoreBoard.setOrigin(0, 0);
        scoreBoard.setScale(0.13f);
        scoreBoard.setOriginBasedPosition(Level.realSize.x/2f - 295f, 148f);

        // Initialize Button
        buttonSkin = new Skin(Gdx.files.internal("skins/button_temp/buttonTemplate.json"));
        // Back
        backBtn = new TextButton("BACK", buttonSkin);
        backBtn.getLabel().setFontScale(1.65f);
        backBtn.setTransform(true);
        backBtn.setScale(0.125f);
        backBtn.setPosition(scoreBoard.getX()+scoreBoard.getWidth()*scoreBoard.getScaleX()/3f-13f*10f, scoreBoard.getY()-110f);
        Main.stage.addActor(backBtn);
        // Reset Scores
        resetScore = new TextButton("RESET SCORES", buttonSkin);
        resetScore.getLabel().setFontScale(1.4f);
        resetScore.setTransform(true);
        resetScore.setScale(0.125f);
        resetScore.setPosition(scoreBoard.getX()+scoreBoard.getWidth()*scoreBoard.getScaleX()/3f-13f*10f + 250f, scoreBoard.getY()-110f);
        Main.stage.addActor(resetScore);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f,  1f);

        batch.begin();
        Title.sky.draw(batch);
        if (Level.background.equalsIgnoreCase("night")) Title.stars.draw(batch);
        Title.cBack.draw(batch);
        Title.cFront.draw(batch);
        Title.buildings.draw(batch);

        FontBase.display("TOP 5 BEST SCORES", FontBase.gameFont, batch, scoreBoard.getX()+340f, scoreBoard.getY() + scoreBoard.getHeight()*scoreBoard.getScaleY()+75f, 0.35f);
        scoreBoard.draw(batch);
        FontBase.gameFont.getData().setScale(0.3f);
        //28
        for (int i=0; i < Main.data.getScore().size(); i++) {
            //FontBase.scoreFont.draw(batch, (i+1) + ":\t\t\t\t\t\t\t\t\t" + Main.data.getScore().get(i), scoreBoard.getX()+230f, scoreBoard.getY() + scoreBoard.getHeight()*scoreBoard.getScaleY()-38f - 83*i);
            String q = ".\t\t\t\t\t";

            if (String.valueOf(Main.data.getScore().get(i)).length() == 1) q += "\t\t";
            else if (String.valueOf(Main.data.getScore().get(i)).length() == 2) q += "\t";

            if (i == 0)
                FontBase.gameFont.draw(batch, (i+1) + q + Main.data.getScore().get(i), scoreBoard.getX()+242f, scoreBoard.getY() + scoreBoard.getHeight()*scoreBoard.getScaleY()-38f - 83*i);
            else
                FontBase.gameFont.draw(batch, (i+1) + q + Main.data.getScore().get(i), scoreBoard.getX()+238f, scoreBoard.getY() + scoreBoard.getHeight()*scoreBoard.getScaleY()-38f - 83*i);
        }

        backBtn.draw(batch, 1f);
        resetScore.draw(batch, 1f);
        batch.end();

        // Button Logic
        if (backBtn.isChecked() && ScreenManager.currentScreen() != Screens.Title) {
            ScreenManager.setScreen(Screens.Title);
            backBtn.setChecked(false);
        }
        if (resetScore.isChecked()) {
            Main.data.reset();
            Main.data.save();
            resetScore.setChecked(false);
        }
    }

    @Override
    public void resize(int width, int height) {
        Gdx.graphics.setWindowedMode(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        boardTex.dispose();
        buttonSkin.dispose();
    }
}
