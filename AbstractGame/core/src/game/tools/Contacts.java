package game.tools;

import game.ai.EnemyState;
import game.objects.Box;
import game.objects.Bullet;
import game.objects.Enemy;
import game.objects.GameObject;
import game.objects.Pickable;
import game.objects.Player;
import game.states.PlayState;
import game.visuals.Effect;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class Contacts implements ContactListener{
	private PlayState state;
	public Contacts(PlayState state) {
		this.state=state;
	}

	@Override
	public void beginContact(Contact contact) {
		GameObject a= (GameObject)contact.getFixtureA().getBody().getUserData();
		GameObject b = (GameObject)contact.getFixtureB().getBody().getUserData();
        
		//bullet involeved
		if(a instanceof Bullet){
        	bulletHit(a, b);
        }
        if(b instanceof Bullet){
        	bulletHit(b,a);
        }
        
        //pickable involved
        if(a instanceof Pickable){
        	pickableHit(a, b);
        }
        if(b instanceof Pickable){
        	pickableHit(b, a);
        }
        
        
        
		
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
	
	private void bulletHit(GameObject bullet, GameObject other){
		
		//effect type
		int type=Effect.OBJECT_HIT;
		
		if((other instanceof Enemy|| other instanceof Player)&&other.isFriendly()!=bullet.isFriendly()){
        	((Bullet)bullet).setRed(true);
        	other.setHealth(other.getHealth()-10);
        	type=Effect.ENTITY_HIT;
        	
        }
    	
    	if(other instanceof Box){
    		other.setDying(true);
    		type=Effect.OBJECT_HIT;
    	}
    	
    	if(other instanceof Enemy){
    		if(((Enemy)other).getStateMachine().getCurrentState()!=EnemyState.CHASING){
    			((Enemy)other).getStateMachine().changeState(EnemyState.CHASING);
    		}
    		
    	}
    	
    	if(!bullet.isDestroyed()){
			bullet.setDestroyed(true);
			state.addObj(new Effect(state,bullet.getPosition().cpy(),type));
		}
	}
	
	private void pickableHit(GameObject pickable, GameObject other){
		if(other instanceof Player){
			((Pickable)pickable).take();
			pickable.setDestroyed(true);
		}
		
	}
	
	

}
