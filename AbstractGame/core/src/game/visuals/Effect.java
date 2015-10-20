package game.visuals;

import game.Content;
import game.objects.GameObject;
import game.objects.Updatable;
import game.states.PlayState;
import box2dLight.PointLight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Effect extends GameObject implements Updatable{
	
	public static final int EXPLOSION=0;
    public static final int ENTITY_HIT=1;
    public static final int OBJECT_HIT=2;
    
    private Color color;
    private float timer=0;
    private Animation animation;
    private PointLight light;
    private boolean lightOn=true;
	public Effect(PlayState state, Vector2 position, int type) {
		super(state,position);
		switch(type){
		case EXPLOSION:
			animation=Content.animations.get("Explosion");
			imgWidth=5;
			imgHeight=5;
			light=new PointLight(state.getRayHandler(),50, new Color(0.5f,0.5f,0.5f,0.5f), 4f, position.x, position.y);
			break;
		case ENTITY_HIT:
			animation=Content.animations.get("Explosion");
			imgWidth=1;
			imgHeight=1;
			color=Color.RED;
			break;
		case OBJECT_HIT:
			animation=Content.animations.get("Explosion");
			imgWidth=1;
			imgHeight=1;
			break;
			
		}
		curTexture=new Sprite(animation.getKeyFrame(animTime));
		animated=true;
		animLoc=position;
		
	}
	
	@Override
	public void init(Vector2 position) {
		//älä tee mittäännn
	}

	@Override
	public void update(float delta) {
		if(animation.isAnimationFinished(animTime)){
			setDestroyed(true);
		}
		if(lightOn&&animTime>0.1f&&light!=null){
			light.remove();
			lightOn=false;
		}
		
		curTexture=new Sprite(animation.getKeyFrame(animTime));
		animTime+=delta;
		
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		Color c=batch.getColor();
		batch.setColor(color==null? c:color);
		super.draw(batch);
		batch.setColor(c);
	}
	@Override
	public Vector2 getPosition() {
		return animLoc;
	}
	@Override
	public void setDestroyed(boolean destroyed) {
		
		super.setDestroyed(destroyed);
	}
}
