package game.objects.guns;

import game.Content;
import game.objects.GameObject;

public class MchineGun extends Gun{
	public MchineGun(GameObject shooter) {
        super(shooter);
        fireRate=0.1f;
        reloadtime=3f;
        clipSize=50;
        inClip=0;
        ammo=0;
        shootSFX=Content.getSound("shootMG");
        reloadSFX=Content.getSound("reloadMG");
    }
}
