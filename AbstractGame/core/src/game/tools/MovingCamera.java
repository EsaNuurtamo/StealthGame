package game.tools;

import game.states.PlayState;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class MovingCamera extends OrthographicCamera{
	public boolean shaking=false;
	private float offset=0.02F;
	private PlayState state;
	private Vector3 vect=new Vector3(0,0,0);
	public MovingCamera(float w, float h, PlayState state) {
		super(w,h);
		this.state=state;
		
		
	}
	
	public void offset(){
		this.position.set(
			 new Vector3((float)(position.x+(-offset+Math.random()*2*offset)),
					     (float)(position.y+(-offset+Math.random()*2*offset)),position.z)
        );
		
	}

	@Override
	public void update() {
		
		if(shaking)offset();
		super.update();
		
	}

	@Override
	public void update(boolean updateFrustum) {
		super.update(updateFrustum);
		
	}

}
