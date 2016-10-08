The Tutoring Service Load Test:
Originally written by Michael Weber July 2007 (or atleast thats all the credits I could find).
Major overhaul of the loadTest was done June/July 2009 by Borg Lojasiewicz,
with the assistance of Jonathan Sewall and Martin Van Velsen.

I have never written one of these so please excuse any mistakes:

The Tutoring Service Load Test is supposed to be mimic (somewhat) classrooms connecting to the Tutoring Service with large loads.

To record a sample session to be later run as part of the load test:

->You first need the .swf file and the corresponding brd of the session you want to record. 
->Make sure that the CTAT_Launcher has the brd file in its directory.

1.)Run the CTATLauncher with debug codes of "tsltsp" and "tsltstp". 
You can either specify the brd file when running CTATLauncher. Or manually open the brd file.

2.)Open up the .swf, wait for it to connect, and perform any steps that you want to be mimicked later on. 
The session *MUST* include a succesfuly done step in order for the load test to work properly.

3.)Copy-paste the output of CTATLauncher into a file and save it in the same directory as test.sh. 
There are smarter ways of doing this if you run CTATLauncher from a shell :)

To run some sample sessions against the LauncherServer:

1.)Run the LauncherServer

2.)Run test.sh with the following arguments in order (see comments in test.sh)
	->a list of all the sample-sessions you want to run against the launcherserver
	->The number of students you want per sample-session
	->The number of times you want to the load test sequentially.
3.)Analyze the output from the shell. Best way to do this is to look at the TSLTcorrectnesschecker output. 
If you see any exceptions or the text "Test Failed:" the loadtest failed for some reason. 

Note: Throughout all the TSLT files there is a lot of trace output with debug code "tslt". 
The HTTPServer to which we connect to has debug code "http". 
Editing the test.sh to run with the debug codes will give you better output for analysis.

Issues with portability and HardCoded URLs (why isn't the loadtest working!?):

This incarnation of the LoadTest works properly on my windows computer using cygwin and Opera web browser.

It probably will not work on your computer.

I tried to make the code as portable as possible so all the changes you will have to make will probably be to test.sh:
1.) You will almost for certain have to change the "D" variable that is specified close to the top of test.sh.
->Simply set D to $(pwd) or hardcode it in. It is hardcoded currently because cygwin has a different directory structure.
2.)In the loop that launches the Opera browser, you will probably want to substitute your own browser. 

Other Possible Issues:

1.) The test.sh has a lot of calls to sleep: most of them are just there to make sure things run smoothly.
->The sleep that happens right before the browser is killed may be to short, and cause sessions to die prematurely.
------>If that seems to be the case, simply make it sleep for a lot longer and see if everthing works then.
2.)TSLTTemplate.html should not be edited. The TSLTPreProcessor assumes it contains strings strings 
such as "myLogFile" and replaces them with the actual logfile. Editing TSLTTemplate.html might cause problems.
3.)The preprocessor assumes that the brd file is in the log and can be somewhere in the console_output:
It searches for "<question_file>" and it searches for "ProblemName=\"". This assumption might not always be true
in the future, especially if you are running the launcherserver with only the tsltsp/tsltstp debug codes and the code
is cleaned up, with system.out.printlns removed.
Quick FIX?
Simply edit the consoleOutput file to contain this line:
 "<question_file>someshit.brd</question_file>"
or
ProblemName="someshit.brd"
