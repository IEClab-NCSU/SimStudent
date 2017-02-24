This directory should reflect the sources in the CTAT HTML5 SVN repository
at:

	<HTML5REP> svn://pact-cvs.pact.cs.cmu.edu/usr5/local/svnroot/AuthoringTools/trunk/HTML5

We consider that svn path the source path and the location in Noboru's
repository the target path, which should be:

	<NBREP> trunk\Communicator\SimStudentServlet\WebContent

For reference purposes we will refer to the source location as <HTML5REP>
and the target repository <NBREP>. 

Note that not all of the subdirectories should be copied/merged. The basic
set of subdirectories that should definitely a part of any merge effort
are:

	- css
	- jquery
	- lib
	- ctatjslib (if there are files here)
	- src
	- skindata
	- third-party

From within the root directory also make sure you copy:

	- build.xml
	- Gruntfile.js

The root directory of:

	trunk\Communicator\SimStudentServlet\WebContent

contains an index.html file, which is the starting point for loading and
running a tutor. The actual tutor html that runs the tutor (html5 version)
is

	tutor.html

Please make sure that the correct files from <HTML5REP> are used when 
modifying or building different versions of the tutor html drivers.
Probably the best version of the tutor driver html to use is the
one that loads our javascript sources as what we call a bundle.
A bundle is a compiled version of all of the relevant sources. Do 
generate a bundle use ant.

First make sure you clean using:

	ant clean

Then build the appropriate targets with

	ant dep
	ant compileTutorJs

This will generate a single js file that contains all the javascript 
sources compressed together into:

	ctatjslib/ctat-tutor.min.js

If you were to open the file html5_tutor.html in <HTML5REP) then you will see
that we load this script file. 
