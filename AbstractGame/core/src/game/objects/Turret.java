package game.objects;

import game.Content;
import game.MyConst;
import game.states.PlayState;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Turret extends GameObject implements Updatable{
	protected Sprite curTexture2;
    protected float imgRotation2;
    boolean alerted=false;
	public Turret(PlayState state, Vector2 position) {
		super(state, position);
		curTexture=new Sprite(Content.atlas.findRegion("Turret_base"));
		curTexture2=new Sprite(Content.atlas.findRegion("Turret1"));;
		
	}
	
	
	
	
	
	@Override
	public void draw(SpriteBatch batch) {
		super.draw(batch);
		batch.draw(
        		curTexture2, body.getPosition().x-imgWidth/2, body.getPosition().y-imgHeight/2, 
	            imgWidth/2, imgHeight/2, imgWidth, imgHeight, 1, 1, imgRotation2-90
	    );
	}



	@Override
	public void update(float delta) {
		direction=state.getPlayer().getPosition().cpy().sub(getPosition()).nor();
		imgRotation2=direction.angle();
	}
	@Override
	public void init(Vector2 position) {
		// First we create a body definition
    	BodyDef bodyDef = new BodyDef();
    	// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
    	bodyDef.type = BodyType.StaticBody;
    	// Set our body's starting position in the world
    	bodyDef.position.set(position);
    	
    	
    	body = state.getWorld().createBody(bodyDef);
    	
    	// Create a circle shape and set its radius to 6
    	//PolygonShape circle = new PolygonShape();
    	//circle.setAsBox(0.3f, 0.3f);
    	PolygonShape circle = new PolygonShape();
    	circle.setAsBox(radius, radius);
    	

    	// Create a fixture definition to apply our shape to
    	FixtureDef fixtureDef = new FixtureDef();
    	fixtureDef.shape = circle;
    	fixtureDef.density = 0.5f; 
    	fixtureDef.friction = 0.4f;
    	fixtureDef.restitution = 0.0f; // Make it bounce a little bit
    	fixtureDef.filter.categoryBits=MyConst.CATEGORY_SCENERY;
    	
    	// Create our fixture and attach it to the body
    	fixture = body.createFixture(fixtureDef);

    	// Remember to dispose of any shapes after you're done with them!
    	// BodyDef and FixtureDef don't need disposing, but shapes do.
    	circle.dispose();
	}
}
