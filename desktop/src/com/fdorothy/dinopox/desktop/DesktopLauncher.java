package com.fdorothy.dinopox.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fdorothy.dinopox.DinoGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.width = 1024;
    config.height = 768;
		new LwjglApplication(new DinoGame(), config);
	}
}
