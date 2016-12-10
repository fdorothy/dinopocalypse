package com.fdorothy.dinopox;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector3;

public class DinoGame extends Game {
  public Resources res;

  @Override
  public void create () {
    res = new Resources();
    this.setScreen(new MainMenuScreen(this));
  }
  
  @Override
  public void render () {
    super.render();
  }
	
  @Override
  public void dispose () {
    res.dispose();
  }
}
