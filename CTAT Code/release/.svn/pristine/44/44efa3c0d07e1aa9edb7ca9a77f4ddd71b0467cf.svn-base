//Route URL requests to handlers

var fs = require("fs");

function route(handle, pathname, response, data) {

    console.log("About to route a request for " + pathname);
    
    //check if path has a handler function
    if (typeof handle[pathname] === 'function') 
    {
        handle[pathname](response, data);
        return;
    }
    
    //Check if path points to a valid file or directory
    pathname = pathname.slice(1, pathname.length);
    fs.exists(pathname, function(exists) {
        if (exists) {
            if (fs.statSync(pathname).isDirectory())
                pathname += '/index.html';

            fs.readFile(pathname, "binary", function(err, file) {
                if (err) {
                    response.writeHead(500, {"Content-Type": "text/plain"});
                    response.write(err + "\n");
                    response.end();
                    return;
                }
                //TODO: Add MIME information
                //var contentType = contentTypesByExtension[path.extname(filename)];
                //if (contentType)
                //   headers["Content-Type"] = contentType;
                response.writeHead(200);
                response.write(file, "binary");
                response.end();
            });
        }
        else
        {
            response.writeHead(404, {"Content-Type": "text/plain"});
            response.write("404 File or Directory Not Found : \n" + pathname);
            response.end();
            return;
        }
    });
}


exports.route = route;
