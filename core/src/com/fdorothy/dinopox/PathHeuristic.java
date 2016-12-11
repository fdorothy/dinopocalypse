package com.fdorothy.dinopox;
import com.badlogic.gdx.ai.pfa.Heuristic;

public class PathHeuristic implements Heuristic<PathNode> {
  @Override
  public float estimate(PathNode src, PathNode dst) {
    // manhattan distance
    return Math.abs(dst.x - src.x) + Math.abs(dst.y - src.y);
  }
}
