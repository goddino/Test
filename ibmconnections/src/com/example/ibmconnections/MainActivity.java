package com.example.ibmconnections;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.webkit.WebView;

public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    try {
      Uri data = getIntent().getData();
      if (data.equals(null)) { 
      } else { 
          String scheme = data.getScheme();
          String host = data.getHost();
          int port = data.getPort(); 
          //type what u want
          String url = "https" + data.toString().substring(6);
          /*
          WebView chrome = new WebView(this);
              // (WebView) findViewById(R.id.wv_bikeWebsite);
          chrome.loadUrl(url);
          */
          
          Uri uri = Uri.parse(url);
          Intent intent = new Intent(Intent.ACTION_VIEW, uri);
          startActivity(intent);
          
       }      
  } catch (NullPointerException e) {
        // TODO: handle exception
    
  }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

}
