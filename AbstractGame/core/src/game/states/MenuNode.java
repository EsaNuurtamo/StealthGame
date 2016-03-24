/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game.states;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Menu data structure class for keeping menus in tree-like 
 * structure (one parent and many children).
 * @author esa
 */
public class MenuNode {
    private MenuNode parent=null;
    private List<MenuNode> children=new ArrayList<MenuNode>();
    private String nodeName;
    private HashMap<String,Button> buttons=new HashMap<String,Button>();
    public MenuNode(String nodeName,MenuNode... child) {
        this.nodeName=nodeName;
        for(int i=0;i<child.length;i++){
            MenuNode n=child[i];
            n.setParent(this);
            children.add(n);
            
        }
    }

    public String getName() {
        return nodeName;
    }
    
    public List<MenuNode> getChildren() {
        return children;
    }

    public MenuNode getParent() {
        return parent;
    }

    public void setParent(MenuNode parent) {
        this.parent = parent;
    }
    
    public MenuNode getChild(String name){
        for(MenuNode n:children){
            if(name.equals(n.getName())){
                return n;
            }
        }
        return null;
    }
}
