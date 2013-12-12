var port = 8887;
var express = require('express')
var app = express();
app.use( express.static( __dirname ) );
// var server = require('http').Server().listen( port );
var server = require('http').createServer(app).listen( port );

// Set up jade as template engine.
app.set('views', __dirname + '/tpl');
app.set('view engine', "jade");
app.engine('jade', require('jade').__express);

// Load web client when using browser to connect to server.
app.get("/", function(req, res){
    res.render("page");
});

var io = require('socket.io').listen(
  server,
  { 'destroy upgrade': false }
);

console.log("Listening on port " + port + ".");

io.sockets.on('connection', function(socket) {
  var msgCon = "Connected to server for Android WebSocket Test!";
  socket.emit('msgServer', { username: "Server", message: msgCon });
  
  // Broadcast messages from event "chat".
  socket.on('chat', function(data) {
    var messages = [];
    if( data.message ) {
      messages.push( data );
    }
    for( var i=0; i < messages.length; i++ ) {
      var curUser = "Unidentified";
      if( messages[i].username ) curUser = messages[i].username;
      // Emit to sender:
      // socket.emit('chat', 
      // Emit to all clients:
      io.sockets.emit('chat', 
        { username: curUser, message: messages[i].message });
    }
  });
  
  // Echo events of "msgClient" with "msgServer".
  socket.on('msgClient', function(data) {
    var messages = [];
    if( data.message ) {
      messages.push( data );
    }
    for( var i=0; i < messages.length; i++ ) {
      var curUser = "Unidentified";
      if( messages[i].username ) curUser = messages[i].username;
      var msg = "Server received from " + curUser + ":\n" + messages[i].message;
      socket.emit('msgServer', { username: "Server", message: msg });
    }
  });
});
  