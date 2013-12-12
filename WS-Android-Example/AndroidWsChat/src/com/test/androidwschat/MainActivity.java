package com.test.androidwschat;

import java.net.MalformedURLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import com.test.androidwschat.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

  // A public member of this class,
    // for other classes to have a handle on MainActivity instance.
  public static MainActivity mainActivity;
  public UIActivity uiActivity;
  
  // String to hold chat contents
  public String chatContents = "";
  
  public SocketIO socket = null;
  
  private static String TAG;
  
 
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // setContentView(R.layout.activity_main);
    TAG = "MainActivity";
    
    try {
      socket = new SocketIO("http://49.128.39.190:8887/");
    } catch (MalformedURLException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    
    socket.connect( new myIOCallback() );
    
    // Set mainActivity
    mainActivity = this;

    // Start UI
    Intent intentUIActivity = 
        new Intent( getApplicationContext(), UIActivity.class );
        
    startActivity( intentUIActivity );
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }
  
  // Method to write contents of txtVwChatMsgs from none UI thread
  public void addChatMsg( final String newChat ) {
    // Modification to UI must run on the UI thread.
    MainActivity.this.runOnUiThread( new Runnable() {
      public void run() {
        chatContents += ( "\n" + newChat );
        if( uiActivity != null )
          uiActivity.txtVwChatMsgs.setText( chatContents );
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
