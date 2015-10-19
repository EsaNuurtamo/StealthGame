/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game.objects.guns;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import game.Content;
import game.objects.GameObject;
import game.objects.Player;

/**
 *
 * @author esa
 */
public class Pistol extends Gun{

    public Pistol(GameObject shooter) {
        super(shooter);
        fireRate=0f;
        reloadtime=2f;
        clipSize=15;
        inClip=clipSize;
        ammo=5*clipSize;
        shootSFX=Content.getSound("shootPistol");
        reloadSFX=Content.getSound("reloadPistol");
    }
    
    @Override
    public void pullTrigger(Vector2 direction) {
    	if(!Gdx.input.justTouched()&&shooter instanceof Player)return;
    	super.pullTrigger(direction);
    }
    
}
