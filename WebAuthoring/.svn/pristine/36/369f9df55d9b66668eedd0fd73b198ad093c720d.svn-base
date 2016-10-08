var http = require("http");
var url = require("url");
var router = require("./router.js");
var handlers = require("./handlers.js");
var queryData;
//var router = require("./router.js");

//Bind handler functions
var handle = {};
handle["/echo"] = handlers.echo;


function onRequest(request, response) {
queryData = "";

var pathname = url.parse(request.url).pathname;
console.log("Request for " + pathname + " received.");


//response.writeHead(200, {"Content-Type": "text/plain"});
//POST request processing
        request.on('data', function(data) {
            queryData += data;
            //console.log("Data chunck recieved:" +   data);
        });

        request.on('end', function() {
        //data recieved
        //console.log(queryData);
        router.route(handle, pathname,response,queryData);
        
        //response.write("\n\n---Response End---\n\n")
	//response.end();
        });

    
    }
    
 
   



http.createServer(onRequest).listen(8888);
console.log("Server listening on port 8888");