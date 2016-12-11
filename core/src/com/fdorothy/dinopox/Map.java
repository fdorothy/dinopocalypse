package com.fdorothy.dinopox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import java.lang.Float;

public class Map {
  public TiledMap map;
  public OrthogonalTiledMapRenderer renderer;
  public final float unitScale = 1.0f;
  public final float TILE_WIDTH = 32.0f;
  public TiledMapTileLayer groundLayer;
  public TiledMapTileLayer blocksLayer;
  public TiledMapTileLayer brushLayer;
  public int width;
  public int height;
  public PathHeuristic heuristic;
  public IndexedAStarPathFinder pathFinder;
  public float blockHealth[];

  // path finding related
  PathGraph graph;

  public Map(TiledMap map) {
    this.map = map;
    groundLayer = (TiledMapTileLayer)map.getLayers().get("ground");
    blocksLayer = (TiledMapTileLayer)map.getLayers().get("blocks");
    brushLayer = (TiledMapTileLayer)map.getLayers().get("brush");
    renderer = new OrthogonalTiledMapRenderer(this.map, unitScale);
    width = groundLayer.getWidth();
    height = groundLayer.getHeight();
    blockHealth = new float[width*height];
    for (int i=0; i<width*height; i++) blockHealth[i] = 5.0f;
    generate_path_graph();
  }

  public int get_item(float x, float y) {
    return get_item(x_to_tile(x), y_to_tile(y));
  }

  public int get_item(int i, int j) {
    Cell cell = blocksLayer.getCell(i, j);
    if (cell != null && cell.getTile() != null)
      return cell.getTile().getId();
    else
      return Item.VOID;
  }

  public void hit_block(Vector3 pos, float damage) {
    int i = x_to_tile(pos.x);
    int j = y_to_tile(pos.y);
    int idx = i+j*width;
    blockHealth[idx] -= damage;
    if (blockHealth[idx] <= 0.0f)
      set_item(i, j, Item.VOID);
  }

  public void set_item(float x, float y, int item_id) {
    set_item(x_to_tile(x), y_to_tile(y), item_id);
  }

  public void set_item(int i, int j, int item_id) {
    if (item_id == Item.VOID) {
      blocksLayer.setCell(i, j, null);
      blockHealth[i+j*width] = 0.0f;
    } else {
      Cell cell = new Cell();
      cell.setTile(map.getTileSets().getTile(item_id));
      blocksLayer.setCell(i, j, cell);
      blockHealth[i+j*width] = 5.0f;
    }
  }

  public int x_to_tile(float x) {
    return (int)(x/TILE_WIDTH);
  }

  public int y_to_tile(float y) {
    return (int)(y/TILE_WIDTH);
  }

  public boolean is_blocked(Vector3 pos) {
    return is_blocked(pos.x, pos.y);
  }

  public boolean is_blocked(float x, float y) {
    Cell cell = blocksLayer.getCell(x_to_tile(x), y_to_tile(y));
    if (cell != null)
      return cell.getTile().getId() == Item.BLOCK;
    return false;
  }

  public boolean inBounds(Vector3 pos) {
    return pos.x >= 0 && pos.y >= 0 && pos.x <= width * TILE_WIDTH && pos.y <= height * TILE_WIDTH;
  }

  public void generate_path_graph() {
    graph = new PathGraph(width, height);
    heuristic = new PathHeuristic();
    for (int i=0; i<width; i++) {
      for (int j=0; j<height; j++) {
        if (get_item(i, j) == Item.BLOCK)
          graph.set_cost(i, j, 10.0f);
        else
          graph.set_cost(i, j, 1.0f);
      }
    }
    pathFinder = new IndexedAStarPathFinder<PathNode>(graph, true);
  }

  public void find_path(Vector3 src, Vector3 dst, GraphPath <PathNode> outPath) {
    find_path(x_to_tile(src.x),
              y_to_tile(src.y),
              x_to_tile(dst.x),
              y_to_tile(dst.y),
              outPath);
  }

  public void find_path(int src_i, int src_j, int dst_i, int dst_j, GraphPath <PathNode> outPath) {
    PathNode src_n = graph.at(src_i, src_j);
    PathNode dst_n = graph.at(dst_i, dst_j);
    if (src_n != null && dst_n != null)
      pathFinder.searchNodePath(src_n, dst_n, heuristic, outPath);
  }
}
