import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.*;

//Han Xiao U11740340

/*
 * A chat server that delivers public and private messages.
 */
public class Server {
    
    // Create a socket for the server 
    private static ServerSocket serverSocket = null;
    // Create a socket for the user 
    private static Socket userSocket = null;
    // Maximum number of users 
    private static int maxUsersCount = 5;
    // An array of threads for users
    private static userThread[] threads = null;

    public static void main(String args[]) {
        
        // The default port number.
        int portNumber = 58733;
        if (args.length < 1) {
            System.out.println("Usage: java Server <portNumber>\n"
                                   + "Now using port number=" + portNumber + "\n");
        } else {
            portNumber = Integer.valueOf(args[0]).intValue();
        }

        //YOUR CODE
        try {
			serverSocket = new ServerSocket(portNumber);
			System.out.println("Using port: " + portNumber);
		} catch (IOException e) {
			System.out.println(e);
		}


        /*
         * Create a user socket for each connection and pass it to a new user
         * thread.
         */
        threads = new userThread[maxUsersCount];
        while (true) {
        	try{
        		userSocket = serverSocket.accept();
        	    int i = 0;
        	    while(i<maxUsersCount){
        		
        		if(threads[i] == null){
        	    threads[i] = new userThread(userSocket, threads);
        	    threads[i].start();
        	    break;}
        		i++;
        	}
        	    if(i == maxUsersCount){
        	    	PrintStream out = new PrintStream(userSocket.getOutputStream());
        	    	out.println("Server is busy");
        	    	out.close();
        	    	userSocket.close();
        	    }
        	    
        	    
   
        	}
        	
        	catch (IOException e) {
        		System.out.println("server e main");
                System.out.println(e);
            }
        	}

        //YOUR CODE


        }
    }


/*
 * Threads
 */
class userThread extends Thread {
    
    private String userName = null;
    private BufferedReader input_stream = null;
    private PrintStream output_stream = null;
    private Socket userSocket = null;
    private final userThread[] threads;
    private int maxUsersCount;

    // only relevant for Part IV: adding friendship
    ArrayList<String> friends = new ArrayList<String>();
    ArrayList<String> friendrequests = new ArrayList<String>();  //keep track of sent friend requests 
    //

    
    public userThread(Socket userSocket, userThread[] threads) {
        this.userSocket = userSocket;
        this.threads = threads;
        maxUsersCount = threads.length;
    }
    
    public void run() {

	/*
	 * Create input and output streams for this client, and start conversation.
	 */
        //YOUR CODE
    	try{output_stream = new PrintStream(userSocket.getOutputStream());
    	input_stream = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
    	while(true){
    		output_stream.println("Enter your name: ");
    	userName = input_stream.readLine();
    	if(userName.indexOf("@")==-1){
    		break;
    	}
    	else
    		output_stream.println("name should not contain @ since it is special");
    }
    	
    	output_stream.println("Welcome user "+ userName+" to our chatroom.");
    	output_stream.println("To leave enter LogOut in a new line.");
    	synchronized(this){
    		for(int i = 0;i<maxUsersCount;i++){
    			if(threads[i]!=null && threads[i] != this){
    				threads[i].output_stream.println("***A new user "+userName+" entered the chat room!***");
    			
    		}
    	}
    }
    	while(true){
    		//user leaves
    		String message = input_stream.readLine();
    		if(message.startsWith("@")){
    			String toname="";
    			for(int index = 1;index<message.length();index++){
    				if(Character.toString(message.charAt(index)).equals(" "))
    					break;
    				else
    					toname += Character.toString(message.charAt(index));//get the user name from @messages
    			}
    			int tonamelen = 1+toname.length();
    			synchronized(this){
    				//unicast
    				for(int i = 0; i<maxUsersCount;i++){
    					if(threads[i]!=null && (threads[i].userName.equals(toname) || threads[i] == this))
    						threads[i].output_stream.println("<"+userName+">"+message.substring(tonamelen));
    				}
    			}
    		}
    		else if(!message.equals("LogOut") && !message.startsWith("@")){
    			synchronized(this){
    				//broadcast
    				for(int i = 0; i<maxUsersCount;i++){
    					if (threads[i] != null && threads[i].userName != null){
    	                threads[i].output_stream.println("<"+userName+">"+message); //send the message to all user? 
    			}
    		}
    	}
    }
    		else if(message.equals("LogOut")){
    			
    			break;
    		}
    		//broadcast
    		/*synchronized(this){
				for(int i = 0; i<maxUsersCount;i++){
					if (threads[i] != null && threads[i].userName != null){
	                threads[i].output_stream.println("<"+userName+">"+message); //send the message to all user? 
			}
		}
	}*/
  }
    		synchronized(this){
        		for(int i = 0;i<maxUsersCount;i++){
        			if(threads[i]!=null && threads[i] != this){
        			threads[i].output_stream.println("***the user "+userName+" is leaving the chatroom!!!***");
        			}
        		}
        		}
    		output_stream.println("###Bye "+ userName +" ###");
    		//when exits, set the thread to null
    		synchronized(this){
    		for (int i = 0; i < maxUsersCount; i++) {
    	        if (threads[i] == this) {
    	          threads[i] = null;
    	        }
    	      }
    		}

    		
    	
    	
    	input_stream.close();
    	output_stream.close();
    	userSocket.close(); 
    	
    	}
    	
    	catch (Exception e) {
    		synchronized(this){
        		for (int i = 0; i < maxUsersCount; i++) {
        	        if (threads[i] == this) {
        	          threads[i] = null;
        	        }
        	      }
        		}
    		System.out.println("shenme");
            System.out.println(e);
        }
    }
}
