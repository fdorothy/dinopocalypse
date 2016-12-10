package com.fdorothy.dinopox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
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
    generate_path_graph();
  }

  public boolean is_blocked(Vector3 pos) {
    return is_blocked(pos.x, pos.y);
  }

  public int x_to_tile(float x) {
    return (int)(x/TILE_WIDTH);
  }

  public int y_to_tile(float y) {
    return (int)(y/TILE_WIDTH);
  }

  public boolean is_blocked(float x, float y) {
    Cell cell = blocksLayer.getCell(x_to_tile(x), y_to_tile(y));
    return cell != null;
  }

  public boolean inBounds(Vector3 pos) {
    return pos.x >= 0 && pos.y >= 0 && pos.x <= width * TILE_WIDTH && pos.y <= height * TILE_WIDTH;
  }

  public void generate_path_graph() {
    graph = new PathGraph(width, height);
  }
}
