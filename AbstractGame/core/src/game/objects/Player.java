package game.objects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import game.Content;
import game.MyConst;
import game.ai.EnemyState;
import game.input.Mouse;
import game.objects.guns.Granade;
import game.objects.guns.Gun;
import game.objects.guns.MchineGun;
import game.objects.guns.Pistol;
import game.objects.guns.Shotgun;
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
	private Gun gun;
	private LinkedList<Gun> guns=new LinkedList<Gun>();
	private GameObject picked=null;
	private int numGranades=1;
	private ConeLight light;
	public Player(PlayState state, Vector2 position) {
		
		super(state, position);
		maxHealth=50;
		health=50;
		curTexture=new Sprite(Content.atlas.findRegion("Player"));
		
		guns.add(new Pistol(this));
		guns.add(new MchineGun(this));
		guns.add(new Shotgun(this));
		gun=guns.peekLast();
		speed=4;
		light=new ConeLight(state.getRayHandler(), 60, new Color(0.3f,0.3f,0.3f,0.4f),
    			9, 0, 0, imgRotation,25);
        light.setSoftnessLength(1f);
        light.setContactFilter((short)(MyConst.CATEGORY_PLAYER|MyConst.CATEGORY_BULLETS), (short)0,(short)(MyConst.MASK_PLAYER&MyConst.MASK_BULLETS));
	}
	
	
	
	public void update(float delta){
		if(health<=0){
			body.setActive(false);
			return;
		}
			
		//update sub objects
		gun.update(delta);
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
		
		Vector2 c=Mouse.getWorldPos(state.getCamera()).sub(getPosition());
		
		direction=c;
		imgRotation=direction.angle();
		
		//shooting
		if(Gdx.input.isButtonPressed(0)&&Gdx.input.isTouched()){
			gun.pullTrigger(c);
		}
		if(Gdx.input.isButtonPressed(1)&&Gdx.input.justTouched()){
			Enemy e=nearestEnemy();
			if(e.getPosition().dst(getPosition())<1f&&Math.abs(direction.angle()-e.getPosition().cpy().sub(getPosition()).angle())<90&&
			   e.getStateMachine().getCurrentState()!=EnemyState.CHASING&&e.getStateMachine().getCurrentState()!=EnemyState.SEARCH){
				e.setHealth(-1);
			}
			
		}
		if(Gdx.input.isKeyJustPressed(Keys.F)){
			light.setActive(!light.isActive());
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.E)){
			state.addObj(new Pickable(state,Mouse.getWorldPos(state.getCamera()),(int)(Math.random()*2)));
			//state.addObj(new Box(state,Mouse.getWorldPos(state.getCamera())));
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.SPACE)){
			
			gun=guns.poll();
			guns.addLast(gun);
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.R)){
			gun.reload();
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.G)&&numGranades>0){
			numGranades--;
			Granade g=new Granade(state,getPosition().cpy().add(direction.cpy().nor().scl(0.25f)));
			g.setDirection(c);
			g.getBody().applyLinearImpulse(c.cpy().nor().scl(0.6f), getPosition(), true);
			state.addObj(g);
		}
		
		
		light.setPosition(getPosition());
		light.setDirection(direction.angle());
	}
	
	public Enemy nearestEnemy(){
		Enemy e=null;
		float dst=Float.MAX_VALUE;
		for(GameObject obj:state.getObjects()){
			float cur=obj.getPosition().dst(getPosition());
			if(obj instanceof Enemy&&cur<dst){
				dst=cur;
				e=(Enemy)obj;
			}
		}
		return e;
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

	
	

	public Gun getGun() {
		return gun;
	}

}
