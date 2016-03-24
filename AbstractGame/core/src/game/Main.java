package game;

	
	
import game.states.MenuState;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

/**
 * This is games main class. It acts asstate/"screen" manager.
 * Class is capsulated to all to game states classes so state can be changed easily
 * (Maybe later some permanent stats that carrie to the next level could be stored here or some kind of character control might be useful)
 */
public class Main extends Game {
    
	
    @Override
    public void create() {
    	MyConst.createSkin();
    	Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);
    
    		for(String s:Content.music.keySet()){
    			Content.music.get(s).play();
    		}  
        setScreen(new MenuState(this));
    }
    
    
	
}
