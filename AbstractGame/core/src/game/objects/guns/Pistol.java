/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game.objects.guns;


import game.objects.GameObject;

/**
 *
 * @author esa
 */
public class Pistol extends Gun{

    public Pistol(GameObject shooter) {
        super(shooter);
        fireRate=0.0f;
        reloadtime=2f;
        clipSize=10;
        inClip=clipSize;
        ammo=500;
    }
    
}
