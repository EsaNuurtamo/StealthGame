package game.objects;

import game.Content;
import game.MyConst;
import game.ai.AIType;
import game.ai.EnemyState;
import game.states.PlayState;
import game.tools.MapBodyBuilder;
import box2dLight.ConeLight;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Enemy extends GameObject implements Updatable{
	private StateMachine<Enemy> stateMachine;
	//path
	private Vector2[] patrolPath;
	private Vector2[] path;
	private int waypoint=0;
	float tolerance=0.2f;
	float pathLength=0;
	float finderInterval=1f;
	//ai
	private AIType aiState=AIType.WAITING;
	private float pathTimer=0;
	private float giveUpTimer=0;
	private float lookoutTimer=0;
	private float turnTimer=0;
	private float reactionTimer=0;
	//flashlight
	private ConeLight light;
	
	private boolean dying;
    
	//target rotation for smooth rotation
    private float targetRotation;
    
	public Enemy(PlayState state, Vector2 position) {
		super(state, position);
		curTexture=new TextureRegion(Content.atlas.findRegion("Enemy"));
	dying=false;
        speed=2;
        light=new ConeLight(state.getRayHandler(), 60, new Color(0.3f,0.3f,0.3f,0.4f),
    			9, 0, 0, imgRotation,25);
        light.setSoftnessLength(1f);
        light.setContactFilter((short)(MyConst.CATEGORY_PLAYER|MyConst.CATEGORY_BULLETS), (short)0,(short)(MyConst.MASK_PLAYER&MyConst.MASK_BULLETS));
        stateMachine = new DefaultStateMachine<Enemy>(this, EnemyState.LOOKOUT);
    }
	
	public void setPath(Vector2[] vertices) {
		waypoint=0;
		/*float m=0;
		Vector2 last=null;
		for(int i=0; i<vertices.length;i++){
			if(last!=null){
				m+=vertices[i].dst(last);
			}else{
				m+=vertices[i].dst(vertices[vertices.length-1]);
			}
			last=vertices[i];
		}
		
	    pathLength=m;*/
		path = vertices;
		
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
    	CircleShape circle = new CircleShape();
    	circle.setRadius(0.3f);

    	// Create a fixture definition to apply our shape to
    	FixtureDef fixtureDef = new FixtureDef();
    	fixtureDef.shape = circle;
    	fixtureDef.density = 0.5f; 
    	fixtureDef.friction = 0.4f;
    	fixtureDef.restitution = 0.0f; // Make it bounce a little bit
    	fixtureDef.filter.categoryBits=MyConst.CATEGORY_ENEMY;
    	
    	// Create our fixture and attach it to the body
    	fixture = body.createFixture(fixtureDef);

    	// Remember to dispose of any shapes after you're done with them!
    	// BodyDef and FixtureDef don't need disposing, but shapes do.
    	circle.dispose();
    	
	}
	
	@Override
	public void update(float delta) {
		pathTimer+=delta;
		giveUpTimer+=delta;
		lookoutTimer+=delta;
		turnTimer+=delta;
		reactionTimer+=delta;
		stateMachine.update();
		handleRotation(delta);
        light.setPosition(getPosition());
		light.setDirection(imgRotation);
	}
	
	
	public void aiUpdate (float delta){	
		if(seePlayer()){
			
			aiState=AIType.CHASING;
			findPathToPlayer();
			
		}
		
		
		switch(aiState){
		
			case WAITING:
				speed=2;
				if(patrolPath==null)return;
				if(path==null){
					findPathTo(patrolPath[0]);
				}
				if(getPosition().dst(patrolPath[0])<0.5f){
					aiState=AIType.PATROL;
					path=patrolPath;
					waypoint=0;
				}
				
					
				walkOnPath();
				
				
				break;
			case PATROL:
				speed=2;
				walkOnPath();
				break;
			case CHASING:
				speed=4;
				
						
				if(finderInterval<0){
					findPathToPlayer();
					finderInterval=1f;
					if(getPosition().dst(state.getPlayer().getPosition())<5){
						findPathToPlayer();
						finderInterval=1f;
					}else{
						
						aiState=AIType.WAITING;
						path=null;
					}
					
				}
				finderInterval-=delta;
				walkOnPath();
				break;
			
		}
	}
	public boolean seePlayer(){
		return light.contains(state.getPlayer().getPosition().x, state.getPlayer().getPosition().y);
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

	public AIType getAiState() {
		return aiState;
	}

	public void setAiState(AIType aiState) {
		this.aiState = aiState;
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
		
		if(aiState==AIType.CHASING){
			targetRotation=state.getPlayer().getPosition().cpy().sub(getPosition()).angle();
		}else if(imgRotation!=v.angle()){
        	targetRotation=v.angle();
        }
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
		float r=targetRotation-imgRotation;
			if(r>0){
				if(r>180){
					angle=360-r;
					imgRotation-=angle*delta*6;
				}else{
					imgRotation+=r*delta*6;
				}
			}else{
				
				if(r<-180){
					angle=360-r;
					imgRotation+=angle*delta*6;
				}else{
					imgRotation+=r*delta*6;
				}
			}
			if(imgRotation>360){
				imgRotation-=360;
			}
			if(imgRotation<0){
				imgRotation=360+imgRotation;
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
}
