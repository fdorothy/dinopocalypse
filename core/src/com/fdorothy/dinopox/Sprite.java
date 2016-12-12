package com.fdorothy.dinopox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;

public class Sprite {
  public TextureRegion img;
  public Vector3 pos;
  public Vector3 delta;
  public Vector3 facing;

  // path finding stuff
  public GraphPath <PathNode> path;
  public PathNode objective;
  public int pathIndex;

  // shared vars to avoid malloc / gc issues on each frame
  public static Vector3 new_pos;

  public Sprite() {
    path = new DefaultGraphPath <PathNode>();
    pos = new Vector3();
    delta = new Vector3();
    facing = new Vector3(1, 0, 0);
  }

  public void reset() {
    pathIndex = 0;
    pos.set(0.0f, 0.0f, 0.0f);
    delta.set(0.0f, 0.0f, 0.0f);
    facing.set(0.0f, 0.0f, 0.0f);
    path.clear();
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

  public void draw_box(ShapeRenderer renderer) {
    int tile_x = (int)(pos.x/32.0f);
    int tile_y = (int)(pos.y/32.0f);
    renderer.rect(tile_x*32, tile_y*32, 32.0f, 32.0f);
  }

  public void update(float dt) {
    follow_path();
    move_to_objective();
  }

  public void follow_path() {
    // update the sprite's path based on the current path
    if (path.getCount() > 0) {
      if (objective != null) {
        if (pos.dst(objective.x*32 + 16, objective.y*32 + 16, 0.0f) < 8.0f)
          next_path_node();
      } else {
        pathIndex = 0;
        next_path_node();
      }
    }
  }

  public void next_path_node() {
    if (pathIndex < path.getCount()) {
      objective = path.get(pathIndex++);
    } else
      stop();
  }

  public void stop() {
    path.clear();
    objective = null;
    set_destination(0.0f, 0.0f);
  }

  public void move_to_objective() {
    if (objective != null) {
      float obj_x = objective.x*32.0f + 16.0f;
      float obj_y = objective.y*32.0f + 16.0f;
      float d = pos.dst(obj_x, obj_y, 0.0f);
      if (d > 1.0f)
        set_destination((obj_x - pos.x) / d, (obj_y - pos.y) / d);
    }
  }
}
