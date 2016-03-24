package game.tools;

import game.objects.GameObject;
import game.objects.organic.Player;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class Rays implements RayCastCallback{
	public static GameObject hit;
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,Vector2 normal, float fraction) {
		hit=null;
		hit=(GameObject)fixture.getBody().getUserData();
		return 1;
	}
	
}
