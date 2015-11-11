package game.objects;

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
import game.visuals.BouncingLaser;
import game.visuals.LaserCallback;

import java.util.LinkedList;

import box2dLight.ConeLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Player extends GameObject implements Updatable{
	private Sprite grabTexture;
	private Sprite normalTexture;
	private Gun gun;
	private LinkedList<Gun> guns=new LinkedList<Gun>();
	private GameObject picked=null;
	private int numGranades=100;
	private ConeLight light;
	public Player(PlayState state, Vector2 position) {
		
		super(state, position);
		friendly=true;
		maxHealth=50;
		health=50;
		
		grabTexture=new Sprite(Content.atlas.findRegion("Player_grab"));
		normalTexture=new Sprite(Content.atlas.findRegion("Player"));
		curTexture=normalTexture;
		guns.add(new Pistol(this));
		guns.add(new MchineGun(this));
		guns.add(new Shotgun(this));
		gun=guns.peekLast();
		speed=4;
		light=new ConeLight(state.getRayHandler(), 60, new Color(1f,1f,1f,0.4f),
    			9, 0, 0, imgRotation,25);
        light.setSoftnessLength(1f);
        light.setContactFilter((short)(MyConst.CATEGORY_PLAYER|MyConst.CATEGORY_BULLETS), (short)0,(short)(MyConst.MASK_PLAYER&MyConst.MASK_BULLETS));
        
	}
	
	public void updateMoving(float delta){
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
		
	}
	
	public void grabOrStab(){
		if(picked!=null){
			picked.picked=false;
			picked=null;
			return;
		}
		
		GameObject obj=nearestObject();
		if(obj.getPosition().dst(getPosition())<1.2f&&(direction.angle()-obj.getPosition().cpy().sub(getPosition()).angle()<90||direction.angle()-obj.getPosition().cpy().sub(getPosition()).angle()>-90)
		   /*obj.getStateMachine().getCurrentState()!=EnemyState.CHASING&&obj.getStateMachine().getCurrentState()!=EnemyState.SEARCH*/){
			if(obj==null)return;
			if(obj instanceof Player)return;
			if(obj instanceof Enemy&& !(obj instanceof KamikazeRobot)){
				obj.setHealth(-1);
			}else{
				picked=obj;
				obj.picked=true;
			}
			
		}
	}
	
	public void throwGranade(){
		numGranades--;
		Granade g=new Granade(state,getPosition().cpy().add(direction.cpy().nor().scl(0.25f)),friendly);
		g.setDirection(direction);
		g.getBody().applyLinearImpulse(direction.cpy().nor().scl(0.6f), getPosition(), true);
		state.addObj(g);
	}
	
	public void pickObject(){
		
	}
	
	public void shootOrThrow(){
		if(picked!=null){
			picked.body.applyLinearImpulse(direction.cpy().scl(5+(picked.body.getLinearDamping()/2)), picked.getPosition(), true);
			picked.picked=false;
			picked=null;
			return;
		}
		gun.pullTrigger(direction);
	}
	
	public void updateCtrls(float delta){
		//shooting
		if(Gdx.input.isButtonPressed(0)&&Gdx.input.isTouched()){
			shootOrThrow();
		}
		if(Gdx.input.isButtonPressed(1)&&Gdx.input.justTouched()){
			grabOrStab();
		}
		if(Gdx.input.isKeyJustPressed(Keys.F)){
			light.setActive(!light.isActive());
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.E)){
			//state.addObj(new Pickable(state,Mouse.getWorldPos(state.getCamera()),(int)(Math.random()*2)));
			state.addObj(new Box(state,Mouse.getWorldPos(state.getCamera())));
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.Q)){
			pickObject();
		}
		
		
		if(Gdx.input.isKeyJustPressed(Keys.SPACE)){
			//change weapon
			gun=guns.poll();
			guns.addLast(gun);
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.R)){
			gun.reload();
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.G)&&numGranades>0){
			throwGranade();
		}
	}
	
	public void update(float delta){
		
		
		if(health<=0){
			body.setActive(false);
			return;
		}
			
		//update sub objects
		gun.update(delta);
		if(picked!=null){
			System.out.println("hahah");
			picked.body.setTransform(getPosition().cpy().add(direction.cpy().nor().scl(radius*2+0.125f)), (float)Math.toRadians(imgRotation));
			picked.setImgRotation(imgRotation+180);
		}
		
		//updates moving
		
		updateMoving(delta);
		updateCtrls(delta);
	
		
		
		
		light.setPosition(getPosition());
		light.setDirection(direction.angle());
		imgRotation=direction.angle();
	}
	
	public GameObject nearestObject(){
		GameObject e=null;
		float dst=Float.MAX_VALUE;
		for(GameObject obj:state.getObjects()){
			if(obj instanceof Player)continue;
			float cur=obj.getPosition().dst(getPosition());
			if(cur<dst){
				dst=cur;
				e=obj;
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

	
	@Override
	public void draw(SpriteBatch batch) {
		//laser.draw(batch);
		if(picked!=null){
			curTexture=grabTexture;
			imgRotation-=90;
		}else{
			curTexture=normalTexture;
		}
		super.draw(batch);
	}
	
	public void debugDraw(ShapeRenderer sr){
		
	}

	public Gun getGun() {
		return gun;
	}

}
