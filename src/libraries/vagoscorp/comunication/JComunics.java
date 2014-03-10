package libraries.vagoscorp.comunication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import javax.swing.SwingWorker;

import libraries.vagoscorp.comunication.Eventos.OnComunicationListener;
import libraries.vagoscorp.comunication.Eventos.OnConnectionListener;
import libraries.vagoscorp.comunication.Eventos.OnDisConnectionListener;

/**
 * The Class JComunic: Recurso de Comunicaci�n para Java basado en Sockets
 */
public class JComunics extends SwingWorker<Integer, byte[]> {

	/** Constante de Estado o Tipo de Conexi�n: Nulo. */
	public final int NULL = 0;// estado
	
	/** Constante de Estado: En Espera. */
	public final int WAITING = 1;// estado
	
	/** Constante de Estado: Conectado. */
	public final int CONNECTED = 2;// estado

	/** Constante de Tipo de Conexi�n: Cliente. */
	public final int CLIENT = 1;// tcon
	
	/** Constante de Tipo de Conexi�n: Servidor. */
	public final int SERVER = 2;// tcon

	/** The en espera. */
	final byte[] EN_ESPERA = { 1 };
	
	/** The conectado. */
	final byte[] CONECTADO = { 2 };
	
	/** The io exception. */
	final byte[] IO_EXCEPTION = { 3 };
	
	/** The dato recivido. */
	final byte[] DATO_RECIBIDO = { 7 };
	
	/** The con killer. */
	final String conKiller = "AZAzaZAZ";

	/** The isa. */
	InetSocketAddress isa;
	
	/** The s port. */
	int sPort = 2000;
	
	/** The socket. */
	Socket socket;
	
	/** The server socket. */
	ServerSocket serverSocket;
	
	/** The input st. */
	DataInputStream inputSt;
	
	/** The output st. */
	DataOutputStream outputSt;

	/** El Tipo de Conexi�n Actual:
	 *  NULL, CLIENT � SERVER. */
	public int tcon = NULL;
	
	/** The conectado. */
	boolean conectado = false;
	
	/** El Estado de la Conexi�n Actual:
	 * NULL, WAITING � CONNECTED. */
	public int estado = NULL;

	// ///////////////C�digo para Listeners/////////////////
	/** The on conn listener. */
	OnConnectionListener onConnListener;
	
	/** The on dis conn listener. */
	OnDisConnectionListener onDisConnListener;
	
	/** The on com listener. */
	OnComunicationListener onCOMListener;

//	/**
//	 * The listener interface for receiving onComunication events.
//	 * The class that is interested in processing an onComunication
//	 * event implements this interface, and the object created
//	 * with that class is registered with a component using the
//	 * component's <code>addOnComunicationListener<code> method. When
//	 * the onComunication event occurs, that object's appropriate
//	 * method is invoked.
//	 *
//	 * @see OnComunicationEvent
//	 */
//	public interface OnComunicationListener {
//		
//		/**
//		 * On data received.
//		 *
//		 * @param dato the dato
//		 */
//		public void onDataReceived(String dato);
//	}
//
//	/**
//	 * The listener interface for receiving onConnection events.
//	 * The class that is interested in processing an onConnection
//	 * event implements this interface, and the object created
//	 * with that class is registered with a component using the
//	 * component's <code>addOnConnectionListener<code> method. When
//	 * the onConnection event occurs, that object's appropriate
//	 * method is invoked.
//	 *
//	 * @see OnConnectionEvent
//	 */
//	public interface OnConnectionListener {
//		
//		/**
//		 * On connectionstablished.
//		 */
//		public void onConnectionstablished();
//
//		/**
//		 * On connectionfinished.
//		 */
//		public void onConnectionfinished();
//	}
//
//	/**
//	 * The listener interface for receiving onDisConnection events.
//	 * The class that is interested in processing an onDisConnection
//	 * event implements this interface, and the object created
//	 * with that class is registered with a component using the
//	 * component's <code>addOnDisConnectionListener<code> method. When
//	 * the onDisConnection event occurs, that object's appropriate
//	 * method is invoked.
//	 *
//	 * @see OnDisConnectionEvent
//	 */
//	public interface OnDisConnectionListener {
//		
//		/**
//		 * On connectionfinished.
//		 */
//		public void onConnectionfinished();
//	}

	/**
	 * Sets the connection listener.
	 *
	 * @param connListener the new connection listener
	 */
	public void setConnectionListener(OnConnectionListener connListener) {
		onConnListener = connListener;
	}

	/**
	 * Sets the dis connection listener.
	 *
	 * @param disconnListener the new dis connection listener
	 */
	public void setDisConnectionListener(OnDisConnectionListener disconnListener) {
		onDisConnListener = disconnListener;
	}

	/**
	 * Sets the comunication listener.
	 *
	 * @param comListener the new comunication listener
	 */
	public void setComunicationListener(OnComunicationListener comListener) {
		onCOMListener = comListener;
	}

	// ///////////////C�digo para Listeners/////////////////

	/**
	 * Wlog.
	 *
	 * @param text the text
	 */
	private void wlog(String text) {
		if(tcon == SERVER)
			System.out.println("Server - "+text);
		else if(tcon == CLIENT)
			System.out.println("Client - "+text);
	}

	/**
	 * Instantiates a new JComunic that only load State Variables.
	 */
	public JComunics() {
		estado = NULL;
	}
	
	/**
	 * Instantiates a new JComunic in Server Mode.
	 *
	 * @param port el puerto que estar� a la espera de conexi�n
	 */
	public JComunics(int port) {
		estado = NULL;
		tcon = SERVER;
		sPort = port;
		onPreExecute();
	}

	/**
	 * Instantiates a new JComunic in Client Mode.
	 *
	 * @param ip La Direcci�n IP a la cu�l conectarse.
	 * @param port El puerto al cu�l vincularse.
	 */
	public JComunics(String ip, int port) {
		estado = NULL;
		tcon = CLIENT;
		isa = new InetSocketAddress(ip, port);
		onPreExecute();
	}

	/**
	 * Enviar El texto Especificado.
	 *
	 * @param dato El texto a ser enviado.
	 */
	public void enviar(String dato) {
		try {
			if (estado == CONNECTED)
				outputSt.writeBytes(dato);
		} catch (IOException e) {
			wlog(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Enviar el N�mero (byte) Especificado.
	 *
	 * @param dato El N�mero a ser enviado.
	 */
	public void enviar(int dato) {
		try {
			if (estado == CONNECTED)
				outputSt.writeByte(dato);
		} catch (IOException e) {
			wlog(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Cortar Conexion Actual.
	 */
	public void Cortar_Conexion() {
		enviar(conKiller);
		try {	
			if (estado == CONNECTED && socket != null) {
				socket.close();
				cancel(true);// socket = null;
			}
		} catch (IOException e) {
			wlog(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Detener la Espera a Conexi�n.
	 */
	public void Detener_Espera() {
		try {
			if (estado == WAITING) {
				// cancel(true);
				if (serverSocket != null)
					serverSocket.close();
				wlog("Espera detenida");
			}
		} catch (IOException e) {
			wlog(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Detener Actividad (Espera/Conexi�n) Actual, seg�n sea el caso.
	 */
	public void Detener_Actividad() {
		Cortar_Conexion();
		Detener_Espera();
//		try {
//			enviar(conKiller);
//			if (estado == CONECTED && socket != null) {
//				socket.close();
//				cancel(true);// socket = null;
//			}
//			if (estado == EN_SPERA) {
//				if (serverSocket != null)
//					serverSocket.close();
//				wlog("Espera detenida");
//			}
//		}catch (IOException e) {
//			wlog(e.getMessage());
//			e.printStackTrace();
//		}
	}

	/**
	 * On pre execute.
	 */
	protected void onPreExecute() {
		estado = NULL;
		socket = null;
		serverSocket = null;
		conectado = false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Integer doInBackground() {
		try {
			if (tcon == CLIENT) {
				socket = new Socket();
				if (socket != null) {
					socket.connect(isa,7000);
				} else
					socket = null;
			} else if (tcon == SERVER) {
				serverSocket = new ServerSocket(sPort);
				if (serverSocket != null) {
					publish(EN_ESPERA);
					socket = serverSocket.accept();
					serverSocket.close();
					serverSocket = null;
				} else
					socket = null;
			}
			if (socket != null && socket.isConnected()) {
				inputSt = new DataInputStream(socket.getInputStream());
				outputSt = new DataOutputStream(socket.getOutputStream());
				conectado = true;
				publish(CONECTADO);
				while (socket.isConnected() && conectado && !isCancelled()) {
					byte[] buffer = new byte[1024];
					int len = inputSt.read(buffer);
					if (len != -1) {
						byte[] blen = (len + "").getBytes();
						publish(DATO_RECIBIDO, blen, buffer);
					}
				}
				conectado = false;
				inputSt.close();
				outputSt.close();
				if (socket != null)
					socket.close();
			}
		} catch (IOException e) {
			wlog("IO Exception");
			publish(IO_EXCEPTION);
			wlog(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

//	public void EnTimeOut(final long ms) {
//
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				try {
//					Thread.sleep(ms);
//					if (estado == CONECTED) {
//						Cortar_Conexion();
//					}
//				} catch (InterruptedException e) {
//
//					e.printStackTrace();
//				}
//			}
//		}).run();
		// new AsyncTask<Void, Void, Integer>() {
		// @Override
		// protected Integer doInBackground(Void... params) {
		// try {
		// Thread.sleep(ms);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// return 1;
		// }
		//
		// @Override
		// protected void onPostExecute(Integer result) {
		// if(estado == CONECTED) {
		// Cortar_Conexion();
		// }
		// super.onPostExecute(result);
		// }
		//
		// }.execute();
//	}

	/* (non-Javadoc)
 * @see javax.swing.SwingWorker#process(java.util.List)
 */
@Override
	protected void process(List<byte[]> values) {
		byte[] orden = values.get(0);
		if (orden == EN_ESPERA) {
			estado = WAITING;
			wlog("En Espera");
		} else if (orden == DATO_RECIBIDO) {
			int len = Integer.parseInt(new String(values.get(1)));
			byte[] buffer = values.get(2);
			String rcv = new String(buffer, 0, len);
			if(rcv.equals(conKiller)) {
				Cortar_Conexion();
			}else {
				if (onCOMListener != null)
					onCOMListener.onDataReceived(rcv);
				wlog("Dato recibido: "+rcv);
			}
		} else if (orden == CONECTADO) {
			estado = CONNECTED;
			if (onConnListener != null)
				onConnListener.onConnectionstablished();
			wlog("Conexion establecida");
		} else if (orden == IO_EXCEPTION) {
//			wlog("IO Exception");
			estado = NULL;
		}
		super.process(values);
	}

	/* (non-Javadoc)
	 * @see javax.swing.SwingWorker#done()
	 */
	@Override
	protected void done() {
		estado = NULL;
		if (onConnListener != null)
			onConnListener.onConnectionfinished();
		if (onDisConnListener != null)
			onDisConnListener.onConnectionfinished();
		wlog("onPostexecute");
		super.done();
	}
//	public static void main(String[] args) {
//		launch(args);
//	}

}
