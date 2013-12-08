package com.test.androidwschat;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

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
        // Process chat message into socket.io format.
          // Example of socket.io message:
          // {"name":"MessageName",
          // "args":[{"ObjectKey1":"Key1Value","ObjectKey2":"Key2Value"}]}
          // {"name":"msgServer","args":[{"username":"bm","message":"hello"}]}
      String msgS = "{\"name\":\"msgServer\"," +
                   "\"args\":[" + 
                    "{\"username\":\"Android\"," +
                    "\"message\":\"Test Android.\"}]}";
      String msgC = "{\"name\":\"msgClient\"," +
                   "\"args\":[" + 
                    "{\"username\":\"Android\"," +
                    "\"message\":\"Test Android.\"}]}";

        
      // Send via socket.io-java-client by Gottox.

      // This line is cached until the connection is established.
      socket.send( msgS );
      }
    }
  
  class myIOCallback implements IOCallback {
      @Override
      public void onMessage(JSONObject json, IOAcknowledge ack) {
          try {
              System.out.println("Server said:" + json.toString(2));
          } catch (JSONException e) {
              e.printStackTrace();
          }
      }

      @Override
      public void onMessage(String data, IOAcknowledge ack) {
          System.out.println("Server said: " + data);
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
          System.out.println("Server triggered event '" + event + "'");
      }
  }
}
