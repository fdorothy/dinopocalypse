package com.fdorothy.dinopox;

import com.badlogic.gdx.math.Vector3;

public class Dino {
  public static final int STATE_VOID=0;
  public static final int STATE_SPAWNING=1;
  public static final int STATE_ALIVE=2;
  public static final int STATE_DEAD=3;

  public Vector3 pos;
  public Vector3 new_pos;
  public Vector3 delta;
  public Vector3 facing;

  //  0 = void, 1 = spawning, 2 = alive, 3 = dead
  public int state;
  public float spawn_timer;
  public Map map;
  public int species;

  Dino(Map map) {
    this.map = map;
    this.species = Resources.HARD;
    reset();
  }

  public void reset() {
    state = 0;
    spawn_timer = 0.0f;
    pos = new Vector3();
    delta = new Vector3();
    new_pos = new Vector3();
    facing = new Vector3(1, 0, 0);
  }

  public void update(float dt) {
    if (spawn_timer > 0.0f) {
      spawn_timer -= dt;
    }
    else if (state == 1) {
      state = 2;
    } else if (state == 3) {
      state = 0;
    } else {
      float speedFactor = 1.0f;
      if (map.is_blocked(pos)) { speedFactor = 0.25f; }
      new_pos.x = pos.x + delta.x*dt*speedFactor;
      new_pos.y = pos.y + delta.y*dt*speedFactor;
      if (map.inBounds(new_pos)) {
        set_pos(new_pos.x, new_pos.y);
      }
    }
  }

  public void set_pos(float x, float y) {
    if (x > pos.x)
      facing.x = 1;
    else if (x < pos.x)
      facing.x = -1;
    if (y > pos.y)
      facing.y = 1;
    else if (y < pos.y)
      facing.y = -1;
    pos.x = x;
    pos.y = y;
  }

  public void spawn(float x, float y) {
    pos.set(x, y, 0.0f);
    delta.set(0.0f, 0.0f, 0.0f);
    this.state = 1;
    this.spawn_timer = 3.0f;
  }

  public void shoot() {
    if (state == 2) {
      state = 3;
      spawn_timer = 10.0f;
      delta.set(0.0f,0.0f,0.0f);
    }
  }

  public void set_destination(float dx, float dy) {
    delta.set(dx,dy,0.0f);
  }
}
