var xml = require("./libXML.js");

//Request handlers go here


//Echo data back as response
function eecho(response,data)
{
    response.write(data);
    response.write("--Response End--");
    response.end();
    console.log("Data echoed.");
}
    
function echo(response,data)
{
    
    xml.parseXML(data, handleXML);
    
}


exports.echo = echo;


function handleXML(xmldoc)
{
    console.log(xmldoc);
}