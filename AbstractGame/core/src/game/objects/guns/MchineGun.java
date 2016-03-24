package game.objects.guns;

import game.Content;
import game.objects.GameObject;

public class MchineGun extends Gun{
	public MchineGun(GameObject shooter) {
        super(shooter);
        fireRate=0.1162f;
        reloadtime=3f;
        clipSize=5000;
        inClip=5000;
        ammo=5000;
        shootSFX=Content.getSound("shootMG");
        reloadSFX=Content.getSound("reloadMG");
    }
}
