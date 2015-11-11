package game.visuals;

import game.Content;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Laser {
	private Vector2 start;
	private Vector2 contact;
	private Vector2 normal;
	TextureRegion tr;
	
	public Laser(Vector2 start, Vector2 contact) {
		tr=Content.atlas.findRegion("Laser");
		if(tr==null)System.out.println("null");
	    this.start=start;
	    this.contact=contact;
	}
	
	public void draw(SpriteBatch batch){
		if(contact==null)return;
		Vector2 vect=contact.cpy().sub(start);
		batch.draw(
	    	tr, start.x, start.y-0.25f, 
            0, 0.5f/2, vect.len(), 0.5f, 1, 1, vect.angle()
	    );
	}
	
	public void draw(ShapeRenderer r){
		if(contact==null)return;
		Color c=r.getColor();
		r.setColor(Color.GREEN);
		r.rectLine(start, contact, 0.1f);
		r.setColor(c);
	}
}
