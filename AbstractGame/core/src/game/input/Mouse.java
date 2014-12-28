package game.input;

import game.MyConst;
import game.objects.GameObject;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Mouse {
	
	public static Vector2 getScreenPos(){
		
		return new Vector2(Gdx.input.getX(),Gdx.graphics.getHeight()-Gdx.input.getY());
	}
	
	//korjaa kauhea purkka yhtälö!!!!!!!!!!
	public static Vector2 getWorldPos(Viewport port){
		//float x=(Mouse.getScreenPos().x-Gdx.graphics.getWidth()/2)/(MyConst.PIX_IN_M)*MyConst.VIEW_SCALE+cam.position.x;
		//float y=(Mouse.getScreenPos().y-Gdx.graphics.getHeight()/2)/(MyConst.PIX_IN_M)*MyConst.VIEW_SCALE+cam.position.y;
		return port.unproject(Mouse.getScreenPos());
		
	}
	
	public static GameObject getOverlapObject(List<GameObject> objects, Viewport port){
		
		GameObject object=null;
		for(GameObject obj:objects){
			if(obj.getFixture().testPoint(getWorldPos(port))){
				object=obj;
			}
		}
		return object;
	}

}
