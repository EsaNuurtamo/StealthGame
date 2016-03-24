package game.objects.guns;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import game.Content;
import game.objects.GameObject;
import game.objects.organic.Player;

public class Shotgun extends Gun{

	public Shotgun(GameObject shooter) {
		super(shooter);
		fireRate=0f;
        reloadtime=4f;
        clipSize=5000;
        inClip=5000;
        ammo=clipSize*1;
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
	
	@Override
	public void pullTrigger(Vector2 direction) {
		//only shoot once
		if(!Gdx.input.justTouched()&&shooter instanceof Player)return;
		super.pullTrigger(direction);
	}
	

}
