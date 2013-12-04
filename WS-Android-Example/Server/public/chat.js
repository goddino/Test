// On complete loading, assign functionality to front end elements.
window.onload = function() {
 
  var messages = [];
  // var socket = io.connect('http://localhost:8887');
  var socket = io.connect('ws://localhost:8887');
  var username = document.getElementById("username");
  var field = document.getElementById("msgField");
  var sendButton = document.getElementById("sendButton");
  var content = document.getElementById("content");

  socket.on('msgServer', function (data) {
    if(data.message) {
      messages.push(data);
      // var html = '';
      var html = 'Raw data:' + '<br />' + data + '<br />';
      for(var i=0; i<messages.length; i++) {
          html += '<b>' + (messages[i].username ? messages[i].username : 'Server') + ': </b>';
          html += messages[i].message + '<br />';
      }
      content.innerHTML = html;
    } else {
      console.log("There is a problem:", data);
    }
  });

  sendChat = function() {
    if(username.value == "") {
      alert("Please type your name!");
    } else {
      var text = field.value;
      socket.emit('msgClient', { username: username.value, message: text });
    }
  };

  field.onkeyup = function( event ) {
    if( event.keyCode == 13 ) {
      sendChat();
    }
  }
  
  sendButton.onclick = sendChat;
}