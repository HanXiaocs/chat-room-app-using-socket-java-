import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.ServerSocket;
import java.util.*;
//Han Xiao U11740340



public class User extends Thread {
    
    // The user socket
    private static Socket userSocket = null;
    // The output stream
    private static PrintStream output_stream = null;
    // The input stream
    private static BufferedReader input_stream = null;
    
    private static BufferedReader inputLine = null;
    private static boolean closed = false;
    
    public static void main(String[] args){
        
        // The default port.
        int portNumber = 58733;
        // The default host.
        String host = "localhost";
        if (args.length < 2) {
            System.out.println("Usage: java User <host> <portNumber>\n"
                             + "Now using host=" + host + ", portNumber=" + portNumber);
        } else {
            host = args[0];
            portNumber = Integer.valueOf(args[1]).intValue();
        }

	/*
         * Open a socket on a given host and port. Open input and output streams.
         */

        //YOUR CODE
        try{
        	userSocket = new Socket(host,portNumber);
        	output_stream = new PrintStream(userSocket.getOutputStream());
        	input_stream = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
        	inputLine = new BufferedReader(new InputStreamReader(System.in));
        	
        }
        catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host "
                                   + host);
        }
        


	/*
         * If everything has been initialized then create a listening thread to 
	 * read from the server. 
	 * Also send any user message to server until user logs out.
     	*/

	// YOUR CODE
        
       
        	if(userSocket!=null && output_stream!=null && input_stream!=null){
        		try{
        	       Thread t = new User();
        	       t.start();
        	while(!closed){
        	       String readinput = inputLine.readLine();
        	       output_stream.println(readinput);
        	}
        	
                   input_stream.close();
                   output_stream.close();
                   userSocket.close();}
                catch (IOException e2) {
                	System.out.println("user1");
                   System.out.println(e2);
          }
        	}
        	

    }
    
 
    public void run() {
        /*
         * Keep on reading from the socket till we receive ### Bye from the
         * server. Once we received that then we want to break and close the connection.
         */
        //YOUR CODE
    	String message;
    	try {while((message = input_stream.readLine())!=null){
    		System.out.println(message);
			while(message.indexOf("###Bye")!=-1){
				break;
			}
			}
    	closed = true;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("user2");
			System.out.println(e1);
		}


    }
}


