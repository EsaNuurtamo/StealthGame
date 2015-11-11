package game.gui;

import sun.font.GlyphLayout;
import game.MyConst;
import game.objects.Updatable;
import game.states.PlayState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class Hud implements Updatable{
	
	public static final int SCALE = 12;
    public static final float HUD_WIDTH = 400;
    public static final float HUD_HEIGHT = 400;
    
    public static float WIDTH;
    public static float HEIGHT;
   
    private PlayState state;

    
    private OrthographicCamera camera;
    private boolean open=false;
    private SpriteBatch batch;
    private ShapeRenderer shapes;
    private BitmapFont font;
    public Hud(PlayState state) {
        this.state=state;
        batch=state.getBatch();
        shapes=state.getSrenderer();
        font=MyConst.skin.getFont("scoreFont");
    }
    
    @Override
    public void update(float delta) {
    	// TODO Auto-generated method stub
    	
    }
    
    
    
    public void draw(){
    	
    	Matrix4 uiMatrix = state.getCamera().combined.cpy();
        uiMatrix.setToOrtho2D(0, 0, MyConst.APP_WIDTH,MyConst.APP_HEIGHT);
        batch.setProjectionMatrix(uiMatrix);
        shapes.setProjectionMatrix(uiMatrix);
        
    	
        shapes.begin(ShapeType.Filled);
        	drawHealth();
    	shapes.end();
    	
    	batch.begin();
    		drawGunAndAmmo();
    		drawFps();
    		drawPos();
    		drawNotifications();
    	batch.end();
    }
    
  
	
	public void drawHealth(){
		
		shapes.setColor(Color.ORANGE);
		shapes.rect(0, MyConst.APP_HEIGHT-200-15, 320, 80);
		if(state.getPlayer().getHealth()<=0)return;
		float fraction=state.getPlayer().getHealth()/state.getPlayer().getMaxHealth();
		if(fraction>0.5f){
			shapes.setColor(Color.GREEN);
		}else if(fraction>0.25f){
			shapes.setColor(Color.YELLOW);
		}else{
			shapes.setColor(Color.RED);
		}
		
		shapes.rect(10, MyConst.APP_HEIGHT-200, 300*fraction, 50);
		
	}
	
	public void drawMouseLoc(){
		
	}
	
	public void drawGunAndAmmo(){
		
		font.draw(batch, state.getPlayer().getGun().getClass().getSimpleName(), 10, MyConst.APP_HEIGHT-50);
		font.draw(batch, state.getPlayer().getGun().getInClip()+"/"+state.getPlayer().getGun().getAmmo(), 170, MyConst.APP_HEIGHT-50);
	}
	
	public void drawInventory(){
		
	}
	
	public void drawFps(){
		font.draw(batch, ""+Gdx.graphics.getFramesPerSecond(), MyConst.APP_WIDTH-40, MyConst.APP_HEIGHT-20);
	}
	
	public void drawMinimap(){
		
	}
	public void drawPos(){
		//Vector3 v=state.getCamera().project(new Vector3(state.getPlayer().getPosition().x,state.getPlayer().getPosition().y,0));
		
		//font.draw(batch, state.getPlayer().getPosition().x+" "+state.getPlayer().getPosition().y, v.x,v.y);
	}
	
	public void drawNotifications(){
		if(state.getPlayer().getHealth()<=0)MyConst.skin.getFont("titleFont").draw(batch, "GAME OVER", 400,400);
	}
	

}
