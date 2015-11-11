package game.visuals;

import game.Content;
import game.MyConst;
import game.objects.GameObject;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class LaserCallback implements RayCastCallback{
	
	Vector2 contact=null;
	Vector2 normal=null;
	float smallest=Float.MAX_VALUE;
	
	public LaserCallback() {
		
	}
	
	public void reset(){
		contact=null;
		normal=null;
		smallest=Float.MAX_VALUE;
	}
	
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction) {
		if(fixture.getFilterData().categoryBits==MyConst.CATEGORY_ON_FLOOR||
		   fixture.getFilterData().categoryBits==MyConst.CATEGORY_ENEMY||
		   fixture.getFilterData().categoryBits==MyConst.CATEGORY_PLAYER||
		   fixture.getFilterData().categoryBits==MyConst.CATEGORY_BULLETS
				)return 1;
		
		if(fraction<smallest){
			
			contact=point.cpy();
			this.normal=normal.cpy();
			smallest=fraction;
		}
		
		
		return 1;
	}
	
	public Vector2 getContact() {
		return contact;
	}
	public Vector2 getNormal() {
		return normal;
	}
	
	
	
	
	
}
