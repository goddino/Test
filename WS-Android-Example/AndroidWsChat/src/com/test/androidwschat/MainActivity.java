package com.test.androidwschat;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
//import io.socket.WebsocketTransport;

import com.test.androidwschat.R;
import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

// public class MainActivity extends Activity {
public class MainActivity extends Activity {

  // Instantiate views
  private TextView txtVwChatMsgs;
  private EditText edtVwUserMsg;
  private EditText edtVwUsername;
  private Button btnSendChat;
  
  private SocketIO socket = null;
  
  private static String TAG;
  
 
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    TAG = "MainActivity";
    
    // Set Views
    txtVwChatMsgs = ( TextView ) findViewById( R.id.textViewChatMessages );
    txtVwChatMsgs.setMovementMethod( new ScrollingMovementMethod() );
    edtVwUsername = ( EditText ) findViewById( R.id.editTextUsername );
    edtVwUserMsg = ( EditText ) findViewById( R.id.editTextUserMessage );
    btnSendChat = ( Button ) findViewById( R.id.buttonSendChat );

    
    try {
		SocketIO.setDefaultSSLSocketFactory(SSLContext.getDefault());
	} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

    try {
		socket = new SocketIO("http://49.128.39.190:8887/");
	} catch (MalformedURLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
    socket.connect( new myIOCallback() );
    
    // Set on ClickListener
    btnSendChat.setOnClickListener( new MyOnClickListener() );
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  class MyOnClickListener implements Button.OnClickListener {
      public void onClick( View v ) {
        String msgS = "{\"name\":\"msgServer\"," +
                     "\"args\":[" + 
                      "{\"username\":\"Android\"," +
                      "\"message\":\"Test Android.\"}]}";
        String msgC = "{\"name\":\"msgClient\"," +
                     "\"args\":[" + 
                      "{\"username\":\"Android\"," +
                      "\"message\":\"Test Android.\"}]}";

        String eventC = "{\"username\":\"Android\"," +
                      "\"message\":\"Test from Android.\"}";
        
        // Convert input into event JSON string in socket.io format.
          // Example of socket.io message:
          /*           
          {"name":"MessageName",
            "args":[{"ObjectKey1":"Key1Value","ObjectKey2":"Key2Value"}]}
          {"name":"msgServer","args":[{"username":"bm","message":"hello"}]}
          */        
        String chat = edtVwUserMsg.getText().toString();
        edtVwUserMsg.setText( "" );
        
        String username = edtVwUsername.getText().toString();
        if( username.equals( "" ) ) username = "Android";
        
        JSONObject eventMsg = new JSONObject();
        try {
          eventMsg.put( "username", username );
          eventMsg.put( "message", chat );
        } catch (JSONException e1) {
          e1.printStackTrace();
        }
        
        // socket.emit( "msgClient", eventMsg );
        
        // For chat, send a "chat" event.
        socket.emit( "chat", eventMsg );
      }
    }
  
  // Method to write contents of txtVwChatMsgs from none UI thread
  public void addChatMsg( final String newChat ) {
    // Modification to UI must run on the UI thread.
    MainActivity.this.runOnUiThread( new Runnable() {
      public void run() {
        // String prevChat = (String) txtVwChatMsgs.getText().toString();
        txtVwChatMsgs.append( "\n" + newChat );
      }
    });
  }
  
  class myIOCallback implements IOCallback {
      @Override
      public void onMessage(JSONObject json, IOAcknowledge ack) {
          try {
              System.out.println("Server said:" + json.toString(2));
              Log.d(TAG, "Server Json: " + json.toString(2) + "\n" );
              addChatMsg( json.toString(2) );
          } catch (JSONException e) {
              e.printStackTrace();
          }
      }

      @Override
      public void onMessage(String data, IOAcknowledge ack) {
          System.out.println("Server said: " + data);
          Log.d(TAG, "Server String: " + data + "\n" );
          addChatMsg( data );
      }

      @Override
      public void onError(SocketIOException socketIOException) {
          System.out.println("an Error occured");
          socketIOException.printStackTrace();
      }

      @Override
      public void onDisconnect() {
          System.out.println("Connection terminated.");
      }

      @Override
      public void onConnect() {
          System.out.println("Connection established");
      }

      @Override
      public void on(String event, IOAcknowledge ack, Object... args) {
        String result = "";
        System.out.println("Server triggered event '" + event + "'");
        Log.d(TAG, "Server Event: " + event + "\n" );
/*         
        Process event messages in socket.io format.
          Example of socket.io event message:
          {"name":"MessageName",
            "args":[{"ObjectKey1":"Key1Value","ObjectKey2":"Key2Value"}]}
          {"name":"msgServer","args":[{"username":"bm","message":"hello"}]}
 */        
        // For chat messages:
        if( event.equals( "chat" ) ) {
          JSONObject jsonChat = (JSONObject) args[0];
          String chatUser = "";
          String chatMsg = "";
          try {
            chatUser = jsonChat.get( "username" ).toString();
            chatMsg = jsonChat.get( "message" ).toString();
          } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          
          String chat = chatUser + ": " + chatMsg;
          
          addChatMsg( chat );
          Log.d( TAG, chat );
        } else {
          // For general messages:
          for( int i = 0; i < args.length; ++i ) {
            JSONObject json = (JSONObject) args[i];
            JSONArray names = json.names();
            for( int j = 0; j < names.length(); ++ j ) {
              String value = "";
              String name = "";
              try {
                name = names.get(j).toString();
                value = json.get( name ).toString();
              } catch (JSONException e) {
                e.printStackTrace();
              }
              result += name + " : " + value + "\n";
            }
          }
          Log.d( TAG, result );
          addChatMsg( result );
        }
      }
  }
}
