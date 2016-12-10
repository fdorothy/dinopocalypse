package com.fdorothy.dinopox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

public class Survivor {
  public int life;
  public int ammo;
  public Item.ItemType holding;
  public Cell holding_cell;
  public Bullet bullet;
  Vector3 pos;
  Vector3 facing;

  public Survivor() {
    pos = new Vector3();
    pos.x = 0.0f;
    pos.y = 0.0f;
    life = 5;
    ammo = 5;
    holding = Item.ItemType.VOID;
    facing = new Vector3(1, 0, 0);
    bullet = new Bullet();
  }

  public void set_pos(float x, float y) {
    if (x > pos.x)
      facing.x = 1;
    else if (x < pos.x)
      facing.x = -1;
    else
      facing.x = 0;
    if (y > pos.y)
      facing.y = 1;
    else if (y < pos.y)
      facing.y = -1;
    else
      facing.y = 0.0f;
    pos.x = x;
    pos.y = y;
  }

  public void action(Map map) {
    float x = pos.x + facing.x * 30.0f;
    float y = pos.y + facing.y * 30.0f;
    int tx = map.x_to_tile(x);
    int ty = map.y_to_tile(y);
    Cell cell = map.blocksLayer.getCell(tx, ty);
    if (holding == Item.ItemType.VOID) {

      // pick up!
      if (cell != null) {
        map.blocksLayer.setCell(tx, ty, null);
        holding = Item.ItemType.BLOCK;
        holding_cell = cell;
      }
      else {
        Gdx.app.log("survivor", "shooting");
        bullet.shoot(pos, facing);
      }

    } else if (cell == null) {
      // throw down maybe?
      if (holding == Item.ItemType.BLOCK) {
        map.blocksLayer.setCell(tx, ty, holding_cell);
        holding = Item.ItemType.VOID;
        holding_cell = null;
      }
    }
  }

  public void reset() {
    life = 5;
    ammo = 5;
    pos.set(0.0f, 0.0f, 0.0f);
    bullet.inplay = false;
  }
}
