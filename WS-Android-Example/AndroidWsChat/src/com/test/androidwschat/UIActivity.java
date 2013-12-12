package com.test.androidwschat;

import org.json.JSONException;
import org.json.JSONObject;

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

public class UIActivity extends Activity {

  private MainActivity mainActivity = MainActivity.mainActivity;
  // Instantiate views
  public TextView txtVwChatMsgs;
  private EditText edtVwUserMsg;
  private EditText edtVwUsername;
  private Button btnSendChat;
  
  private static String TAG;
  
 
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ui);
    TAG = "UIActivity";
    
    // Set uiActivity of mainActivity
    mainActivity.uiActivity = UIActivity.this;
    
    // Set Views
    txtVwChatMsgs = ( TextView ) findViewById( R.id.textViewChatMessages );
    txtVwChatMsgs.setMovementMethod( new ScrollingMovementMethod() );
    txtVwChatMsgs.setText( mainActivity.chatContents );
    edtVwUsername = ( EditText ) findViewById( R.id.editTextUsername );
    edtVwUserMsg = ( EditText ) findViewById( R.id.editTextUserMessage );
    btnSendChat = ( Button ) findViewById( R.id.buttonSendChat );

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
        
        // For chat, send a "chat" event.
        mainActivity.socket.emit( "chat", eventMsg );
      }
    }
  
  // Method to write contents of txtVwChatMsgs from none UI thread
  public void addChatMsg( final String newChat ) {
    // Modification to UI must run on the UI thread.
    mainActivity.addChatMsg( newChat );
  }

}
