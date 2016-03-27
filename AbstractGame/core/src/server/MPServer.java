package server;

import game.objects.GameObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import server.Network.RegisterObject;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

public class MPServer {
	Server server;
	List<GameObject> objects = new ArrayList<GameObject>();
	
	public MPServer() throws IOException{
		Log.set(Log.LEVEL_DEBUG);
		
	}
	public void startServer() throws IOException{
		server=new Server(){
			protected Connection newConnection () {
				return new ObjectConnection();
			}
		};
		Network.register(server);
		server.addListener(new Listener() {
			public void received (Connection c, Object object) {
				// We know all connections for this server are actually CharacterConnections.
				ObjectConnection connection = (ObjectConnection)c;
				GameObject gameObject = connection.gameObject;

				if (object instanceof RegisterObject) {
					// Ignore if already logged in.
					if (gameObject != null) return;

				    gameObject = createObject();

					// Reject if couldn't load character.
					if (character == null) {
						c.sendTCP(new RegistrationRequired());
						return;
					}

					loggedIn(connection, character);
					return;
				}

			}

			private boolean isValid (String value) {
				if (value == null) return false;
				value = value.trim();
				if (value.length() == 0) return false;
				return true;
			}

			public void disconnected (Connection c) {
				ObjectConnection connection = (ObjectConnection)c;
				if (connection.object != null) {
					loggedIn.remove(connection.character);

					RemoveCharacter removeCharacter = new RemoveCharacter();
					removeCharacter.id = connection.character.id;
					server.sendToAllTCP(removeCharacter);
				}
			}
		});
		server.bind(Network.port);
		server.start();
	}
	static class ObjectConnection extends Connection {
		public GameObject gameObject;
	}
	
	public void createObject(){
		objects.add(new )
		
	}
	
	public void updateObjects () {
		// Collect the names for each connection.
		Connection[] connections = server.getConnections();
		ArrayList<GameObject> objects = new ArrayList<GameObject>();
		for (int i = connections.length - 1; i >= 0; i--) {
			ChatConnection connection = (ChatConnection)connections[i];
			names.add(connection.name);
		}
		
	}
	
    
}
