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
    objective = new Vector3(40 * 32.0f, 38 * 32.0f, 0.0f);
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

    // clear out dead dinos
    for (int i=0; i<dinos.size; i++) {
      int state = dinos.get(i).state;
      if (state != Dino.STATE_ALIVE && state != Dino.STATE_SPAWNING) {
        dinos.removeIndex(i);
        i--;
      }
    }

    // only update so many a* paths per second
    // for our dinos so that we do not get
    // overloaded
    float freq = 1.0f / UPDATES_PER_SECOND;
    while (t > freq) {
      Dino dino = next_dino_to_update();
      if (dino != null)
        update_path(dino);
      t -= freq;
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
    dino.stop();
    map.find_path(dino.pos, objective, dino.path);
  }
}
