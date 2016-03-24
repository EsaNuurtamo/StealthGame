package game.objects.items;

import game.Content;
import game.MyConst;
import game.objects.GameObject;
import game.objects.Updatable;
import game.states.PlayState;
import game.visuals.BouncingLaser;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Box extends GameObject implements Updatable{
	//private BouncingLaser laser;
	public Box(PlayState state, Vector2 position) {
		this(state, position,0.3f);
		
		
		
	}
	public Box(PlayState state, Vector2 position, float radius) {
		super(state, position,radius);
		curTexture=new Sprite(Content.atlas.findRegion("Box"));
		//this.laser=new BouncingLaser(state);
	}
	@Override
	public void update(float delta) {
		
		imgRotation=(float)Math.toDegrees(body.getAngle());
		direction.setAngle(imgRotation);
		//laser.updateLaser(getPosition(), direction.cpy().nor());
		if(dying){
			/*for(int i=0;i<5;i++){
				state.addObj(new Box(state, getPosition(),radius*0.7f));
			}*/
			setDestroyed(true);
			
		}
		
		
		
	}
	
	
	@Override
	public void drawShape(ShapeRenderer sr) {
		
		//laser.draw(sr);
	}
	@Override
	public void init(Vector2 position) {
		// First we create a body definition
    	BodyDef bodyDef = new BodyDef();
    	// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
    	bodyDef.type = BodyType.DynamicBody;
    	// Set our body's starting position in the world
    	bodyDef.position.set(position);
    	bodyDef.linearDamping=10f;
    	bodyDef.angularDamping=10f;
    	
    	body = state.getWorld().createBody(bodyDef);
    	
    	// Create a circle shape and set its radius to 6
    	//PolygonShape circle = new PolygonShape();
    	//circle.setAsBox(0.3f, 0.3f);
    	PolygonShape circle = new PolygonShape();
    	circle.setAsBox(radius, radius);
    	

    	// Create a fixture definition to apply our shape to
    	FixtureDef fixtureDef = new FixtureDef();
    	fixtureDef.shape = circle;
    	fixtureDef.density = 10f; 
    	fixtureDef.friction = 0.4f;
    	fixtureDef.restitution = 0.0f; // Make it bounce a little bit
    	//fixtureDef.filter.categoryBits=MyConst.CATEGORY_SCENERY;
    	
    	// Create our fixture and attach it to the body
    	fixture = body.createFixture(fixtureDef);

    	// Remember to dispose of any shapes after you're done with them!
    	// BodyDef and FixtureDef don't need disposing, but shapes do.
    	circle.dispose();
    	
	}
	@Override
	public void drawEffects(SpriteBatch batch) {
		//laser.draw(batch);
	}

}
