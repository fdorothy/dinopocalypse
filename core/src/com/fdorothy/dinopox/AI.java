package com.fdorothy.dinopox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.ai.pfa.GraphPath;

public class AI {
  public Map map;
  public Array <Dino> dinos;
  public Vector3 objective;
  public float t;
  public int dino_index;

  public final int UPDATES_PER_SECOND = 1;

  public AI(Map map) {
    dinos = new Array <Dino>();
    objective = new Vector3(map.width / 2.0f * 32.0f, map.height / 2.0f * 32.0f, 0.0f);
    reset(map);
  }

  public void reset(Map map) {
    this.dinos.clear();
    this.map = map;
    this.dino_index = 0;
    this.t = 0.0f;
  }

  public void manage(Dino dino) {
    dinos.add(dino);
  }

  public void update(float dt) {
    t += dt;

    // only update so many a* paths per second
    // for our dinos so that we do not get
    // overloaded
    float freq = 1.0f / UPDATES_PER_SECOND;
    while (t > freq) {
      Dino dino = next_dino_to_update();
      if (dino != null) {
        if (dino.state == Dino.STATE_ALIVE) {
          update_path(next_dino_to_update());
        } else {
          dinos.removeIndex(dino_index);
          dino_index = 0;
        }
      }
      t -= freq;
    }

    // force the dinos to follow a path
    for (int i=0; i<dinos.size; i++) {
      follow_path(dinos.get(i));
    }
  }

  public Dino next_dino_to_update() {
    dino_index++;
    if (dino_index >= dinos.size) {
      dino_index = 0;
    }
    if (dino_index >= dinos.size)
      return null;
    else
      return dinos.get(dino_index);
  }

  public void update_path(Dino dino) {
    dino.pathIndex = 0;
    dino.objective = null;
    map.find_path(dino.pos, objective, dino.path);
  }

  public void follow_path(Dino dino) {
    // update the dino's objective based on the current path
    if (dino.pathIndex < dino.path.getCount()) {
      if (dino.objective == null && dino.path != null) {
        dino.pathIndex = 0;
        next_path_node(dino);
      }
      else if (objective != null && dino.pos.dst(dino.objective.x*32, dino.objective.y*32, 0.0f) < 16.0f) {
        next_path_node(dino);
      }
    }
  }

  public void next_path_node(Dino dino) {
    if (dino.pathIndex < dino.path.getCount()) {
      dino.objective = dino.path.get(dino.pathIndex++);
      float obj_x = dino.objective.x*32.0f;
      float obj_y = dino.objective.y*32.0f;
      float d = dino.pos.dst(obj_x, obj_y, 0.0f);
      dino.set_destination((obj_x - dino.pos.x) / d, (obj_y - dino.pos.y) / d);
    } else {
      dino.set_destination(0.0f, 0.0f);
    }
  }
}
