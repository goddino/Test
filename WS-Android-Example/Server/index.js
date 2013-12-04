var express = require("express");
var app = express();
var port = 8887;
 
app.set('views', __dirname + '/tpl');
app.set('view engine', "jade");
app.engine('jade', require('jade').__express);
app.get("/", function(req, res){
    res.render("page");
});

// External JS for front end logic:
// app.use(express.static(__dirname + '/public'));
app.use(express.static(__dirname));

// Listen Socket.io will use ExpressJS server, and listen for ws.
var io = require('socket.io').listen(
  app.listen(port),
  { 'destroy upgrade': false }
);
console.log("Listening on port " + port + ".");

// Respond to client connecting.
io.sockets.on( 
  'connection', 
  function( socket ) {
    // Once connected, send greeting to client.
    socket.emit( 'msgServer',
                { message: 'Welcome to WebSocket test for Android' });
    // On receipt of client message of type "msgClient"
    socket.on( 
      'msgClient',
      // Broadcast the message to all clients.
      function( data ) {
        io.sockets.emit( 'msgServer', data );
      }
    );
  }
);