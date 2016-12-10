package com.fdorothy.dinopox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class MainMenuScreen implements Screen {

  final DinoGame game;

  OrthographicCamera camera;

  public MainMenuScreen(final DinoGame game) {
    this.game = game;
    camera = new OrthographicCamera();
    camera.setToOrtho(false, game.res.width, game.res.height);
  }


  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0.15f, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    camera.update();
    game.res.batch.setProjectionMatrix(camera.combined);

    game.res.batch.begin();
    game.res.font.draw(game.res.batch, "Welcome to DinoPox", 100, 150);
    game.res.font.draw(game.res.batch, "Tap anywhere to begin!", 100, 100);
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
