package game.objects.guns;

import com.badlogic.gdx.math.Vector2;

import game.Content;
import game.objects.GameObject;

public class Shotgun extends Gun{

	public Shotgun(GameObject shooter) {
		super(shooter);
		fireRate=1f;
        reloadtime=4f;
        clipSize=6;
        inClip=clipSize;
        ammo=clipSize*5;
        shootSFX=Content.getSound("shootShotgun");
        reloadSFX=Content.getSound("reloadShotgun");
	}
	
	@Override
	public void shoot(Vector2 direction) {
		float total=35;
		Vector2 v=direction.cpy().rotate(-total/2);
		
		for(int i=0;i<10;i++){
			super.shoot(v.cpy());
			v.rotate(total/10);
			
		}
		
		
	}
	

}
