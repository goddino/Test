package com.test.androidwschat;

import com.test.androidwschat.R;

import android.os.Bundle;
import org.java_websocket.client.*;

import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

// For Java-WebSocket
import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;


public class MainActivity extends Activity {

  // Instantiate views
  private TextView txtVwChatMsgs;
  private EditText edtVwUserMsg;
  private Button btnSendChat;
  
  // Instantiate Java-WebSocket WebSocketClient;
  private WsClient wsClient;
    
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    // Initialise wsClient
    try {
      // wsClient = new WsClient( new URI( "ws://localhost:8887" ), new Draft_17() );
      wsClient = new WsClient( new URI( "ws://49.128.39.190:8887" ), new Draft_17() );
    } catch (URISyntaxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    wsClient.connect();
    
    // Set Views
    txtVwChatMsgs = ( TextView ) findViewById( R.id.textViewChatMessages );
    edtVwUserMsg = ( EditText ) findViewById( R.id.editTextUserMessage );
    btnSendChat = ( Button ) findViewById( R.id.buttonSendChat );

    // Set on ClickListener
    btnSendChat.setOnClickListener( new Button.OnClickListener() {
      public void onClick( View v ) {
        // Process chat message into socket.io format.
          // Example of socket.io message:
          // {"name":"MessageName",
          // "args":[{"ObjectKey1":"Key1Value","ObjectKey2":"Key2Value"}]}
      String msg = "{\"name\":\"msgServer\"," +
                   "\"args\":[" + 
                    "{\"username\":\"Android\"," +
                    "\"message\":\"Test Android.\"}]}";

        
        // Send via Java-WebSocket client.
        wsClient.send( msg );
      }
    });
    

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  public class WsClient extends WebSocketClient {

    public WsClient( URI serverUri , Draft draft ) {
      super( serverUri, draft );
    }

    public WsClient( URI serverURI ) {
      super( serverURI );
    }

    @Override
    public void onOpen( ServerHandshake handshakedata ) {
      System.out.println( "opened connection" );
      // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
    }

    @Override
    public void onMessage( String message ) {
      System.out.println( "received: " + message );
    }

    @Override
    public void onFragment( Framedata fragment ) {
      System.out.println( "received fragment: " + new String( fragment.getPayloadData().array() ) );
    }

    @Override
    public void onClose( int code, String reason, boolean remote ) {
      // The codecodes are documented in class org.java_websocket.framing.CloseFrame
      System.out.println( "Connection closed by " + ( remote ? "remote peer" : "us" ) );
    }

    @Override
    public void onError( Exception ex ) {
      ex.printStackTrace();
      // if the error is fatal then onClose will be called additionally
    }    
  }  
}
