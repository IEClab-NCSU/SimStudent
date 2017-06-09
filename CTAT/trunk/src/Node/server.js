var http = require("http");
var url = require("url");
var router = require("./router.js");
var handlers = require("./handlers.js");
var queryData;




//Bind handler functions
var handle = {};
handle["/echo"] = handlers.echo;
handle["/parsexml"] = handlers.parsexml;
handle["/test"] = handlers.test;
handle["/pushsiaxml"] = handlers.pushSIAXML;
handle["/sendToTutor"] = handlers.handleSendToTutor;

function onRequest(request, response) {
queryData = "";

var pathname = url.parse(request.url).pathname;
console.log("Request for " + pathname + " received.");


        request.on('data', function(data) {
            queryData += data;
            //console.log("Data chunck recieved:" +   data);
        });

        request.on('end', function() {
        router.route(handle, pathname,response,queryData);
        });

    }

http.createServer(onRequest).listen(8888);
console.log("Server listening on port 8888");
