package com.fdorothy.dinopox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Sprite {
  public TextureRegion img;
  public Vector3 pos;
  public Vector3 delta;
  public Vector3 facing;

  // shared vars to avoid malloc / gc issues on each frame
  public static Vector3 new_pos;

  public Sprite() {
    pos = new Vector3();
    delta = new Vector3();
    facing = new Vector3(1, 0, 0);
  }

  public void reset() {
    pos.set(0.0f, 0.0f, 0.0f);
    delta.set(0.0f, 0.0f, 0.0f);
    facing.set(0.0f, 0.0f, 0.0f);
  }

  public void move(float dt, float distance) {
    set_pos(pos.x + delta.x*distance*dt, pos.y + delta.y*distance*dt);
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

  public void set_destination(float dx, float dy) {
    delta.set(dx,dy,0.0f);
  }

  public void draw(SpriteBatch batch) {
    if (img != null) {
      float w = img.getRegionWidth();
      float h = img.getRegionHeight();
      if (facing.x > 0)
        batch.draw(img, pos.x - w / 2.0f, pos.y);
      else
        batch.draw(img, (pos.x+w) - w / 2.0f, pos.y, -w, h);
    }
  }
}
