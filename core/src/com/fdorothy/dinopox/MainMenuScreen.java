package com.fdorothy.dinopox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MainMenuScreen implements Screen {

  final DinoGame game;

  OrthographicCamera camera;

  public MainMenuScreen(final DinoGame game) {
    this.game = game;
    camera = new OrthographicCamera();
    camera.setToOrtho(false, game.res.width, game.res.height);
  }


  public int print(SpriteBatch batch, String text, int line) {
    game.res.font.draw(batch, text, 0.0f, game.res.height-18*(line+1));
    return line + 1;
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0.15f, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    camera.update();
    game.res.batch.setProjectionMatrix(camera.combined);

    game.res.batch.begin();
    SpriteBatch b = game.res.batch;
    int offset = 0;
    offset = print(b, "2017, the dinopocalypse. Thunderlizards emerge from their subterranean slumber", offset);
    offset = print(b, "with their eyes on world domination. One man holds the key to cast them back", offset);
    offset = print(b, "to the center of the earth, but he is stranded in a single-room research lab", offset);
    offset = print(b, "in rural Alabama.", offset);
    offset = print(b, "", offset);
    offset = print(b, "Do you have what it takes to defeat General Ankyleesaurus's horde?", offset);
    offset = print(b, "", offset);
    offset = print(b, "Rules", offset);
    offset = print(b, "", offset);
    offset = print(b, " - You must survive 9 waves of attacks from the thunderlizard horde.", offset);
    offset = print(b, " - Touch the screen to move the researcher around.", offset);
    offset = print(b, " - Touch a thunderlizard to shoot at it, some are harder than others to kill", offset);
    
    b.draw(game.res.dinos[Resources.LEE], 0, 0);
    TextureRegion t = game.res.dinos[Resources.PARA];
    float w = t.getRegionWidth();
    float h = t.getRegionHeight();
    float x = game.res.width - t.getRegionWidth() / 2.0f;
    b.draw(t, (x+w) - w / 2.0f, 0.0f, -w, h);

    b.end();

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
