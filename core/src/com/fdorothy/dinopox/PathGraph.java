package com.fdorothy.dinopox;

import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;

public class PathGraph implements IndexedGraph<PathNode> {
  public PathNode nodes[];
  public int width, height;

  public PathGraph(int width, int height) {
    this.width = width;
    this.height = height;
    nodes = new PathNode[width*height];
    for (int i=0; i<width; i++) {
      for (int j=0; j<height; j++) {
        int index = j*width+i;
        PathNode n = new PathNode(i, j, index);
        nodes[index] = n;
      }
    }

    //  connect all of the nodes
    for (int i=1; i<width-1; i++) {
      for (int j=1; j<height-1; j++) {
        PathNode n = at(i,j);
        if (n != null) {
          PathNode n_l = at(i-1,j);
          PathNode n_r = at(i+1,j);
          PathNode n_u = at(i,j-1);
          PathNode n_d = at(i,j+1);
          if (n_l != null) n.addConn(n_l);
          if (n_r != null) n.addConn(n_r);
          if (n_u != null) n.addConn(n_u);
          if (n_d != null) n.addConn(n_d);
        }
      }
    }
  }

  public PathNode at(int i, int j) {
    if (i >= 0 && j >= 0 && i <= width && j <= height) {
      return nodes[i+j*width];
    } else {
      return null;
    }
  }

  public Array<Connection<PathNode>> getConnections(PathNode node) {
    return node.conns;
  }

  @Override
  public int getNodeCount() {
    return nodes.length;
  }

  @Override
  public int getIndex(PathNode node) {
    return node.index;
  }
}
