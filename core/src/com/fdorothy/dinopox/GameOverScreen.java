package com.fdorothy.dinopox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Input.TextInputListener;

public class GameOverScreen implements Screen {
  final DinoGame game;
  OrthographicCamera camera;
  long time_alive;
  public int entering_name;
  public String name;

  public class NameInputListener implements TextInputListener {
    GameOverScreen screen;
    NameInputListener(GameOverScreen screen) {
      this.screen = screen;
    }

    @Override
    public void input (String text) {
      this.screen.name = text;
      this.screen.entering_name = 0;
    }

    @Override
    public void canceled () {
    }
  }

  public GameOverScreen(final DinoGame game, long time_alive) {
    this.game = game;
    this.time_alive = time_alive;
    camera = new OrthographicCamera();
    camera.setToOrtho(false, game.res.width, game.res.height);
    entering_name = 0;
    name = "";
  }


  @Override
  public void render(float dt) {
    if (name != "") {
      game.setScreen(new MainMenuScreen(game));
    }

    Gdx.gl.glClearColor(0, 0.15f, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    camera.update();
    game.res.batch.setProjectionMatrix(camera.combined);

    game.res.batch.begin();
    game.res.font.draw(game.res.batch, "GAME OVER", game.res.width/2.0f, game.res.height-20.0f);

    int seconds = (int)time_alive % 60;
    int minutes = (int)(time_alive / 60) % 60;
    int hours = (int)(time_alive / 3600);
    game.res.font.draw(game.res.batch, "You survived for: " + hours + ":" + minutes + ":" + seconds, 0, 100);
    game.res.font.draw(game.res.batch, "Touch anywhere to enter your name", 0, 50);

    game.res.batch.end();

    if (Gdx.input.isTouched()) {
      if (entering_name == 0) {
	entering_name = 1;
	NameInputListener listener = new NameInputListener(this);
	Gdx.input.getTextInput(listener, "Enter your name", name, "name");
      }
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
