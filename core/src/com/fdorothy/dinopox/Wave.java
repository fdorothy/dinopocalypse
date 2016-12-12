package com.fdorothy.dinopox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;

public class Wave {
  public float wave_countdown;
  public int wave;
  public int state;
  public static final int STATE_PREPARE = 1;
  public static final int STATE_ATTACK = 2;
  public static final int STATE_WON = 3;
  public static int TOTAL_WAVES = 10;
  public static int PREPARE_TIME = 0;
  public static final int WAVE_SIZE = 10;

  AI ai;
  Map map;
  Array<Dino> dinos;
  DinoGame game;

  public Wave(DinoGame game, AI ai, Map map, Array<Dino> dinos) {
    this.game = game;
    this.ai = ai;
    this.map = map;
    this.dinos = dinos;
    reset();
  }

  public void reset() {
    wave = 1;
    wave_countdown = PREPARE_TIME;
    state = STATE_PREPARE;
  }

  public void update(float dt) {
    if (state == STATE_PREPARE || state == STATE_WON) {
      wave_countdown -= dt;
      if (wave_countdown <= 0.0f) {
        state = STATE_ATTACK;
        spawn();
      }
    }
    else if (state == STATE_ATTACK) {
      // check if all of the dinos are dead
      if (ai.dinos.size == 0) {
        wave++;
        if (wave >= TOTAL_WAVES) {
          state = STATE_WON;
          wave_countdown = PREPARE_TIME;
          wave = 1;
        } else {
          state = STATE_PREPARE;
          wave_countdown = PREPARE_TIME;
        }
      }
    }
  }

  public int print(SpriteBatch batch, String text, int line) {
    game.res.font.draw(batch, text, 10.0f, game.res.height-20*(line+1));
    return line + 1;
  }

  public void render(SpriteBatch batch) {
    switch (state) {
    case STATE_PREPARE:
      print(batch, "wave " + wave + " starts in " + (int)(wave_countdown) + " seconds", 0);
      break;
    case STATE_ATTACK:
      print(batch, "wave " + wave, 0);
      print(batch, "thunderlizards: " + ai.dinos.size, 1);
      break;
    case STATE_WON:
      print(batch, "you defeated General Ankylee, congratulations!", 0);
      print(batch, "Restart in " + wave_countdown + " seconds", 1);
      break;
    }
  }

  void edge_spawn(Dino dino, float time)
  {
    Gdx.app.log("spawn", "spawned a dino on the edge");
    int edge = MathUtils.random.nextInt(4);
    float t = MathUtils.random.nextInt(100) / 100.0f;
    if (edge == 0) {
      dino.spawn(16.0f, t * map.height * 32.0f, time);
    }
    else if (edge == 1) {
      dino.spawn(t * map.width * 32.0f, 16.0f, time);
    }
    else if (edge == 2) {
      dino.spawn(map.width * 32.0f - 16.0f, t * map.height * 32.0f, time);
    }
    else if (edge == 3) {
      dino.spawn(t * map.width * 32.0f, map.height * 32.0f - 16.0f, time);
    }
    ai.manage(dino);
  }

  int spawn_wave(int species, int offset) {
    return spawn_wave(species, offset, 0.0f);
  }

  int spawn_wave(int species, int offset, float time) {
    Gdx.app.log("wave", "spawning wave of " + species);
    for (int i=0; i<WAVE_SIZE; i++) {
      Dino dino = dinos.get(i + offset);
      dino.set_species(species);
      edge_spawn(dino, time);
    }
    return offset + WAVE_SIZE;
  }

  void spawn() {
    Gdx.app.log("spawn", "spawn()");
    // reset all dinos
    for (int i=0; i<dinos.size; i++)
      dinos.get(i).reset();

    int offset = 0;
    switch (wave) {
    case 1:
      spawn_wave(Resources.HARD, 0);
      break;
    case 2:
      offset = spawn_wave(Resources.HARD, offset);
      offset = spawn_wave(Resources.HARD, offset);
      break;
    case 3:
      offset = spawn_wave(Resources.HARD, offset);
      offset = spawn_wave(Resources.JUMP, offset);
      break;
    case 4:
      offset = spawn_wave(Resources.HARD, offset);
      offset = spawn_wave(Resources.HARD, offset);
      offset = spawn_wave(Resources.JUMP, offset);
      break;
    case 5:
      offset = spawn_wave(Resources.JUMP, offset);
      offset = spawn_wave(Resources.JUMP, offset);
      break;
    case 6:
      offset = spawn_wave(Resources.HARD, offset);
      offset = spawn_wave(Resources.HARD, offset);
      offset = spawn_wave(Resources.HARD, offset);
      break;
    case 7:
      offset = spawn_wave(Resources.PARA, offset, 5.0f);
      break;
    case 8:
      offset = spawn_wave(Resources.HARD, offset);
      offset = spawn_wave(Resources.JUMP, offset);
      offset = spawn_wave(Resources.PARA, offset, 5.0f);
      offset = spawn_wave(Resources.LEE, offset, 15.0f);
      break;
    case 9:
      offset = spawn_wave(Resources.JUMP, offset);
      offset = spawn_wave(Resources.JUMP, offset);
      break;
    }
  }
}
