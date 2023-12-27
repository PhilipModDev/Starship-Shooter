package com.engine.starship;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL30,3,2);
		config.setIdleFPS(60);
		config.setForegroundFPS(144);
		config.setWindowedMode(1080,720);
		config.setWindowIcon("textures/starship.png");
		config.setTitle("Starship Shooter");
		new Lwjgl3Application(new StarshipShooter(), config);
	}
}
