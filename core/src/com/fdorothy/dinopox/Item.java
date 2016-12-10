package com.fdorothy.dinopox;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.MathUtils;

public class Item {
  public enum ItemState {
    NONE,
    VISIBLE
  }

  public enum ItemType {
    VOID,
    BOMB,
    BLOCK,
    LIFE
  }

  public Vector3 pos;
  public ItemState state;
  public ItemType type;
  public float availableTime;
  public Map map;

  Item(Map map) {
    pos = new Vector3();
    state = ItemState.NONE;
    type = ItemType.BOMB;
    availableTime = 0.0f;
    this.map = map;
  }

  void update(float dt) {
    availableTime -= dt;

    if (availableTime <= 0.0f) {
      pos.set((float)(MathUtils.random.nextDouble() * map.width * map.TILE_WIDTH),
	      (float)(MathUtils.random.nextDouble() * map.height * map.TILE_WIDTH),
	      0.0f);
      double p = MathUtils.random.nextDouble();
      if (p < 0.75)
        spawn(pos, ItemType.BOMB);
      else
        spawn(pos, ItemType.LIFE);
    }
  }

  void spawn(Vector3 pos, ItemType type) {
    this.pos.set(pos);
    this.type = type;
    this.state = ItemState.VISIBLE;
    this.availableTime = 30.0f;
  }

  void pickup() {
    this.state = ItemState.NONE;
    availableTime = 30.0f;
  }
}
