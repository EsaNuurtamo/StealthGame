package game.objects;



import game.states.PlayState;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;



public class GameObject{
	protected Body body;
	protected Fixture fixture;
    protected PlayState state;
	protected float speed;
	protected Vector2 direction;
	protected boolean dying;
	//visuals
    protected Sprite curTexture;
    protected float imgRotation;
    protected float imgWidth;
    protected float imgHeight;
    protected float radius;
    protected Animation animation;
    protected boolean destroyed;
	protected float health=5f;
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
    	circle.setRadius(1f);

    	// Create a fixture definition to apply our shape to
    	FixtureDef fixtureDef = new FixtureDef();
    	fixtureDef.shape = circle;
    	fixtureDef.density = 0.5f; 
    	fixtureDef.friction = 0.4f;
    	fixtureDef.restitution = 0.6f; // Make it bounce a little bit
        
    	// Create our fixture and attach it to the body
    	fixture = body.createFixture(fixtureDef);

    	// Remember to dispose of any shapes after you're done with them!
    	// BodyDef and FixtureDef don't need disposing, but shapes do.
    	circle.dispose();
    }
    
    public void dispose(){
    	
    }

    public GameObject(PlayState state, Vector2 position) {
    	
    	this.state=state;
    	init(position);
    	imgWidth=(int)(fixture.getShape().getRadius()*2*1.85f);
    	imgHeight=(int)(fixture.getShape().getRadius()*2*1.85f);
    	imgRotation=0;
    	radius=fixture.getShape().getRadius();
    	destroyed=false;
    	body.setUserData(this);
    	dying=false;
    	direction=new Vector2(0,1);
    }
    
    
    
    
    
    public void draw(SpriteBatch batch){
    	
        batch.draw(
        		
            curTexture, body.getPosition().x-state.getCamera().position.x-imgWidth/2, body.getPosition().y-state.getCamera().position.y-imgHeight/2, 
            imgWidth/2, imgHeight/2, imgWidth, imgHeight, 1, 1, imgRotation
        );
    }
    
    public void drawShape(ShapeRenderer sr){
    	sr.circle(body.getPosition().x, body.getPosition().y, radius, segments);
    }
    
    public Body getBody() {
		return body;
	}
    
    public Fixture getFixture() {
		return fixture;
	}
    
    
    public boolean isDestroyed(){
    	return destroyed;
    }
    
    public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}
    
    public PlayState getState() {
		return state;
	}
    
    public void setDirection(Vector2 direction) {
		this.direction = direction;
	}
    
    public void setImgRotation(float imgRotation) {
		this.imgRotation = imgRotation;
	}
    public Vector2 getDirection() {
		return direction;
	}
    public Vector2 getPosition(){
    	return body.getPosition();
    }
    public float getRadius() {
		return radius;
	}
    public void setRadius(float radius) {
		this.radius = radius;
	}
    public float getSpeed() {
		return speed;
	}
    public void setSpeed(float speed) {
		this.speed = speed;
	}
    public void setDying(boolean dying) {
		this.dying = dying;
	}
    
    public float getRotation() {
		return imgRotation;
	}
    
    public void setHealth(float health) {
		this.health = health;
	}
    public float getHealth() {
		return health;
	}
    
    
    
    @Override
    public boolean equals(Object obj) {
    	if(!(obj instanceof GameObject))return false;
    	GameObject go=(GameObject) obj;
    	
    	return go.imgRotation==imgRotation&&go.getPosition().equals(getPosition());
    }
}
