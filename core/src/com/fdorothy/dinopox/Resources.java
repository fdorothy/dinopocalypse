package com.fdorothy.dinopox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Resources {
  public static final int HARD=0;
  public static final int JUMP=1;
  public static final int PARA=2;
  public static final int SHARK=3;
  public static final int LEE=4;

  public Texture sprites;
  public Texture gameover;
  public Texture place;
  public Texture pickup;
  public Texture cancel;
  public TextureRegion man;
  public TextureRegion bomb;
  public TextureRegion block;
  public static TextureRegion dinos[];
  public TiledMap map;
  public IntMap<TiledMapTile> item_tiles;
  public SpriteBatch batch;
  public SpriteBatch batch_hud;
  public BitmapFont font;
  public ShapeRenderer shapeRenderer;
  public int width, height;

  public Resources() {
    map = new TmxMapLoader().load("map.tmx");
    sprites = new Texture("tiles.png");
    gameover = new Texture("gameover.png");
    man = new TextureRegion(sprites, 160, 128, 32, 64);
    bomb = new TextureRegion(sprites, 192, 256, 32, 64);
    item_tiles = new IntMap<TiledMapTile>();
    item_tiles.put(Item.BOMB, map.getTileSets().getTile(Item.BOMB));
    item_tiles.put(Item.BLOCK, map.getTileSets().getTile(Item.BLOCK));
    block = new TextureRegion(sprites, 192, 320, 32, 32);
    dinos = new TextureRegion[5];
    place = new Texture("place.png");
    pickup = new Texture("pickup.png");
    cancel = new Texture("cancel.png");
    dinos[HARD] = new TextureRegion(sprites, 0, 192, 160, 64);
    dinos[JUMP] = new TextureRegion(sprites, 160, 192, 64, 64);
    dinos[PARA] = new TextureRegion(sprites, 0, 128, 160, 64);
    dinos[SHARK] = new TextureRegion(sprites, 128, 64, 160, 64);
    dinos[LEE] = new TextureRegion(sprites, 0, 0, 128, 128);
    batch = new SpriteBatch();
    batch_hud = new SpriteBatch();
    shapeRenderer = new ShapeRenderer();
    font = new BitmapFont();
    width = Gdx.graphics.getWidth();
    height = Gdx.graphics.getHeight();
  }

  public void dispose() {
    //man.dispose();
    map.dispose();
    font.dispose();
    batch.dispose();
    batch_hud.dispose();
  }
}
