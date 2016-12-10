package com.fdorothy.dinopox;
import com.badlogic.gdx.math.Vector3;

public class Bullet {
  public boolean inplay;
  public Vector3 pos, dir;

  Bullet() {
    pos = new Vector3();
    dir = new Vector3();
    inplay = false;
  }

  void shoot(Vector3 position, Vector3 direction) {
    if (!inplay) {
      pos.x = position.x;
      pos.y = position.y + 32.0f;
      dir.set(direction);
      inplay = true;
    }
  }
}
