package com.fdorothy.dinopox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.ai.pfa.GraphPath;

public class Dino extends Sprite {
  public static final int STATE_VOID=0;
  public static final int STATE_SPAWNING=1;
  public static final int STATE_ALIVE=2;
  public static final int STATE_DEAD=3;

  //  0 = void, 1 = spawning, 2 = alive, 3 = dead
  public int state;
  public float spawn_timer;
  public Map map;
  public int species;
  public GraphPath <PathNode> path;
  public PathNode objective;
  public int pathIndex;
  public float speed;

  Dino(Map map) {
    this.map = map;
    set_species(Resources.HARD);
    reset();
    pathIndex = 0;
  }

  public void set_species(int species) {
    this.species = species;
    switch (species) {
    case Resources.HARD:
      speed = 1 * 32.0f;
      img = Resources.dinos[species];
      break;
    }
  }

  public void reset() {
    state = 0;
    pathIndex = 0;
    spawn_timer = 0.0f;
    objective = null;
  }

  public void update(float dt) {
    if (spawn_timer > 0.0f) {
      spawn_timer -= dt;
    }
    else if (state == STATE_SPAWNING) {
      state = STATE_ALIVE;
    } else if (state == STATE_DEAD) {
      state = STATE_VOID;
    } else {
      float speedFactor = 1.0f;
      if (map.is_blocked(pos)) { speedFactor = 0.25f; }
      move(dt, speedFactor * speed);
    }
  }

  public void spawn(float x, float y) {
    pos.set(x, y, 0.0f);
    delta.set(0.0f, 0.0f, 0.0f);
    this.state = 1;
    this.spawn_timer = 3.0f;
  }

  public void shoot() {
    if (state == 2) {
      state = STATE_DEAD;
      spawn_timer = 10.0f;
      delta.set(0.0f,0.0f,0.0f);
    }
  }
}
