package game.states;

import game.Content;
import game.MyConst;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * MenuScreen is the main menu screen that shows one menu page.
 * Has access to all the menu pages through MenuNode menu.
 * @author esa
 *
 */
public class MenuState implements Screen {
	
	private String level="Test";
	private Stage stage;
	private SpriteBatch batch;
    private Table table;
	private Game game;
    private TextField save;
    private MenuNode menu;
    
    public MenuState(Game g){
        this.game=g;
    }

	public void render (float delta) {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
            if(menu.getParent()!=null){
                menu=menu.getParent();
                createMenu();
            }
        }
        stage.act();stage.draw();
    }
	
	@Override
	public void resize (int width, int height) {
		stage.getViewport().update(width, height, false);
	}

	@Override
	public void dispose () {
		stage.dispose();
    }

	
	//menu in tree formation
	@Override
	public void show() {
		table=new Table();
        stage = new Stage(new FitViewport(MyConst.APP_WIDTH, MyConst.APP_HEIGHT));
		createMenuTree();
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
        
     
    public void createDropDownSelect(){
    	
    }
    /**
     * Creates a menu as a tree form
     */
    public void createMenuTree(){
    	menu=
        new MenuNode("Unknown Depths",new MenuNode("New Game",    new MenuNode("Easy"),
        		                                                  new MenuNode("Medium"),
        		                                                  new MenuNode("Hard")),
        		                      
        		                      new MenuNode("Level Select",new MenuNode("Test"),
				                                                  new MenuNode("Map"),
				                                                  new MenuNode("Tutorial")),
				                                                  
        							  new MenuNode("Options",     new MenuNode("Audio Options",    new MenuNode(Content.musicTags[0]),
												                      							  new MenuNode(Content.musicTags[1]),
													                		                      new MenuNode(Content.musicTags[2]),
													                		                      new MenuNode(Content.musicTags[3]),
													                		                      new MenuNode(Content.musicTags[4])),
													              new MenuNode("Game Options"),
                                                                  new MenuNode("Credits")),
                                      new MenuNode("Exit"));
        createMenu();                        
        Gdx.input.setInputProcessor(stage); 
    }
    
    /**
     * Method cleas and creates stage and table for stage. Menu has all the labels ect.
     * This is called Every time something in the menu is changed NOT every frame :)
     */
    public void createMenu(){
    	
    	stage.clear();
        table.clear();
        table.align(Align.center);
    	
        //headline
        table.add(new Label(menu.getName(),MyConst.skin, "title")).height(100).row();
    	
    	
        //////////////////////////////////////////////////////
        //SCROLLDOWN VALIKKO LEVUJEN JA SEIVIEN SELAAMIIISEN//
        /////////////////////////////////////////////////////
        
        
        //add all buttons to tables
        for(MenuNode node:menu.getChildren()){
           createButton(node.getName());
        }
        table.setFillParent(true);
        stage.addActor(table);
     }
        
        /**
         * Creates new button and attaches it to table,
         * @param name is the label of the button.
         */
        public void createButton(String name){
            
            TextButton button = new TextButton(name, MyConst.skin);
            
            createListener(button);
            table.add(button).size(350, 100).row();
            
        }
        
        
        
        /**
         * Creates identical clicklistener to every button. Listener then has a switch to decide the action related to certain buttons
         * behaviour depending the button tag. BAD CODING CAUSE EVERY LISTENER INSTANCE HAS ALL FUNCTIONILTY
         * @param button 
         */
        public void createListener(TextButton button){
            
        	button.addListener(new ClickListener(){
                
        		public void clicked(InputEvent event, float x, float y) {
                    TextButton t=(TextButton)event.getListenerActor();
                    String tag=t.getLabel().getText().toString();
                    
                    //Exiting from game
                    if(tag.equals("Exit")){Gdx.app.exit();}
                    
                    //New Game menu functionality
                    if(menu.getName().equals("New Game")){
	                    int difficulty;
	                	if(tag.equals("Easy")){difficulty=0;}
	                	else if(tag.equals("Normal")){difficulty=1;}
	                	else if(tag.equals("Hard")){difficulty=2;}
	                	else if(tag.equals("Impossible")){difficulty=3;}
	                	game.setScreen(new PlayState(game));
	                	dispose();
                    }else 
                    if(menu.getName().equals("Level Select")){
                    	for(int i=0;i<Content.levelTags.length;i++){//here you can change level
                    		if(tag.equals(Content.levelTags[i])){
                    			Gdx.app.log("User", "Clicked "+Content.musicTags[i]);
                    			game.setScreen(new PlayState(game, Content.levelTags[i]));
        	                	dispose();
                    		}
                    	}
                    }else{
                    	//if there is no menu left, dont try go there
                    	if(menu.getChild(tag)==null){
                            return;
                        }
                        menu=menu.getChild(tag);//make pressed item new menu
                        createMenu();
                    }
                }
            });
        }
}
