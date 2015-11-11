package game.objects;

import game.Content;
import game.states.PlayState;
import game.tools.MyUtils;
import game.visuals.Effect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class KamikazeRobot extends Enemy implements Updatable{
    float selfDestructTimer=0;
    Vector2 finalPos=null;
    
	public KamikazeRobot(PlayState state, Vector2 position) {
		super(state, position);
		speed=4;
		setHealth(1);
		curTexture=new Sprite(Content.atlas.findRegion("Kamikaze_robot"));
		pathfindTimer=1;
		
	}
	
	@Override
	public void update(float delta) {
		if(destroyed)return;
		if(health<=0){
			
			setDestroyed(true);
			return;
		}
		if((state.getPlayer().getPosition().dst(getPosition())<2&&seePlayer()) ||
		    health<=0||dying){
			dying=true;
			if(selfDestructTimer>2f){
				setDestroyed(true);
			}
			
			selfDestructTimer+=delta;
			
			if(picked)return;
			
			/*if(finalPos==null){
				finalPos=getPosition().cpy();
			}else{
				System.out.println("eiei");
				body.setTransform(finalPos.cpy(), (float)Math.toRadians(imgRotation));
			}*/
			return;
			
		}
		if(pathfindTimer>0.2f){
			findPathToPlayer();
			pathfindTimer-=0.2f;
		}
		walkOnPath();
		
		imgRotation=-90+body.getLinearVelocity().angle();
		
		updateTime(delta);
	}
	
	@Override
	public boolean seePlayer() {
		if(direction.len()==0)return false;
		if(player.getPosition().dst(getPosition())>7)return false;
		
		
		visibility.setVisible(true);
		state.getWorld().rayCast(visibility, getPosition(),state.getPlayer().getPosition());
		
		return visibility.isVisible();
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		Color c=batch.getColor();
		if(dying)batch.setColor(Color.RED);
		  super.draw(batch);
		batch.setColor(c);
		
	}
	
	@Override
	public void setDestroyed(boolean destroyed) {
		this.destroyed=destroyed;
		for(GameObject obj:MyUtils.objInRange(state.getObjects(), getPosition().cpy(), 3)){
			if(obj instanceof Enemy|| obj instanceof Player){
				
				obj.setHealth(obj.getHealth()-200);;
				
			}
		}
		state.addObj(new Effect(state, getPosition().cpy(),Effect.EXPLOSION));
		Content.getSound("explosion").play();
	}
	
public void init(Vector2 position) {
    	
    	// First we create a body definition
    	BodyDef bodyDef = new BodyDef();
    	// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
    	bodyDef.type = BodyType.DynamicBody;
    	// Set our body's starting position in the world
    	bodyDef.position.set(position);
    	bodyDef.fixedRotation=true;
    	bodyDef.linearDamping=3f;
    	body = state.getWorld().createBody(bodyDef);

    	// Create a circle shape and set its radius to 6
    	CircleShape circle = new CircleShape();
    	circle.setRadius(radius);

    	// Create a fixture definition to apply our shape to
    	FixtureDef fixtureDef = new FixtureDef();
    	fixtureDef.shape = circle;
    	fixtureDef.density = 4; 
    	
    	
    
        
    	// Create our fixture and attach it to the body
    	fixture = body.createFixture(fixtureDef);

    	// Remember to dispose of any shapes after you're done with them!
    	// BodyDef and FixtureDef don't need disposing, but shapes do.
    	circle.dispose();
    }

}
