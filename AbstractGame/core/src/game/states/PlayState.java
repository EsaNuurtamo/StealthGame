package game.states;

import game.Content;
import game.MyConst;
import game.ai.EnemyState;
import game.gui.Hud;
import game.input.Mouse;
import game.objects.Box;
import game.objects.Bullet;
import game.objects.Enemy;
import game.objects.GameObject;
import game.objects.KamikazeRobot;
import game.objects.Pickable;
import game.objects.Player;
import game.objects.Turret;
import game.objects.Updatable;
import game.tools.Contacts;
import game.tools.MapBodyBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xguzm.pathfinding.gdxbridge.NavTmxMapLoader;

import box2dLight.ChainLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * 
 * @author esa
 *
 */
public class PlayState implements Screen{
	//Objects
	private boolean inLight=true;
	private Player player;
	public List<GameObject> objects;
	private List<GameObject> objsToAdd;
	private TiledMap map;
	private TiledMapRenderer mapRenderer;
	private SpriteBatch batch;
	private SpriteBatch fontbatch;
    private OrthographicCamera camera;
    private OrthographicCamera cameraFont;
    private Viewport viewport;
    private BitmapFont font=new BitmapFont();
    private RayCastCallback callback;
    public static GameObject nearestSeen=null;
    private ShapeRenderer srenderer;
    public Vector2 vec1=new Vector2();
    public Vector2 vec2=new Vector2();
    private List<Vector2> linjat=new ArrayList<Vector2>();
    //Gamehandler;
    private Game game;
    private float camOffset=0;
    //Box2d
    private World world=new World(new Vector2(0, 0), true);
    private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
    private RayHandler rayHandler; 
    private List<Light> lights=new ArrayList<Light>();
    
    //hud
    private Hud hud;
    public PlayState(Game g) {
    	
        this.game=g;  
        RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);
		Content.loadAnimations();
		MyConst.createSkin();
		Content.loadAll();
		camOffset=0;
    }
    
    @Override
    public void show() {
    	
    	loadResources();
    	//Content.music.get("mainTheme").play();
    	//map = new TmxMapLoader().load("maps/Test.tmx");
    	map=new TmxMapLoader().load("maps/Map.tmx");
    	mapRenderer=new OrthogonalTiledMapRenderer(map,1/(MyConst.ORG_TILE_WIDTH*MyConst.TILES_IN_M));
        camera=new OrthographicCamera(MyConst.APP_WIDTH/MyConst.PIX_IN_M*MyConst.VIEW_SCALE, MyConst.APP_HEIGHT/MyConst.PIX_IN_M*MyConst.VIEW_SCALE);
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
		player=new Player(this,new Vector2(10,7));
		
        objects=new ArrayList<GameObject>();
        objects.add(player);
        objects.add(new Turret(this,new Vector2(7,7)));
        objects.add(new KamikazeRobot(this, new Vector2(16,6)));
		createEnemies();
		objsToAdd=new ArrayList<GameObject>();
		
        camera.position.set(player.getPosition(), camera.position.z);
	    camera.update();
	    
	    Pixmap pm = new Pixmap(Gdx.files.internal("images/Crosshair.png"));
	    //Gdx.input.setCursorImage(pm, (int)(pm.getWidth()/2), (int)(pm.getHeight()/2));
	    
	    pm.dispose();
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
    	Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(Gdx.input.isKeyPressed(Keys.ESCAPE)){
        	System.exit(0);
        }
        if(Gdx.input.isKeyJustPressed(Keys.L)){
        	dispose();
        	game.setScreen(new PlayState(game));
        }
        
        //enable zooming with shift
        /*if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)){
			camOffset+=20*delta;
			if(camOffset>10)camOffset=10;
		}else{
			camOffset-=20*delta;
			if(camOffset<0)camOffset=0;
			
		}*/
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
     * Creates one enemy for every path on the map
     */
    public void createEnemies(){
    	MapBodyBuilder.buildObjects(map, this);
    	for(Vector2[] vect:MapBodyBuilder.buildPaths(map)){
    		final Vector2 v=vect[0];
    		/*Timer.schedule(new Task(){

				@Override
				public void run() {
					addObj(new Enemy(get(),v));
					
				}
    			
    		}, 0f,5f);*/
    		float[] ooho=new float[vect.length*2];
    		for(int i=0;i<vect.length;i++){
    			ooho[i*2]=vect[i].x;
    			ooho[i*2+1]=vect[i].y;
    		}
    		//lights.add(new ChainLight(rayHandler, 50, new Color(1f,1f,1f,0.6f), 1,1,ooho));
    		Enemy e=new Enemy(this, vect[0]);
    		e.setPatrolPath(vect);
    		objects.add(e);
    	}
    	//Box b=new Box(this, new Vector2(player.getPosition().x, player.getPosition().y));
    	//objects.add(b);
    }

    @Override
    public void resize(int w, int h) {
    	//viewport.update((int)(w/(MyConst.PIX_IN_M*MyConst.VIEW_SCALE)), (int)(h/MyConst.PIX_IN_M*MyConst.VIEW_SCALE));
        viewport.update(w, h);   
    }

    

    @Override
    public void hide() {
        
    }

    @Override
    public void pause() {
       
    }

    @Override
    public void resume() {
        
    }

    @Override
    public void dispose() {
        Content.removeAll();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }


    
    
    
    
    
    
   
    
    public void loadResources(){
       
        
        
        
    }

    
    public World getWorld() {
		return world;
	}
    
    
    public List<GameObject> getObjects() {
    	return objects;
    }

   public void setRayHandler(RayHandler rayHandler) {
	this.rayHandler = rayHandler;
   }
   public RayHandler getRayHandler() {
	return rayHandler;
   }
   public Viewport getViewport() {
	return viewport;
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
   
   public boolean isInLight(){
	   return inLight;
   }
   
   public SpriteBatch getBatch() {
	return batch;
}
   
   public ShapeRenderer getSrenderer() {
	return srenderer;
}
        
}
