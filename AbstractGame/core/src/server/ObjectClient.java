package server;

import game.objects.GameObject;
import game.objects.organic.Player;
import game.states.PlayState;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import server.Network.RegisterObject;

public class ObjectClient {
	Client client;
	GameObject object;
	private PlayState state;
	public ObjectClient (PlayStatet state) {
		
		client = new Client();
		client.start();

		// For consistency, the classes to be sent over the network are
		// registered by the same method for both the client and server.
		Network.register(client);

		client.addListener(new Listener(){
			@Override
			public void connected(Connection connection) {
				
			}
			@Override
			public void received(Connection connection, Object object) {
				if (object instanceof RegisterObject) {
					RegisterObject registerObject = new RegisterObject();
					registerObject.object=(GameObject)object;
					client.sendTCP(registerObject);
					
				}

				if (object instanceof UpdateObject) {
					ui.updateCharacter((UpdateCharacter)object);
					return;
				}

				if (object instanceof RemoveCharacter) {
					RemoveCharacter msg = (RemoveCharacter)object;
					ui.removeCharacter(msg.id);
					return;
				}
				
			}
			@Override
			public void disconnected(Connection connection) {
				
			}
			
		});

		static class UI {
			HashMap<Integer, Character> characters = new HashMap();

			public String inputHost () {
				String input = (String)JOptionPane.showInputDialog(null, "Host:", "Connect to server", JOptionPane.QUESTION_MESSAGE,
					null, null, "localhost");
				if (input == null || input.trim().length() == 0) System.exit(1);
				return input.trim();
			}

			public String inputName () {
				String input = (String)JOptionPane.showInputDialog(null, "Name:", "Connect to server", JOptionPane.QUESTION_MESSAGE,
					null, null, "Test");
				if (input == null || input.trim().length() == 0) System.exit(1);
				return input.trim();
			}

			public String inputOtherStuff () {
				String input = (String)JOptionPane.showInputDialog(null, "Other Stuff:", "Create account", JOptionPane.QUESTION_MESSAGE,
					null, null, "other stuff");
				if (input == null || input.trim().length() == 0) System.exit(1);
				return input.trim();
			}

			public void addCharacter (Character character) {
				characters.put(character.id, character);
				System.out.println(character.name + " added at " + character.x + ", " + character.y);
			}

			public void updateCharacter (UpdateCharacter msg) {
				Character character = characters.get(msg.id);
				if (character == null) return;
				character.x = msg.x;
				character.y = msg.y;
				System.out.println(character.name + " moved to " + character.x + ", " + character.y);
			}

			public void removeCharacter (int id) {
				Character character = characters.remove(id);
				if (character != null) System.out.println(character.name + " removed");
			}
		}

		
		

	}

}
