package game.states;

import java.util.Collections;

import game.Content;
import game.MyConst;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class PauseState implements Screen{
	
	private PlayState state;
	private Stage stage;
	private SpriteBatch batch;
    private Table table;
	private Game game;
    private TextField save;
    private MenuNode menu;
    
    public PauseState(Game g, PlayState state){
        this.game=g;
        this.state=state;
      
	}
    
    public void render (float delta) {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
		//????????????????mikä tämä on ?????????????
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
            if(menu.getParent()!=null){
                menu=menu.getParent();
                createMenu();
            }
        }
        stage.act();stage.draw();
    }
    public void createMenuTree(){
    	
    	menu=
    	        new MenuNode("Paused",new MenuNode("Resume"),
    	        		              new MenuNode("Options",     new MenuNode("Audio Options",   new MenuNode(Content.musicTags[0]),
    													                      							  new MenuNode(Content.musicTags[1]),
    														                		                      new MenuNode(Content.musicTags[2]),
    														                		                      new MenuNode(Content.musicTags[3]),
    														                		                      new MenuNode(Content.musicTags[4])),
    														              new MenuNode("Game Options"),
    	                                                                  new MenuNode("Credits")),
    	                              new MenuNode("Main Menu"));
    	
    	
	                	          
                //crete menu and focus on it
                createMenu();                            
                Gdx.input.setInputProcessor(stage); 
    }
	
	public void createMenu(){
        stage.clear();
        table.clear();
        table.align(Align.center);
        
        table.add(new Label(menu.getName(),MyConst.skin, "title")).height(75).row();
        
        
        
        //add all chilrdren
        for(MenuNode node:menu.getChildren()){
                
        	createButton(node.getName());
        }
        
        if(menu.getName().equals("Audio Options")){
        	createSliders();
        }
        
        table.setFillParent(true);
        stage.addActor(table);
     }
	
	public void createSliders(){
		Label labelmusic=new Label("Music volume" ,MyConst.skin, "score");
		table.add(labelmusic).size(350, 100).row();
		table.row();
		
    	final Slider slider1 = new Slider(0, 1, 0.1f, false, MyConst.skin);
    	
		slider1.setValue(state.getCurMusic().getVolume());
		slider1.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				state.getCurMusic().setVolume(slider1.getValue());
			}
		});
		table.add(slider1).size(350, 30).row();
		labelmusic.invalidate();
		
		
		
		Label labelsfx=new Label("SFX volume" ,MyConst.skin, "score");
		table.add(labelsfx).size(350, 100).row();
		table.row();
		
    	final Slider slider2 = new Slider(0, 1, 0.1f, false, MyConst.skin);
		slider2.setValue(MyConst.SFXvol);
		slider2.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				MyConst.SFXvol=slider2.getValue();
			}
		});
		table.add(slider2).size(350, 30).row();
		labelsfx.invalidate();
		
    }
	public void createButton(String name){
        
        TextButton button = new TextButton(name, MyConst.skin);
        
        createListener(button);
        table.add(button).size(350, 100).row();
        
    }
    
    //defining actions for different buttons
    public void createListener(TextButton button){
        button.addListener(new ClickListener(){
        
        @Override
        public void clicked(InputEvent event, float x, float y) {
            TextButton t=(TextButton)event.getListenerActor();
            String tag=t.getLabel().getText().toString();
            
            if(tag.equals("Main Menu")){
            	Gdx.app.log("User", "Clicked Main Menu");
            	game.setScreen(new MenuState(game));
            	dispose();
            }else 
            if(tag.equals("Resume")){
                Gdx.app.log("User", "Pressed RESUME");
            	game.setScreen(state);
            	state.getCurMusic().play();
            	state.playing=true;
    			dispose();
            }else 
            if(menu.getName().equals("Audio Options")){//audio option
            	Gdx.app.log("User", "In AUDIO OPTIONS");
            	
            	for(int i=0;i<Content.musicTags.length;i++){//here you can change music from audiooptions
            		if(tag.equals(Content.musicTags[i])){
            			Gdx.app.log("User", "Clicked "+Content.musicTags[i]);
            			state.changeMusic(Content.music.get(Content.musicTags[i]));
            		}
            	}
            }else{
            	//if there is no menu left, dont go there
            	if(menu.getChild(tag)==null){
                    return;
                }
                menu=menu.getChild(tag);//make pressed item new menu
                createMenu();
            }
            
            
            
        }
        });
    }

	@Override
	public void show() {
		
			table=new Table();
	        stage = new Stage(new FitViewport(MyConst.APP_WIDTH, MyConst.APP_HEIGHT));
			createMenuTree();
	    
		
	}

	@Override
	public void resize (int width, int height) {
		stage.getViewport().update(width, height, false);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose () {
		stage.dispose();
    }

}
