package com.fdorothy.dinopox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

public class Survivor extends Sprite {
  public int item;
  public Bullet bullet;

  // after moving we try to interact with this location
  public boolean interact;
  public Vector3 interact_pos;

  public Survivor() {
    interact_pos = new Vector3();
    interact = false;
    item = Item.VOID;
    bullet = new Bullet();
  }

  public void update(Map map, float dt) {
    super.update(dt);
    move(dt, 128.0f);
    if (interact && pos.dst(interact_pos) < 32.0f) {
      stop();
      action(map, interact_pos.x, interact_pos.y);
    }
  }

  @Override
  public void stop() {
    super.stop();
    interact = false;
  }

  public void action(float x, float y) {
    interact_pos.set(x, y, 0.0f);
    interact = true;
  }

  public void action(Map map) {
    float x = pos.x + facing.x * 30.0f;
    float y = pos.y + facing.y * 30.0f;
    action(map, x, y);
  }

  public void action(Map map, float x, float y) {
    int ground_item = map.get_item(x, y);
    if (item == Item.VOID) {
      // pick up!
      if (ground_item != Item.VOID) {
        map.set_item(x, y, Item.VOID);
        item = ground_item;
        Gdx.app.log("survivor", "ok, picked up a " + item);
      }
    } else if (ground_item == Item.VOID) {
      // throw down!
      if (item != Item.VOID) {
        map.set_item(x, y, item);
        item = Item.VOID;
      }
    }
  }

  public void reset() {
    bullet.inplay = false;
    super.reset();
  }
}
