package game.objects;

import game.Content;
import game.input.Mouse;
import game.objects.guns.Gun;
import game.objects.guns.Pistol;
import game.states.PlayState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Player extends GameObject implements Updatable{
	private Gun curGun;
	private GameObject picked=null;
	public Player(PlayState state, Vector2 position) {
		
		super(state, position);
		curTexture=new TextureRegion(Content.atlas.findRegion("Player"));
		curGun= new Pistol(this);
		speed=4;
	}
	
	public void update(float delta){
		curGun.update(delta);
		Vector2 v=new Vector2();
		if (Gdx.input.isKeyPressed(Keys.A) ) { 
			 v.add(new Vector2(-50,0));
		     
		     
		}

		// apply right impulse, but only if max velocity is not reached yet
		if (Gdx.input.isKeyPressed(Keys.D)) {
			v.add(new Vector2(50,0));
		    
		}	
		
		if (Gdx.input.isKeyPressed(Keys.W) ) { 
			v.add(new Vector2(0,50));
		}

		// apply right impulse, but only if max velocity is not reached yet
		if (Gdx.input.isKeyPressed(Keys.S)) {
			v.add(new Vector2(0,-50));
		}
		body.setLinearVelocity(v.limit(speed));
		
		
		Vector2 mouse=new Vector2(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);
		direction=new Vector2(Gdx.input.getX(),Gdx.graphics.getHeight()-Gdx.input.getY()).sub(mouse);
		imgRotation=new Vector2(direction).angle();
		
		if(Gdx.input.justTouched()){
			
			Vector2 c=Mouse.getScreenPos().sub(mouse);
			curGun.shoot(c);
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
    	circle.setRadius(0.3f);

    	// Create a fixture definition to apply our shape to
    	FixtureDef fixtureDef = new FixtureDef();
    	fixtureDef.shape = circle;
    	fixtureDef.density = 9f; 
    	fixtureDef.friction = 0.4f;
    	 // Make it bounce a little bit
        
    	// Create our fixture and attach it to the body
    	fixture = body.createFixture(fixtureDef);

    	// Remember to dispose of any shapes after you're done with them!
    	// BodyDef and FixtureDef don't need disposing, but shapes do.
    	circle.dispose();
	}

	

	

}
