package game.objects.guns;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import game.Content;
import game.MyConst;
import game.ai.EnemyState;
import game.objects.Bullet;
import game.objects.GameObject;
import game.objects.Updatable;
import game.objects.organic.Enemy;
import game.objects.organic.Player;
import game.states.PlayState;
import game.tools.MyUtils;
import game.tools.VisibleCallback;
import game.visuals.Effect;

public class Granade extends GameObject implements Updatable{
	private float explosionRadius=3;
	private float timer=0;
	private VisibleCallback rayCast;
	public Granade(PlayState state, Vector2 position, boolean friendly) {
		super(state, position, 0.15f);
		curTexture=new Sprite(Content.atlas.findRegion("Grenade"));
		imgWidth=(radius*8);
    	imgHeight=(radius*8);
        speed=10;
        this.friendly=friendly;
        Timer.schedule(new Task(){
        	@Override
			public void run() {
        		setDestroyed(true);
        		
			}
        	
        }, 2);
        rayCast=new VisibleCallback();
	}
	
	@Override
	public void init(Vector2 position) {
		// First we create a body definition
    	BodyDef bodyDef = new BodyDef();
    	// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
    	bodyDef.type = BodyType.DynamicBody;
    	// Set our body's starting position in the world
    	bodyDef.position.set(position);
    	bodyDef.linearDamping=5f;
    	bodyDef.fixedRotation=true;
    	//bodyDef.bullet=true;
    	body = state.getWorld().createBody(bodyDef);
    	
    	// Create a circle shape and set its radius to 6
    	CircleShape circle = new CircleShape();
    	circle.setRadius(radius);

    	// Create a fixture definition to apply our shape to
    	FixtureDef fixtureDef = new FixtureDef();
    	fixtureDef.shape = circle;
    	fixtureDef.density = 0.2f; 
    	fixtureDef.friction = 0.0f;
    	fixtureDef.restitution=0.4f;
    	fixtureDef.filter.categoryBits=MyConst.CATEGORY_BULLETS;
    	fixtureDef.filter.maskBits=MyConst.MASK_BULLETS;
    	// Create our fixture and attach it to the body
    	fixture = body.createFixture(fixtureDef);

    	// Remember to dispose of any shapes after you're done with them!
    	// BodyDef and FixtureDef don't need disposing, but shapes do.
    	circle.dispose();
    	
	}

	@Override
	public void update(float delta) {
		imgRotation+=delta*100;
		
	}
	
	@Override
	public void setDestroyed(boolean destroyed) {
		super.setDestroyed(destroyed);
		for(GameObject obj:MyUtils.objInRange(state.getObjects(), getPosition(), explosionRadius)){
			if(obj instanceof Enemy|| obj instanceof Player){
				if(isObjectInView(obj)&&obj.isFriendly()!=isFriendly()){
					obj.setHealth(obj.getHealth()-200);;
				}
				
			}
		}
		for(GameObject obj:MyUtils.objInRange(state.getObjects(), getPosition(), (float)Math.pow(explosionRadius,2))){
			if(obj instanceof Enemy){
				Enemy e=(Enemy)obj;
				e.getStateMachine().changeState(EnemyState.SEARCH);
				
			}
		}
		state.addObj(new Effect(state, getPosition().cpy(),Effect.EXPLOSION));
		Content.getSound("explosion").play(MyConst.SFXvol);
	}
	
	public boolean isObjectInView(GameObject obj){
		rayCast.setVisible(true);
		rayCast.setTargetObj(obj);
		state.getWorld().rayCast(rayCast, getPosition().cpy(), obj.getPosition());
		return rayCast.isVisible();
	}

}
