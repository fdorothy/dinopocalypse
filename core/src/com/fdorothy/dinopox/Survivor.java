package com.fdorothy.dinopox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

public class Survivor extends Sprite {
  public int item;
  public Bullet bullet;

  public Survivor() {
    item = Item.VOID;
    bullet = new Bullet();
  }

  public void action(Map map) {
    float x = pos.x + facing.x * 30.0f;
    float y = pos.y + facing.y * 30.0f;
    int ground_item = map.get_item(x, y);
    if (item == Item.VOID) {
      // pick up!
      if (ground_item != Item.VOID) {
        map.set_item(x, y, Item.VOID);
        item = ground_item;
        Gdx.app.log("survivor", "ok, picked up a " + item);
      }
      else {
        bullet.shoot(pos, facing);
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
