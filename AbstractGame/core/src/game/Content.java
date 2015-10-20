/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;


public class Content {
        public static HashMap<String, Texture> textures=new HashMap<String, Texture>();
	public static HashMap<String, Music> music= new HashMap<String, Music>();
	public static HashMap<String, Sound> sounds= new HashMap<String, Sound>();
	public static TextureAtlas atlas=new TextureAtlas(Gdx.files.internal("images/game.atlas"));
	public static HashMap<String,Animation> animations=new HashMap<String, Animation>();
	
	public static void loadAll(){
		loadSound("sound/Pistol.wav", "shootPistol");
		loadSound("sound/MG.wav", "shootMG");
		loadSound("sound/Shotgun.wav", "shootShotgun");
		loadSound("sound/PistolReload.wav", "reloadPistol");
		loadSound("sound/MGReload.wav", "reloadMG");
		loadSound("sound/ShotgunReload.wav", "reloadShotgun");
		loadSound("sound/Explosion.wav", "explosion");
		
		loadMusic("sound/MainTheme.ogg", "mainTheme");
		
	}
	/***********/
	/* Texture */
	/***********/
	
	public static void loadAnimations(){
		
		TextureRegion r=atlas.findRegion("Explosion");
		TextureRegion[][] grid=r.split(64, 64);
		Animation a=new Animation(0.045f, grid[0]);
		a.setPlayMode(PlayMode.NORMAL);
		animations.put("Explosion", a);
		
		r=atlas.findRegion("EnemyDeath");
		grid=r.split(64, 128);
		a=new Animation(0.06f, grid[0]);
		a.setPlayMode(PlayMode.NORMAL);
		animations.put("EnemyDeath", a);
	}
    
	
	public static void loadTexture(String path) {
		int slashIndex = path.lastIndexOf('/');
		String key;
		if(slashIndex == -1) {
			key = path.substring(0, path.lastIndexOf('.'));
		}
		else {
			key = path.substring(slashIndex + 1, path.lastIndexOf('.'));
		}
		loadTexture(path, key);
	}
	public static void loadTexture(String path, String key) {
		Texture tex = new Texture(Gdx.files.internal(path));
		
		
		textures.put(key, tex);
	}
	public static Texture getTexture(String key) {
		return textures.get(key);
	}
	public static void removeTexture(String key) {
		Texture tex = textures.get(key);
		if(tex != null) {
			textures.remove(key);
			tex.dispose();
		}
	}
	
	/*********/
	/* Music */
	/*********/
	
	public static void loadMusic(String path) {
		int slashIndex = path.lastIndexOf('/');
		String key;
		if(slashIndex == -1) {
			key = path.substring(0, path.lastIndexOf('.'));
		}
		else {
			key = path.substring(slashIndex + 1, path.lastIndexOf('.'));
		}
		loadMusic(path, key);
	}
	public static void loadMusic(String path, String key) {
		Music m = Gdx.audio.newMusic(Gdx.files.internal(path));
		music.put(key, m);
	}
	public static Music getMusic(String key) {
		return music.get(key);
	}
	public static void removeMusic(String key) {
		Music m = music.get(key);
		if(m != null) {
			music.remove(key);
			m.dispose();
		}
	}
	
	/*******/
	/* SFX */
	/*******/
	
	public static void loadSound(String path) {
		int slashIndex = path.lastIndexOf('/');
		String key;
		if(slashIndex == -1) {
			key = path.substring(0, path.lastIndexOf('.'));
		}
		else {
			key = path.substring(slashIndex + 1, path.lastIndexOf('.'));
		}
		loadSound(path, key);
	}
	public static void loadSound(String path, String key) {
		Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
		sounds.put(key, sound);
	}
	public static Sound getSound(String key) {
		return sounds.get(key);
	}
	public static void removeSound(String key) {
		Sound sound = sounds.get(key);
		if(sound != null) {
			sounds.remove(key);
			sound.dispose();
		}
	}
	
	/*********/
	/* other */
	/*********/
	
	public static void removeAll() {
		/*Iterator<Map.Entry<String, Texture>> iter1 = textures.entrySet().iterator();
		while(iter1.hasNext()) {
			Texture tex = iter1.next().getValue();
			tex.dispose();
			iter1.remove();
		}
		Iterator<Map.Entry<String, Music>> iter2 = music.entrySet().iterator();
		while(iter2.hasNext()) {
			Music music = iter2.next().getValue();
			music.dispose();
			iter2.remove();
		}
		Iterator<Map.Entry<String, Sound>> iter3 = sounds.entrySet().iterator();
		while(iter3.hasNext()) {
			Sound sound = iter3.next().getValue();
			sound.dispose();
			iter3.remove();
		}*/
		for(Object o : textures.values()) {
			Texture tex = (Texture) o;
			tex.dispose();
		}
		textures.clear();
		for(Object o : music.values()) {
			Music music = (Music) o;
			music.dispose();
		}
		music.clear();
		for(Object o : sounds.values()) {
			Sound sound = (Sound) o;
			sound.dispose();
		}
		sounds.clear();
	}
        
        public static void clearMapFiles(String save){
        FileHandle file=Gdx.files.local("bin/map/"+save);
        
        for(FileHandle h:file.list()){
            h.delete();
        }
    }
}
