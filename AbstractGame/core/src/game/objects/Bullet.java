/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game.objects;

import game.Content;
import game.MyConst;
import game.states.PlayState;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;


/**
 *
 * @author esa
 */
public class Bullet extends GameObject implements Updatable{
	  
	float animTime=0;
	private boolean red=false;
    public Bullet(PlayState state, Vector2 position) {
        super(state, position);
        curTexture=new Sprite(Content.atlas.findRegion("Bullet"));
        imgWidth=(int)(fixture.getShape().getRadius()*2*5);
    	imgHeight=(int)(fixture.getShape().getRadius()*2*5);
        speed=20;
        dying=false;
    }
    
    @Override
	public void init(Vector2 position) {
		// First we create a body definition
    	BodyDef bodyDef = new BodyDef();
    	// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
    	bodyDef.type = BodyType.DynamicBody;
    	// Set our body's starting position in the world
    	bodyDef.position.set(position);
    	bodyDef.linearDamping=0f;
    	bodyDef.fixedRotation=true;
    	bodyDef.bullet=true;
    	body = state.getWorld().createBody(bodyDef);
    	
    	// Create a circle shape and set its radius to 6
    	CircleShape circle = new CircleShape();
    	circle.setRadius(0.1f);

    	// Create a fixture definition to apply our shape to
    	FixtureDef fixtureDef = new FixtureDef();
    	fixtureDef.shape = circle;
    	fixtureDef.density = 0.2f; 
    	fixtureDef.friction = 0.0f;
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
		
		if(dying){
			body.setActive(false);
			if(animation==null){
				imgHeight*=0.7f;
				imgWidth*=0.7f;
				animation=Content.animations.get("Explotion");
			}
			if(animation.isAnimationFinished(animTime)){
				destroyed=true;
				return;
			}
			
			curTexture=new Sprite(animation.getKeyFrame(animTime));
			
			animTime+=delta;
			return;
		}
		body.setLinearVelocity(direction.cpy().limit(speed));
		
	}
	@Override
	public void draw(SpriteBatch batch) {
		Color c=batch.getColor();
		if(red)batch.setColor(Color.RED);
		super.draw(batch);
		if(red)batch.setColor(c);
		
	}

    public void setRed(boolean red) {
		this.red = red;
	}
    
}
