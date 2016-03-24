package game.visuals;

import game.states.PlayState;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class BouncingLaser {
	private List<Laser> lasers=new ArrayList<Laser>();
	private float len=0;
	private LaserCallback caster;
	private PlayState state;
	public BouncingLaser(PlayState state) {
		this.state=state;
		caster=new LaserCallback();
	}
	
	public void updateLaser(Vector2 firstStart, Vector2 startDir){
		lasers.clear();
		len=0f;
		Vector2 direction=startDir;
		Vector2 start=firstStart;
		while(len<30){
			caster.reset();
			Vector2 end=start.cpy().add(direction.nor().scl(30));
			if(end.cpy().sub(start).len()<=0||end.equals(start)||Float.isNaN(end.x)||Float.isNaN(end.y)){
				break;
			}
			
			state.getWorld().rayCast(caster, start.cpy(), end.cpy());
			lasers.add(new Laser(start,caster.getContact()));
			
			if(caster.getContact()==null)break;
			len+=caster.getContact().cpy().sub(start).len();
			
			//reflecting the vector of wall
			Vector2 v=direction.cpy().nor();
			direction=v.cpy().sub(caster.getNormal().scl(2*v.dot(caster.getNormal())));
			
			start=caster.getContact();
		}
		
	}
	
	public void draw(SpriteBatch batch){
		for(Laser l:lasers){
			l.draw(batch);
		}
	}
	public void draw(ShapeRenderer batch){
		for(Laser l:lasers){
			l.draw(batch);
		}
	}
}
