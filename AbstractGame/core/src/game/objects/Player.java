package game.objects;

import game.Content;
import game.MyConst;
import game.input.Mouse;
import game.objects.guns.Gun;
import game.objects.guns.Pistol;
import game.states.PlayState;
import box2dLight.ConeLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Player extends GameObject implements Updatable{
	private Sprite testSprite;
	private Gun curGun;
	private GameObject picked=null;
	
	private ConeLight light;
	public Player(PlayState state, Vector2 position) {
		
		super(state, position);
		health=200;
		curTexture=new Sprite(Content.atlas.findRegion("Player"));
		testSprite=new Sprite(curTexture);
		testSprite.setColor(Color.RED);
		curGun= new Pistol(this);
		speed=4;
		light=new ConeLight(state.getRayHandler(), 60, new Color(0.3f,0.3f,0.3f,0.4f),
    			9, 0, 0, imgRotation,25);
        light.setSoftnessLength(1f);
        light.setContactFilter((short)(MyConst.CATEGORY_PLAYER|MyConst.CATEGORY_BULLETS), (short)0,(short)(MyConst.MASK_PLAYER&MyConst.MASK_BULLETS));
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		batch.draw(
	           testSprite, body.getPosition().x-state.getCamera().position.x-imgWidth/2, body.getPosition().y-state.getCamera().position.y-imgHeight/2, 
	            imgWidth/2, imgHeight/2, imgWidth, imgHeight, 1, 1, imgRotation
	        );
	}
	
	public void update(float delta){
		if(health<=0){
			
			return;
		}
			
		//update sub objects
		curGun.update(delta);
		Vector2 v=new Vector2();
		if (Gdx.input.isKeyPressed(Keys.A) ) { 
			 v.add(new Vector2(-50,0));
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			v.add(new Vector2(50,0));
		}	
		if (Gdx.input.isKeyPressed(Keys.W) ) { 
			v.add(new Vector2(0,50));
		}
		if (Gdx.input.isKeyPressed(Keys.S)) {
			v.add(new Vector2(0,-50));
		}
		body.setLinearVelocity(v.nor().scl(speed));
		
		
		Vector2 mouse=new Vector2(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);
		direction=new Vector2(Gdx.input.getX(),Gdx.graphics.getHeight()-Gdx.input.getY()).sub(mouse);
		imgRotation=direction.angle();
		
		//shooting
		if(Gdx.input.isButtonPressed(0)&&Gdx.input.justTouched()){
			Vector2 c=Mouse.getScreenPos().sub(mouse);
			curGun.shoot(c);
		}
		if(Gdx.input.isButtonPressed(1)&&Gdx.input.justTouched()){
			state.addObj(new Box(state,Mouse.getWorldPos(state.getViewport())));
		}
		if(Gdx.input.isKeyJustPressed(Keys.F)){
			light.setActive(!light.isActive());
		}
		
		light.setPosition(getPosition());
		light.setDirection(direction.angle());
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
