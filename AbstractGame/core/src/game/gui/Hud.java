package game.gui;

import game.MyConst;
import game.objects.Updatable;
import game.states.PlayState;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;

public class Hud implements Updatable{
	
	public static final int SCALE = 12;
    public static final float HUD_WIDTH = 400;
    public static final float HUD_HEIGHT = 400;
    
    public static float WIDTH;
    public static float HEIGHT;
   
    private PlayState state;

    
    private OrthographicCamera camera;
    private boolean open=false;
    
    public Hud(PlayState state) {
        this.state=state;
    }
    
    @Override
    public void update(float delta) {
    	// TODO Auto-generated method stub
    	
    }
    
    
    
    public void draw(ShapeRenderer shapes, SpriteBatch batch){
    	
    	Matrix4 uiMatrix = state.getCamera().combined.cpy();
        uiMatrix.setToOrtho2D(0, 0, MyConst.APP_WIDTH, MyConst.APP_WIDTH);
        batch.setProjectionMatrix(uiMatrix);
        shapes.setProjectionMatrix(uiMatrix);
        
    	
        shapes.begin(ShapeType.Filled);
        	shapes.setColor(Color.CYAN);
    		shapes.rect(0, 0, 100,100);
    	shapes.end();
    	
    	batch.begin();
    	
    	batch.end();
    }
    
  
	
	public void drawHealth(){
		
	}
	
	public void drawMouseLoc(){
		
	}
	
	public void drawGunAndAmmo(){
		
	}
	
	public void drawInventory(){
		
	}
	
	public void drawMinimap(){
		
	}

	

}
