package networkdcq.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


import networkdcq.NetworkApplicationData;
import networkdcq.util.Logger;


public class TCPNetwork extends TCPCommunication {

	/** TCP Port */
	public static final int TCP_PORT = 9999;
	
    /** Configured Host IP */
    protected String host;
    /** Configured Host port */
    protected int port;     

    /** Client socket */
    protected Socket socket;
    /** Server socket */
    protected ServerSocket serverConn;
    
    /** Buffer for writing objects */
    protected ObjectOutputStream toBuffer = null;
    /** Buffer for reading objects */
    protected ObjectInputStream fromBuffer = null;

	/**
     * Writes an object to the stream
     * @param data object to be written
     * @throws IOException in case of socket error 
     */
    public void write(NetworkApplicationData data) throws IOException {
        try {
            toBuffer.writeObject(data);
            toBuffer.flush();
            toBuffer.reset();
        }
        catch (Exception ex) { 
        	Logger.w("Exception writing object:" + ex.getMessage());
       		throw new IOException("Socket error");
        }
    }   
    

    /**
     * Reads the next object from the stream
     * @return received data
     * @throws IOException in case of socket error
     */
    public NetworkApplicationData receive() throws IOException {
        try {
        	return (NetworkApplicationData)fromBuffer.readObject();
        }   
        catch (Exception ex) {
        	Logger.w("Exception reading object:" + ex.getMessage());
       		throw new IOException("Socket error");
        }
    }  
    

    /**
     * Closes the server socket 
     */
    public void closeServer() {
        try {       
            socket.close();
            serverConn.close();
        }
        catch (Exception ex) {
            Logger.w(ex.getMessage()); 
        }      
    }
    


    
}
