package game.states;

import game.Content;
import game.MyConst;
import game.gui.Hud;
import game.input.Mouse;
import game.map.MapBodyBuilder;
import game.objects.GameObject;
import game.objects.Updatable;
import game.objects.organic.Enemy;
import game.objects.organic.Player;
import game.objects.robotic.KamikazeRobot;
import game.objects.robotic.Turret;
import game.tools.Contacts;
import game.tools.MovingCamera;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import box2dLight.Light;
import box2dLight.RayHandler;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * 
 * @author esa
 *
 */
public class PlayState implements Screen{
	//flags
	public boolean playing=false;
	public boolean started=false;
	private String level="Map";
	private boolean inLight=true;
	private Music curMusic;
	//objects
	private Player player;
	public List<GameObject> objects;
	private List<GameObject> objsToAdd;
	
	//map, and drawing
	private TiledMap map;
	private TiledMapRenderer mapRenderer;
	private SpriteBatch batch;
	private ShapeRenderer srenderer;
    
    //camera and viewport
	private float camOffset;
    private OrthographicCamera camera;
    
    private Viewport viewport;
    
    
    //Gamehandler;
    private Game game;
    
    
    //Box2d
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private RayHandler rayHandler; 
    private List<Light> lights=new ArrayList<Light>();
    private RayCastCallback callback;
    
    //hud
    private Hud hud;
    public PlayState(Game g){
    	this(g,"Map");
    }
    public PlayState(Game g, String lvl) {
    	this.level=lvl;
        this.game=g; 
        MyConst.createSkin();
		
		
		Content.loadAll();
		
		
		
        
        //init box2d
        world=new World(new Vector2(0, 0), true);
        debugRenderer = new Box2DDebugRenderer();
        
        //lights
        lights=new ArrayList<Light>();
        RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);
		
		
		//camera setup
		camOffset=0;
		
    }
   
    @Override
    public void show() {
    	if(started)return;
    	started=true;
    	Gdx.app.debug("Report", "started PlayState.show()");
    	//if map hasnth been loaded yet
        curMusic=Content.music.get("Wooshing Up");
        curMusic.setVolume(0.5f);
        curMusic.setLooping(true);
    	//curMusic.play();
    	
    	//songs=(List<Music>)Content.music.values();
    	map = new TmxMapLoader().load("maps/"+level+".tmx");
    	
    	mapRenderer=new OrthogonalTiledMapRenderer(map,1/(MyConst.ORG_TILE_WIDTH*MyConst.TILES_IN_M));
        camera=new MovingCamera(MyConst.APP_WIDTH/MyConst.PIX_IN_M*MyConst.VIEW_SCALE, MyConst.APP_HEIGHT/MyConst.PIX_IN_M*MyConst.VIEW_SCALE, this);
        viewport=new FitViewport(MyConst.APP_WIDTH/MyConst.PIX_IN_M*MyConst.VIEW_SCALE, MyConst.APP_HEIGHT/MyConst.PIX_IN_M*MyConst.VIEW_SCALE, camera);
        batch=new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        srenderer=new ShapeRenderer();
        srenderer.setProjectionMatrix(camera.combined);
        hud=new Hud(this);
		Array<Body> bodies=MapBodyBuilder.buildTiles(map, world,this);
		rayHandler = new RayHandler(world); 	
		rayHandler.setAmbientLight(0.05f, 0.05f, 0.05f, 0.2f);
		rayHandler.setBlurNum(3);
		MapBodyBuilder.buildLights(map, rayHandler,this);
		
		
		Light.setGlobalContactFilter(MyConst.CATEGORY_BULLETS,(short)0,MyConst.MASK_LIGHTS);
		world.setContactListener(new Contacts(this));
		world.setWarmStarting(true);
		MapBodyBuilder.initFinder(map);
		//object creation
		
		
		
        objects=new ArrayList<GameObject>();
        objsToAdd=new ArrayList<GameObject>();
        
        //objects.add(new Turret(this,new Vector2(7,7)));
        //objects.add(new KamikazeRobot(this, new Vector2(16,6)));
		createPeople();
		
        
        camera.position.set(player.getPosition(), camera.position.z);
	    camera.update();
	    
	    //Pixmap pm = new Pixmap(Gdx.files.internal("images/Crosshair.png"));
	    //Gdx.input.setCursorImage(pm, (int)(pm.getWidth()/2), (int)(pm.getHeight()/2));
	    
	    //pm.dispose();
    }
    
    public boolean isInView(Vector2 vect){
    	Vector2 v=new Vector2(camera.position.x,camera.position.y);
    	if(Math.abs(v.x-vect.x)>camera.viewportWidth/2+150||
     	   Math.abs(v.y-vect.y)>camera.viewportHeight/2+150)
     	{
    		return false;
     	}else{
     		return true;
     	}
    	
    }
    
    public void drawComponents(float delta){
    	mapRenderer.render();
        debugRenderer.render(world, camera.combined);
    	
      
        
        //----------------Sprites--------------------------------
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        for(GameObject obj:objects){
    		if(!isInView(obj.getPosition())||obj instanceof Player)
    		{
    			continue;
    		}
        	obj.draw(batch);
        }
        player.draw(batch);
    	
        batch.end();
        //-------------------------------------------------------
        
        
        //draw lights
        rayHandler.setCombinedMatrix(camera.combined);
        rayHandler.updateAndRender();
        
        //draw lasers ect. on top of light layer
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for(GameObject obj:objects){
    		if(!isInView(obj.getPosition()))
    		{
    			continue;
    		}
        	obj.drawEffects(batch);
        }
        
        batch.end();
        
        
        
        //----------------Shapes--------------------------------
        srenderer.setProjectionMatrix(camera.combined);
        srenderer.begin(ShapeType.Filled);
        srenderer.setColor(Color.WHITE);
        for(GameObject obj:objects){
        	if(!isInView(obj.getPosition()))
    		{
    			continue;
    		}
        	obj.drawShape(srenderer);
        	player.debugDraw(srenderer);
        }
        srenderer.end();
        //------------------------------------------------------
        
        hud.draw();
    	
    }
    
    @Override
    public void render(float delta) {
    	if(playing=false)return;
    	
    	Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
        	game.setScreen(new PauseState(game,this));
        	curMusic.pause();
        	return;
        }
        
        //enable zooming with shift
        /*if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)){
			camOffset+=20*delta;
			if(camOffset>10)camOffset=10;
		}else{
			camOffset-=20*delta;
			if(camOffset<0)camOffset=0;
			
		}*/
        if(camera==null)return;
        Vector2 vect=new Vector2(camera.position.x,camera.position.y);
        if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)){
        	/*Vector2 neew=vect.cpy().add(Mouse.getWorldPos(camera).sub(vect).nor().scl(10*delta));
        	camera.position.set(neew, camera.position.z);
        	if(neew.dst(player.getPosition())>10){
        		camera.position.set(vect, camera.position.z);
        	}*/
        	camera.position.set(Mouse.getWorldPos(camera).sub(player.getPosition()).scl(0.5f).add(player.getPosition()), camera.position.z);
		}else{
			
			camera.position.set(player.getPosition(), camera.position.z);
		}
        //camera.position.set(player.getPosition().cpy().add(player.getDirection().cpy().nor().scl(camOffset).limit(10)), camera.position.z);
        
	    camera.update();
	    
	    
        mapRenderer.setView(camera);
        
        drawComponents(delta); 
	    
        
        world.step(delta, 6, 2);
	    world.clearForces();
	    updateObjects(delta);
	    
    }
    
    public boolean isPlayerInLight(){
    	for(Light pl:lights){
    		if(pl.contains(player.getPosition().x, player.getPosition().y))return true;
    	}
    	return false;
    }
    
    public void updateObjects(float delta){
    	
    	if(isPlayerInLight()){
    		  inLight=true;
    	}else inLight=false;
    	
    	Iterator i=objects.iterator();
        while(i.hasNext()){
        	GameObject obj=(GameObject)i.next();
        	
        	if(obj.isDestroyed()){
        		obj.dispose();
        		i.remove();
        		if(obj.getBody()!=null)world.destroyBody(obj.getBody());
        		
        	}else if(obj instanceof Updatable){
        		((Updatable)obj).update(delta);
        	}
        }
        objects.addAll(objsToAdd);
        objsToAdd.clear();
    }
    
    public void addObj(GameObject obj){
    	objsToAdd.add(obj);
    	
    }
    public PlayState get(){
    	return this;
    }
    
    /**
     * Creates one enemy for every path on the map. SEPARATE FACTORY CLASS ON THE MAKING
     */
    public void createPeople(){
    	MapBodyBuilder.buildObjects(map, this);
    	player=new Player(this,MapBodyBuilder.playerLoc);
		objects.add(player);
    	for(Vector2[] vect:MapBodyBuilder.buildPaths(map)){
    		float[] ooho=new float[vect.length*2];
    		for(int i=0;i<vect.length;i++){
    			ooho[i*2]=vect[i].x;
    			ooho[i*2+1]=vect[i].y;
    		}
    		
    		Enemy e=new Enemy(this, vect[0]);
    		e.setPatrolPath(vect);
    		objects.add(e);
    	}
    	//Box b=new Box(this, new Vector2(player.getPosition().x, player.getPosition().y));
    	//objects.add(b);
    }
    
    //implemented from interface from Screen
    @Override
    public void resize(int w, int h) {
    	//viewport.update((int)(w/(MyConst.PIX_IN_M*MyConst.VIEW_SCALE)), (int)(h/MyConst.PIX_IN_M*MyConst.VIEW_SCALE));
    	if(viewport==null)return;
        viewport.update(w, h);   
    }
    
    public void changeMusic(Music newMusic){
    	curMusic.stop();
    	curMusic.dispose();
    	curMusic=newMusic;
    	curMusic.setLooping(true);
    }
    public void hide() {
        
    }
    public void pause() {
       
    }
    @Override
    public void resume() {
        
    }
    public void dispose() {
        Content.removeAll();
    }
    ///////////////////////////////////////
    
    //Getters
    public Music getCurMusic() {
		return curMusic;
	}
    public MovingCamera getCamera() {
        return (MovingCamera)camera;
    }
    public World getWorld() {
		return world;
	}
    public List<GameObject> getObjects() {
    	return objects;
    }
    public RayHandler getRayHandler() {
    	return rayHandler;
    }
    public Player getPlayer() {
    	return player;
       }
    public TiledMap getMap() {
    	return map;
    }
    public List<Light> getLights() {
    	   return lights;
       }
    public SpriteBatch getBatch() {
    	return batch;
    }
    public ShapeRenderer getSrenderer() {
    	return srenderer;
    }
    public String getlevel() {
    	return level;
    }
    public Viewport getViewport() {
    	return viewport;
       }
    public boolean isInLight(){
 	   return inLight;
    }
    //Setters
    public void setRayHandler(RayHandler rayHandler) {
	   this.rayHandler = rayHandler;
   }
    public void setlevel(String levelpath) {
    	this.level=level;
    }
    
    public void setCurMusic(Music curMusic) {
		this.curMusic = curMusic;
	}
    
}
