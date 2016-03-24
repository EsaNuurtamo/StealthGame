package game.objects.organic;

import game.Content;
import game.MyConst;
import game.ai.EnemyState;
import game.map.MapBodyBuilder;
import game.objects.GameObject;
import game.objects.Pickable;
import game.objects.Updatable;
import game.objects.guns.Granade;
import game.objects.guns.Gun;
import game.objects.guns.Pistol;
import game.states.PlayState;
import game.tools.VisibleCallback;
import box2dLight.ConeLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class Enemy extends GameObject implements Updatable{
	protected Vector2 start=new Vector2();
	protected Vector2 target=new Vector2();
	protected StateMachine<Enemy> stateMachine;
	protected GameObject objectSeen;
	protected float visionLen=10;
	protected Player player;
	protected float FOV=120f;
	//gun
	protected Gun gun;
	protected float shootTimer;
	
	//path
	protected Vector2[] patrolPath;
	protected Vector2[] path;
	protected int waypoint=0;
	float tolerance=0.2f;
	float pathLength=0;
	float finderInterval=1f;
	
	//ai
	
	protected float pathTimer=0;
	protected float giveUpTimer=0;
	protected float lookoutTimer=0;
	protected float turnTimer=0;
	protected float reactionTimer=0;
	protected float pathfindTimer=0;
	public float granadetimer=0;
	//flashlight
	protected ConeLight light;
	protected VisibleCallback visibility;
	
	protected boolean dying;
    
	//target rotation for smooth rotation
	protected float targetRotation;
    //physical properties
	
	public Enemy(PlayState state, Vector2 position) {
		super(state, position, 0.3f);
		friendly=false;
		health=20;
		direction=new Vector2(0,1);
		curTexture=new Sprite(Content.atlas.findRegion("Enemy"));
		gun= new Pistol(this);
		shootTimer=0f;
	    dying=false;
        speed=1;
        light=new ConeLight(state.getRayHandler(), 60, new Color(1f,1f,1f,0.4f),
    			9, 0, 0, imgRotation,25);
        light.setSoftnessLength(1f);
        light.setContactFilter((short)(MyConst.CATEGORY_PLAYER|MyConst.CATEGORY_BULLETS), (short)0,(short)(MyConst.MASK_PLAYER&MyConst.MASK_BULLETS));
        
        stateMachine = new DefaultStateMachine<Enemy>(this, EnemyState.PATROL);
        visibility=new VisibleCallback(state.getPlayer());
        player=state.getPlayer();
        animation=Content.animations.get("EnemyDeath");
    }
	
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
    	fixtureDef.density = 6f; 
    	fixtureDef.friction = 0.4f;
    	fixtureDef.restitution = 0.6f; // Make it bounce a little bit
    	fixtureDef.filter.categoryBits=MyConst.CATEGORY_ENEMY;
    	// Create our fixture and attach it to the body
    	fixture = body.createFixture(fixtureDef);

    	// Remember to dispose of any shapes after you're done with them!
    	// BodyDef and FixtureDef don't need disposing, but shapes do.
    	circle.dispose();
    }
	
	
	
	@Override
	public void dispose() {
		light.remove();
		
	}
	
	public void setPath(Vector2[] vertices) {
		waypoint=0;
		path = vertices;
	}
	
	@Override
	public void update(float delta) {
		if(state.getPlayer().isDestroyed())stateMachine.changeState(EnemyState.WALK_TO_PATROL);
		if(dying)return;
		if(health<=0&&!animated){
			imgHeight*=2;
			
			animated=true;
				
			//placing the animation beacause differnet size than 
			animLoc=getPosition().cpy().add((direction.cpy().nor().scl(imgHeight/4*1)));
		}
		if(animated){
			body.setActive(false);
			
			if(animation.isAnimationFinished(animTime)){
				light.setActive(false);
				dying=true;
				state.addObj(new Pickable(state,getPosition(),(int)(Math.random()*2)));
				return;
			}
			curTexture=new Sprite(animation.getKeyFrame(animTime));
			animTime+=delta;
			return;
		}
		updateTime(delta);
		
		stateMachine.update();
		gun.update(delta);
		handleRotation(delta);
		imgRotation=direction.angle();
        light.setPosition(getPosition());
		light.setDirection(imgRotation);
		
		
	}
	
	public void resetTimers(){
		pathTimer=0;
		giveUpTimer=0;
		lookoutTimer=0;
		turnTimer=0;
		reactionTimer=0;
		shootTimer=0;
		pathfindTimer=0;
		granadetimer=0;
	}
	
	public void updateTime(float delta){
		pathTimer+=delta;
		giveUpTimer+=delta;
		lookoutTimer+=delta;
		turnTimer+=delta;
		reactionTimer+=delta;
		shootTimer+=delta;
		pathfindTimer+=delta;
		granadetimer+=delta;
	}
	
	public void throwGranade(){
		
			Granade g=new Granade(state,getPosition().cpy().add(direction.cpy().nor().scl(0.25f)),false);
			g.setDirection(direction);
			g.getBody().applyLinearImpulse(direction.cpy().nor().scl(0.6f), getPosition(), true);
			state.addObj(g);
		
	}
	
	public void shoot(){
		
		//take advance
		Vector2 meetPoint=player.getPosition().cpy().add(player.getDirection().cpy().nor().scl(1/20f));
		Vector2 apu=meetPoint.cpy().sub(getPosition());
		gun.aiShoot(direction,0f);
	}
	
	public void shootPlayer(){
		targetRotation=state.getPlayer().getPosition().cpy().sub(getPosition()).angle();
		shoot();
	}
	
	
	
	public boolean seePlayer(){
		if(direction.len()==0)return false;
		if(player.getPosition().dst(getPosition())>7)return false;
		
		
		visibility.setVisible(true);
		state.getWorld().rayCast(visibility, getPosition(),state.getPlayer().getPosition());
		
		return 
	    (visibility.isVisible())&&//raycasting finds player
		(state.isInLight()||light.contains(state.getPlayer().getPosition().x, state.getPlayer().getPosition().y))&&//is player in any light
		(Math.abs(player.getPosition().cpy().sub(getPosition()).angle()-direction.angle()))<=FOV/2;///is player in FOV
		
		
		
	}
	@Override
	public void drawShape(ShapeRenderer sr) {
		sr.rectLine(start, target, 0.2f);
	}
	
	public int getWaypoint() {
		return waypoint;
	}

	public void setWaypoint(int waypoint) {
		this.waypoint = waypoint;
	}

	public float getTolerance() {
		return tolerance;
	}

	public void setTolerance(float tolerance) {
		this.tolerance = tolerance;
	}

	public float getPathLength() {
		return pathLength;
	}

	public void setPathLength(float pathLength) {
		this.pathLength = pathLength;
	}

	public float getFinderInterval() {
		return finderInterval;
	}

	public void setFinderInterval(float finderInterval) {
		this.finderInterval = finderInterval;
	}

	

	public ConeLight getLight() {
		return light;
	}

	public void setLight(ConeLight light) {
		this.light = light;
	}
	

	public boolean isDying() {
		return dying;
	}

	public void setDying(boolean dying) {
		this.dying = dying;
	}

	public float getTargetRotation() {
		return targetRotation;
	}

	public void setTargetRotation(float targetRotation) {
		this.targetRotation = targetRotation;
	}

	public Vector2[] getPatrolPath() {
		return patrolPath;
	}

	public Vector2[] getPath() {
		return path;
	}

	public void findPathToPlayer(){
		findPathTo(state.getPlayer().getPosition());
	}
	public void findPathTo(Vector2 to){
		setPath(MapBodyBuilder.findPath(getPosition(), to, state.getMap()));
	}
	
	public void walkOnPath(){
		if(path==null||path.length<=0){
			
			waypoint=0;
			return;
		}
		Vector2 v=path[waypoint].cpy().sub(getPosition()).nor().scl(speed);
		targetRotation=v.angle();
        body.setLinearVelocity(v);
		
        if(isReached()){
        	waypoint++;
        	if(waypoint>=path.length){
        		waypoint=0;
        	}
        }
    }
	
	public void handleRotation(float delta){
		float angle=0;
		float r=targetRotation-direction.angle();
		if(r==0){
			return;
		}
		if(r>0){
			if(r>180){
				angle=360-r;
				direction.rotate(-angle*delta*6);
			}else{
				direction.rotate(r*delta*6);
				
			}
		}else{
			if(r<-180){
				angle=360+r;
				direction.rotate(+angle*delta*6);
			}else{
				direction.rotate(r*delta*6);
			}
		}
	}
	
	
	public boolean isReached(){
		//System.out.println("dlength: "+(path[waypoint].x-getPosition().x));
		if(Math.abs(path[waypoint].x-getPosition().x)<tolerance&&Math.abs(path[waypoint].y-getPosition().y)<tolerance){
			return true;
			
		}
		return false;
	}
	
	private void printPath(){
		for(int i=0;i<path.length;i++){
			System.out.println(path[i].toString());
		}
	}
	
	
    public void setPatrolPath(Vector2[] path){
    	setPath(path);
    	patrolPath=path;
    }
    public StateMachine<Enemy> getStateMachine() {
		return stateMachine;
	}
    
    public float getPathTimer() {
		return pathTimer;
	}
    public void setPathTimer(float pathTimer) {
		this.pathTimer = pathTimer;
	}
    public void setGiveUpTimer(float giveUpTimer) {
		this.giveUpTimer = giveUpTimer;
	}
    public float getGiveUpTimer() {
		return giveUpTimer;
	}
    
    public float getLookoutTimer() {
		return lookoutTimer;
	}
   public void setLookoutTimer(float lookoutTimer) {
	this.lookoutTimer = lookoutTimer;
}
   
   public float getTurnTimer() {
	return turnTimer;
}
   public void setTurnTimer(float turnTimer) {
	this.turnTimer = turnTimer;
}
   public void setReactionTimer(float reactionTimer) {
	this.reactionTimer = reactionTimer;
}
   public float getReactionTimer() {
	return reactionTimer;
}
   //TASKS
   private class GiveUp extends Task{
	   
	   public GiveUp() {
		
	    }
		@Override
		public void run() {
			 getStateMachine().changeState(EnemyState.LONG_SEARCH);
		}
	}
   public Task giveUp(){
	   
	   return new GiveUp();
	   
   }
   
    public float getShootTimer() {
		return shootTimer;
	}public void setShootTimer(float shootTimer) {
		this.shootTimer = shootTimer;
	}  
    public float getTargetRoation(){
    	return targetRotation;
    }
    public float getPathfindTimer() {
		return pathfindTimer;
	}
 public void setPathfindTimer(float pathfindTimer) {
	this.pathfindTimer = pathfindTimer;
}
}
