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
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameScreen implements Screen {
  Survivor survivor;
  Array<Dino> dinos;
  AI ai;
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
    survivor.img = game.res.man;
    dinos = new Array<Dino>();
    for (int i=0; i<100; i++) {
      dinos.add(new Dino(map));
    }
    ai = new AI(map);

    new_game();
  }
  
  @Override
  public void render (float dt) {
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
    survivor.draw(game.res.batch);

    for (int i=0; i<100; i++) {
      Dino dino = dinos.get(i);
      if (dino.state == Dino.STATE_ALIVE) {
        dino.draw(game.res.batch);
      } else if (dino.state == 1) {
      } else if (dino.state == 3) {
      }
    }

    game.res.batch.end();

    ShapeRenderer r = game.res.shapeRenderer;
    r.setProjectionMatrix(camera.combined);

    // draw the bullet
    Bullet b = survivor.bullet;
    if (b.inplay) {
      r.begin(ShapeRenderer.ShapeType.Filled);
      r.setColor(1, 0, 0, 1);
      r.rectLine(b.pos.x, b.pos.y, b.pos.x-b.dir.x * 10, b.pos.y-b.dir.y * 10, 4);
      r.end();
    }

    // draw the bounding boxes of all sprites, for debug
    r.begin(ShapeRenderer.ShapeType.Line);
    survivor.draw_box(r);
    for (int i=0; i<dinos.size; i++) {
      Dino d = dinos.get(i);
      if (d != null && d.state == Dino.STATE_ALIVE)
        d.draw_box(r);
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
    ai.update(dt);
    for (int i=0; i<dinos.size; i++) {
      dinos.get(i).update(dt);
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
        if (min_z != null && min_d < 32.0f) {
          min_z.shoot();
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

  void man_update() {
    // have we been hit or has a dino reached the flag?
    Dino z = null;
    for (int i=0; i<dinos.size; i++) {
      z = dinos.get(i);
      if (z.state == 2) {

        // dino hit us, we dead
        float d = z.pos.dst(survivor.pos);
        if (d < 32) {
          Gdx.app.log("game over", "dino hit us, game over");
          game_over();
        }

        // dino reached the flag, we dead
        d = z.pos.dst(ai.objective);
        if (d < 32) {
          Gdx.app.log("game over", "dino got the flag, game over");
          game_over();
        }

        // dino hit bomb, everything around is dead
        int tile_x = map.x_to_tile(z.pos.x);
        int tile_y = map.y_to_tile(z.pos.y);
        if (map.get_item(tile_x, tile_y) == Item.BOMB) {
          explode_bomb(tile_x, tile_y);
        }

        // bullet hit anything?
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

  void explode_bomb(int i, int j) {
    map.set_item(i, j, Item.VOID);
    float bomb_x = (i + 0.5f) * 32.0f;
    float bomb_y = (j + 0.5f) * 32.0f;
    if (hit_by_bomb(survivor, bomb_x, bomb_y)) {
      Gdx.app.log("game over", "survivor hit by bomb, game over");
      game_over();
    } else {
      for (int idx=0; idx<dinos.size; idx++) {
        Dino d = dinos.get(idx);
        if (d.state == Dino.STATE_ALIVE)
          if (hit_by_bomb(d, bomb_x, bomb_y)) {
            Gdx.app.log("bomb", "dino hit by bomb");
            d.shoot();
          }
      }
    }
  }

  boolean hit_by_bomb(Sprite sprite, float bomb_x, float bomb_y)
  {
    return sprite.pos.dst(bomb_x, bomb_y, 0.0f) < 2.0f * 32.0f;
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
    dino.path = new DefaultGraphPath <PathNode>();
    ai.manage(dino);
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
        dino.set_species(Resources.HARD);
        edge_spawn(dino);
      }
    } else if (wave == 1) {
    }
    // ...
  }
}
