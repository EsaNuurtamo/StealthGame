package game.desktop;

import game.Main;
import game.MyConst;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width=MyConst.APP_WIDTH;
		config.height=MyConst.APP_HEIGHT;
		config.vSyncEnabled=true;
		config.fullscreen=true;
		Settings settings = new Settings();
        
        //TexturePacker.process(settings, "/Users/esa/Desktop/LIBGDX/AbstractGame/android/assets/images", 
        //								"/Users/esa/Desktop/LIBGDX/AbstractGame/android/assets/images", "game");

    
		new LwjglApplication(new Main(), config);
	}
}
