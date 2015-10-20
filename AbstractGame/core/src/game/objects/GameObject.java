package game.objects;



import game.MyConst;
import game.ai.EnemyState;
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



public abstract class GameObject{
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
    protected boolean animated;
    protected Vector2 animLoc=new Vector2();
    		protected float animTime=0;
    protected boolean destroyed;
	protected float health=5f;
	protected float maxHealth=5f;

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
    	fixtureDef.restitution = 0.6f; // Make it bounce a little bit
        
    	// Create our fixture and attach it to the body
    	fixture = body.createFixture(fixtureDef);

    	// Remember to dispose of any shapes after you're done with them!
    	// BodyDef and FixtureDef don't need disposing, but shapes do.
    	circle.dispose();
    }
    public void alertAllNear(){
		for(GameObject obj:state.getObjects()){
			if(obj instanceof Enemy&&obj.getPosition().dst(getPosition())<7&&((Enemy)obj).getStateMachine().getCurrentState()!=EnemyState.CHASING){
				((Enemy)obj).getStateMachine().changeState(EnemyState.SEARCH);
			}
		}
	}
    public void changeHealth(float amount){
    	health+=amount;
    	if(health>maxHealth)health=maxHealth;
    	if(health<0)health=0;
    }
    
    
    public void dispose(){
    	
    }
    
    public GameObject(PlayState state, Vector2 position) {
		this(state,position,0.3f);
	}

    public GameObject(PlayState state, Vector2 position, float radius) {
    	
    	this.state=state;
    	this.radius=radius;
    	init(position);
    	imgWidth=radius*2*1.85f;
    	imgHeight=radius*2*1.85f;
    	imgRotation=0;
    	
    	destroyed=false;
    	
    	dying=false;
    	direction=new Vector2(0,1);
    	animated=false;
    	if(body!=null)body.setUserData(this);
    }
    
    
    
    
    
    public void draw(SpriteBatch batch){
    	
    	if(animated){
    		
    		batch.draw(
            		curTexture, animLoc.x-imgWidth/2, animLoc.y-imgHeight/2, 
    	            imgWidth/2, imgHeight/2, imgWidth, imgHeight, 1, 1, imgRotation
    	        );
    		
    	}else{
    		batch.draw(
            		
    	            curTexture, body.getPosition().x-imgWidth/2, body.getPosition().y-imgHeight/2, 
    	            imgWidth/2, imgHeight/2, imgWidth, imgHeight, 1, 1, imgRotation
    	        );
    		
    	}
    }
    
    public void destroy(){
    	
    }
    
    public void drawShape(ShapeRenderer sr){
    	//sr.circle(body.getPosition().x, body.getPosition().y, radius);
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
    	if(health>=maxHealth)this.health=maxHealth;
    	else this.health = health;
		
	}
    public float getHealth() {
		return health;
	}
    
    public float getMaxHealth() {
		return maxHealth;
	}
    
    @Override
    public boolean equals(Object obj) {
    	if(!(obj instanceof GameObject))return false;
    	GameObject go=(GameObject) obj;
    	
    	return go.imgRotation==imgRotation&&go.getPosition().equals(getPosition());
    }
    
    public Vector2 getAnimLoc() {
		return animLoc;
	}
}
