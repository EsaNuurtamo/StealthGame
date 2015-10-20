package game.objects;

import game.Content;
import game.states.PlayState;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Pickable extends GameObject{
	public static final int AMMO=0,HEALTH=1;
	private int type;
	public Pickable(PlayState state, Vector2 position, int type) {
		super(state, position,0.3f);
		this.type=type;
		switch (type) {
		case AMMO:
			curTexture=new Sprite(Content.atlas.findRegion("Ammo"));
			break;

		case HEALTH:
			curTexture=new Sprite(Content.atlas.findRegion("Health"));
			break;
		}
		
	}
	@Override
	public void init(Vector2 position) {
		// First we create a body definition
    	BodyDef bodyDef = new BodyDef();
    	// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
    	bodyDef.type = BodyType.DynamicBody;
    	// Set our body's starting position in the world
    	bodyDef.position.set(position);
    	bodyDef.fixedRotation=true;
   
    	body = state.getWorld().createBody(bodyDef);

    	// Create a circle shape and set its radius to 6
    	CircleShape circle = new CircleShape();
    	circle.setRadius(radius);

    	// Create a fixture definition to apply our shape to
    	FixtureDef fixtureDef = new FixtureDef();
    	fixtureDef.shape = circle;
    	fixtureDef.density = 0.5f; 
    	fixtureDef.friction = 0.4f;
    	fixtureDef.isSensor=true;
        
    	
    	// Create our fixture and attach it to the body
    	fixture = body.createFixture(fixtureDef);

    	// Remember to dispose of any shapes after you're done with them!
    	// BodyDef and FixtureDef don't need disposing, but shapes do.
    	circle.dispose();
	}
	
	public void take(){
		switch (type) {
		case AMMO:
			state.getPlayer().getGun().addAmmo(20);;
			break;
		case HEALTH:
			state.getPlayer().changeHealth(20);
			break;
		}
	}
}
