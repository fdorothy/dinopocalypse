package com.fdorothy.dinopox;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

public class PathNode {
  public Array<Connection<PathNode>> conns = new Array<Connection<PathNode>>();
  public int x, y, index;
  public float cost;

  public PathNode(int x, int y, int index) {
    this.x = x;
    this.y = y;
    this.index = index;
  }

  public void addConn(PathNode other) {
    if (other != null)
      conns.add(new PathConnection(this, other));
  }
}
