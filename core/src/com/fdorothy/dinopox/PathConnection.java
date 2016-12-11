package com.fdorothy.dinopox;
import com.badlogic.gdx.ai.pfa.Connection;

public class PathConnection implements Connection<PathNode> {
  protected PathNode src;
  protected PathNode dst;

  public PathConnection(PathNode src, PathNode dst) {
    this.src = src;
    this.dst = dst;
  }
  @Override
  public float getCost() { return dst.cost; }
  @Override
  public PathNode getFromNode() {return src;}
  @Override
  public PathNode getToNode() {return dst;}
}
