package com.fdorothy.dinopox;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameScreen implements Screen {
  Survivor survivor;
  Array<Dino> dinos;
  Array<Item> items;
  int wave;
  Map map;
  long last_millis;
  long start_millis;

  OrthographicCamera camera;
  OrthographicCamera camera_hud;

  final DinoGame game;

  public GameScreen(final DinoGame game) {
    this.game = game;

    last_millis = TimeUtils.millis();
    start_millis = last_millis;

    camera = new OrthographicCamera();
    camera.setToOrtho(false, game.res.width, game.res.height);
    camera.zoom = 1.0f;

    camera_hud = new OrthographicCamera();
    camera_hud.setToOrtho(false, game.res.width, game.res.height);

    map = new Map(game.res.map);

    survivor = new Survivor();
    dinos = new Array<Dino>();
    for (int i=0; i<100; i++) {
      dinos.add(new Dino(map));
    }
    items = new Array<Item>();
    for (int i=0; i<20; i++) {
      items.add(new Item(map));
    }

    new_game();
  }
  
  @Override
  public void render (float dt) {
    last_millis = TimeUtils.millis();
    update(dt);

    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    draw_board(dt);
    draw_hud();
    process_input(dt);
  }

  public void draw_board(float dt) {
    camera.position.set(survivor.pos);
    camera.update();
    game.res.batch.setProjectionMatrix(camera.combined);

    map.renderer.setView(camera);
    map.renderer.render();

    game.res.batch.begin();
    draw_avatar(game.res.man, survivor.pos, survivor.facing.x);

    for (int i=0; i<100; i++) {
      Dino z = dinos.get(i);
      if (z.state == Dino.STATE_ALIVE) {
        draw_avatar(game.res.dinos[z.species], z.pos, 1);
      } else if (z.state == 1) {
      } else if (z.state == 3) {
        //draw_avatar(game.res.dead, z.pos);
      }
    }

    game.res.batch.end();

    // draw the bullet
    ShapeRenderer r = game.res.shapeRenderer;
    r.setProjectionMatrix(camera.combined);
    Bullet b = survivor.bullet;
    if (b.inplay) {
      r.begin(ShapeRenderer.ShapeType.Filled);
      r.setColor(1, 0, 0, 1);
      r.rectLine(b.pos.x, b.pos.y, b.pos.x-b.dir.x * 10, b.pos.y-b.dir.y * 10, 4);
      r.end();
    }
    r.end();
  }

  public void draw_hud() {
    camera_hud.update();
    game.res.batch_hud.setProjectionMatrix(camera_hud.combined);

    game.res.batch_hud.begin();

    // time-alive
    int time_alive = (int)((last_millis - start_millis) / 1000);
    int seconds = (int)time_alive % 60;
    int minutes = (int)(time_alive / 60) % 60;
    int hours = (int)(time_alive / 3600);
    game.res.font.draw(game.res.batch_hud, "" + hours + ":" + minutes + ":" + seconds, game.res.width/2.0f, game.res.height-20);

    game.res.font.draw(game.res.batch_hud, "wave " + wave, game.res.width/2.0f, game.res.height-40);

    game.res.batch_hud.end();
  }

  public void update(float dt) {
    for (int i=0; i<dinos.size; i++) {
      dinos.get(i).update(dt);
    }
    for (int i=0; i<items.size; i++) {
      items.get(i).update(dt);
    }
    man_update();
  }

  public void process_input(float dt) {

    // update the man's location (cannot move over blocks)
    float dx = 0.0f;
    float dy = 0.0f;
    boolean moved = false;
    if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP)) {
      dy += dt * 32.0 * 4;
      moved = true;
    }
    if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN)) {
      dy -= dt * 32.0 * 4;
      moved = true;
    }
    if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) {
      dx -= dt * 32.0 * 4;
      moved = true;
    }
    if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
      dx += dt * 32.0 * 4;
      moved = true;
    }
    if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
      survivor.action(map);
    }
    if (moved) {
      float new_x = survivor.pos.x + dx;
      float new_y = survivor.pos.y + dy;
      if (!map.is_blocked(new_x, new_y)) {
        survivor.set_pos(new_x, new_y);
      }
    }

    if (Gdx.input.justTouched()) {
      int x = Gdx.input.getX();
      int y = Gdx.input.getY();
      Vector3 cur = camera.unproject(new Vector3((float)x, (float)y, 0.0f));
      if (survivor.ammo > 0) {
        float min_d=100.0f;
        Dino min_z = null;
        for (int i=0; i<100; i++) {
          Dino z = dinos.get(i);
          if (z.state == 2) {
            float d = cur.dst(z.pos);
            if (d < min_d) {
              min_d = d;
              min_z = z;
            }
          }
        }
        if (min_z != null && min_d < 32.0f) {
          min_z.shoot();
          //survivor.ammo--;
        }
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

  public void new_game() {
    for (int i=0; i<100; i++) {
      dinos.get(i).reset();
    }
    survivor.reset();
    survivor.pos.x = (map.width+1.5f) / 2.0f * 32.0f;
    survivor.pos.y = (map.height+1.5f) / 2.0f * 32.0f;
    wave = 1;
    spawn();
  }

  public void game_over() {
    game.setScreen(new GameOverScreen(game, (last_millis - start_millis) / 1000));
  }

  void draw_avatar(TextureRegion image, Vector3 pos, float facing) {
    if (facing > 0)
      game.res.batch.draw(image, pos.x - image.getRegionWidth() / 2.0f, pos.y);
    else {
      float w = image.getRegionWidth();
      float h = image.getRegionHeight();
      game.res.batch.draw(image, (pos.x+w) - w / 2.0f, pos.y, -w, h);
    }
  }

  void man_update() {
    // have we been hit?
    Dino z = null;
    for (int i=0; i<dinos.size; i++) {
      z = dinos.get(i);
      if (z.state == 2) {
        float d = z.pos.dst(survivor.pos);
        if (d < 32) {
          game_over();
        }

        // shot anything?
        if (survivor.bullet.inplay) {
          d = z.pos.dst(survivor.bullet.pos);
          if (d < 32) {
            z.shoot();
            survivor.bullet.inplay = false;
          } else if (!map.inBounds(survivor.bullet.pos)) {
            survivor.bullet.inplay = false;
          } else if (map.is_blocked(survivor.bullet.pos)) {
            survivor.bullet.inplay = false;
          }

        }
      }
    }
    // update bullet
    if (survivor.bullet.inplay) {
      survivor.bullet.pos.x += survivor.bullet.dir.x * 16;
      survivor.bullet.pos.y += survivor.bullet.dir.y * 16;
    }
  }

  void edge_spawn(Dino dino)
  {
    Gdx.app.log("spawn", "spawned a dino on the edge");
    int edge = MathUtils.random.nextInt(4);
    float t = MathUtils.random.nextInt(100) / 100.0f;
    if (edge == 0) {
      dino.spawn(16.0f, t * map.height * 32.0f);
    }
    else if (edge == 1) {
      dino.spawn(t * map.width * 32.0f, 16.0f);
    }
    else if (edge == 2) {
      dino.spawn(map.width * 32.0f - 16.0f, t * map.height * 32.0f);
    }
    else if (edge == 3) {
      dino.spawn(t * map.width * 32.0f, map.height * 32.0f - 16.0f);
    }
  }

  void spawn() {
    Gdx.app.log("spawn", "spawn()");
    // reset all dinos
    for (int i=0; i<dinos.size; i++)
      dinos.get(i).reset();

    final int WAVE_SIZE = 10;
    if (wave == 1) {
      for (int i=0; i<WAVE_SIZE; i++) {
        Dino dino = dinos.get(i);
        dino.species = Resources.HARD;
        edge_spawn(dino);
      }
    } else if (wave == 1) {
    }
    // ...
  }
}
