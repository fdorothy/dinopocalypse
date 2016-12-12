package com.fdorothy.dinopox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.Texture;

public class GameOverScreen implements Screen {
  final DinoGame game;
  OrthographicCamera camera;
  String reason;

  public GameOverScreen(final DinoGame game, String reason) {
    this.game = game;
    camera = new OrthographicCamera();
    camera.setToOrtho(false, game.res.width, game.res.height);
    this.reason = reason;
  }


  @Override
  public void render(float dt) {
    Gdx.gl.glClearColor(0, 0.15f, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    camera.update();
    game.res.batch.setProjectionMatrix(camera.combined);

    game.res.batch.begin();
    Texture t = game.res.gameover;
    game.res.batch.draw(t, game.res.width/2.0f - t.getWidth()/2.0f, game.res.height/2.0f-t.getHeight()/2.0f);
    game.res.font.draw(game.res.batch, reason, 0.0f, game.res.height-18);

    game.res.batch.end();

    if (Gdx.input.isTouched()) {
      game.setScreen(new GameScreen(game));
      dispose();
    }
  }

  @Override
  public void dispose () {
  }

  @Override
  public void hide() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void pause() {
  }

  @Override
  public void resize(int w, int h) {
  }

  @Override
  public void show() {
  }

}
