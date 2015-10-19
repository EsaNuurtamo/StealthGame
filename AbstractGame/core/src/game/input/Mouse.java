package game.input;

import game.MyConst;
import game.objects.GameObject;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Mouse {
	//tämä on väärin
	public static Vector2 getScreenPos(){
		
		return new Vector2(Gdx.input.getX(),Gdx.graphics.getHeight()-Gdx.input.getY());
	}
	
	//korjaa kauhea purkka yhtälö!!!!!!!!!!
	public static Vector2 getWorldPos(Camera camera){
		//float x=(Mouse.getScreenPos().x-Gdx.graphics.getWidth()/2)/(MyConst.PIX_IN_M)*MyConst.VIEW_SCALE+cam.position.x;
		//float y=(Mouse.getScreenPos().y-Gdx.graphics.getHeight()/2)/(MyConst.PIX_IN_M)*MyConst.VIEW_SCALE+cam.position.y;
		
		Vector3 vecCursorPos = new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
		Vector3 world=camera.unproject(vecCursorPos);
		return new Vector2(world.x,world.y);
		
	}
	
	

}
