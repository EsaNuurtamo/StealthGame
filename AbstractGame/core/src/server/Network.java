package server;

import java.util.ArrayList;

import game.objects.GameObject;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

// This class is a convenient place to keep things common to both the client and server.
public class Network {
	static public final int port = 54555;

	// This registers objects that are going to be sent over the network.
	static public void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(RegisterObject.class);
		kryo.register(UpdateObjects.class);
		
	}

	static public class RegisterObject {
		public GameObject gameObject;
	}
	
	static public class UpdateObjects {
		public ArrayList<GameObject> gameObjects;
	}
}
