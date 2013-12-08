package com.test.androidwschat;
import com.test.androidwschat.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;


// public class MainActivity extends Activity {
public class MainActivity extends Activity {

  // Instantiate views
  private TextView txtVwChatMsgs;
  private EditText edtVwUserMsg;
  private Button btnSendChat;
  
  private static String TAG;
  
  // Instantiate Autobahn WebSocket Connection;
  private WebSocketConnection mConnection;
 
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    TAG = "MainActivity";
    mConnection = new WebSocketConnection();
    start();
    
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
          // {"name":"msgServer","args":[{"username":"bm","message":"hello"}]}
      String msgS = "{\"name\":\"msgServer\"," +
                   "\"args\":[" + 
                    "{\"username\":\"Android\"," +
                    "\"message\":\"Test Android.\"}]}";
      String msgC = "{\"name\":\"msgClient\"," +
                   "\"args\":[" + 
                    "{\"username\":\"Android\"," +
                    "\"message\":\"Test Android.\"}]}";

        
        // Send via Autobahn connection.
        mConnection.sendTextMessage( msgS );
        mConnection.sendTextMessage( msgC );
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  private void start() {
 
    final String wsuri = "ws://49.128.39.190:8887";

    try {
      mConnection.connect(wsuri, new WebSocketHandler() {

        @Override
        public void onOpen() {
           Log.d(TAG, "Status: Connected to " + wsuri);
           mConnection.sendTextMessage("Hello, world!");
        }

        @Override
        public void onTextMessage(String payload) {
           Log.d(TAG, "Got echo: " + payload);
           String existingChat = (String) txtVwChatMsgs.getText();
           txtVwChatMsgs.setText( existingChat + "\n" + payload );
        }

        @Override
        public void onClose(int code, String reason) {
           Log.d(TAG, "Connection lost.");
        }
      });
    } catch (WebSocketException e) {

      Log.d(TAG, e.toString());
    }
  }
}
