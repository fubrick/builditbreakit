Program for the Buil-it Break-it Fix-it competition


===========================
Summary

Players will implement a secure log to describe the state of an art gallery: the guests and employees who have entered and left, and persons that are in rooms. The log will be used by two programs. One program, logappend, will append new information to this file, and the other, logread, will read from the file and display the state of the art gallery according to a given query over the log. Both programs will use an authentication token, supplied as a command-line argument, to authenticate each other; the security model is described in more detail below.


===========================
Security Model

The system as a whole must guarantee the privacy and integrity of the log in the presence of an adversary that does not know the authentication token. This token is used by both the logappend and logread tools, specified on the command line. Without knowledge of the token an attacker should not be able to:

	Query the logs via logread or otherwise learn facts about the names of guests, employees, room numbers, or times by inspecting the log itself

	Modify the log via logappend.
	
	Fool logread or logappend into accepting a bogus file. In particular, modifications made to the log by means other than correct use of logappend should be detected by (subsequent calls to) logread or logappend when the correct token is supplied

===========================
logappend:

logappend -T <timestamp> -K <token> (-E <employee-name> | -G <guest-name>) (-A | -L) [-R <room-id>] <log>
logappend -B <file>

============================
Appends data to the log at the specified timestamp using the authentication token. If the log does not exist, logappend will create it. Otherwise it will append to the existing log.

If the data to be appended to the log is not consistent with the current state of the log, logappend should print "invalid" and leave the state of the log unchanged.

-T
	timestamp Time the event is recorded. This timestamp is formatted as the number of seconds since the gallery opened and is a non-negative integer. Time should always increase, invoking logappend with an event at a time that is prior to the most recent event already recorded is an error.

-K
	token Token used to authenticate the log. This token consists of an arbitrary-sized string of alphanumeric (a-z, A-Z, and 0-9) characters. Once a log is created with a specific token, any subsequent appends to that log must use the same token.

-E 
	employee-name Name of employee. Names are alphabetic characters (a-z, A-Z) in upper and lower case. Names may not contain spaces. Names are case sensitive. Employees and guests cannot have the same name.

-G 
	guest-name Name of guest. Names are alphabetic characters (a-z, A-Z) in upper and lower case. Names may not contain spaces. Names are case sensitive. Employees and guests cannot have the same name.

-A 
	Specify that the current event is an arrival; can be used with -E, -G, and -R. This option can be used to signify the arrival of an employee or guest to the gallery, or, to a specific room with -R. If -R is not provided, -A indicates an arrival to the gallery as a whole. No employee or guest should enter a room without first entering the gallery. No employee or guest should enter a room without having left a previous room. Violation of either of these conditions implies inconsistency with the current log state and should result in logappend exiting with an error condition.

-L 
	Specify that the current event is a departure, can be used with -E, -G, and -R.This option can be used to signify the departure of an employee or guest from the gallery, or, from a specific room with -R. If -R is not provided, -L indicates a deparature from the gallery as a whole. No employee or guest should leave the gallery without first leaving the last room they entered. No employee or guest should leave a room without entering it. Violation of either of these conditions implies inconsistency with the current log state and should result in logappend exiting with an error condition.

-R 
	room-id Specifies the room ID for an event. Room IDs are non-negative integer characters with no spaces. Leading zeros in room IDs should be dropped, such that 003, 03, and 3 are all equivalent room IDs. A gallery is composed of multiple rooms. A complete list of the rooms of the gallery is not available and rooms will only be described when an employee or guest enters or leaves one. A room cannot be left by an employee or guest unless that employee or guest has previously entered that room. An employee or guest may only occupy one room at a time. If a room ID is not specified, the event is for the entire art gallery.

log
	The path to the file containing the event log. The log's filename may be specified with a string of alphanumeric characters (including underscores). If the log does not exist, logappend should create it. logappend should add data to the log, preserving the history of the log such that queries from logread can be answered. If the log file cannot be created due to an invalid path, or any other error, logappend should print "invalid" and return -1.

-B 
	file Specifies a batch file of commands. file contains one or more command lines, not including the logappend command itself (just its options), separated by \n (newlines). These commands should be processed by logappend individually, in order. This allows logappend to add data to the file without forking or re-invoking. Of course, option -B cannot itself appear in one of these command lines. Commands specified in a batch file include the log name. If a single line in a batch file is invalid, print the appropriate error message for that line and continue processing the rest of the batch file. Here is an example (the last one).

After logappend exits, the log specified by log argument should be updated. The added information should be accessible to the logread tool when the token provided to both programs is the same, and not available (e.g., by inspecting the file directly) otherwise.

===================================
Return values and error conditions:

If logappend must exit due to an error condition, or if the argument combination is incomplete or contradictory, logappend should print "invalid" to stdout and exit, returning a -1.

If the supplied token does not match an existing log, "security error" should be printed to stderr and -1 returned.

If -B is passed, logappend should always return 0.

Some examples of conditions that would result in printing "invalid" and doing nothing to the log:

	The specified datetime on the command line is smaller than the most recent datetime in the existing log

	-B is used in a batch file
	
	The name for an employee or guest, or the room ID, does not correspond to the character constraints

	Conflicting command line arguments are given, for example both -E and -G or -A and -L

=====================================
=====================================	
logread

logread queries the state of the gallery. It prints which employees and guests are in the gallery or its rooms, and allows for various time-based queries of the state of the gallery. The following invocations must be supported:

logread -K <token> [-H] -S <log>
logread -K <token> [-H] -R (-E <name> | -G <name>) <log>

The following invocations are optional (for extra points):

	logread -K <token> -T (-E <name> | -G <name>) <log>
	logread -K <token> [-H] -I (-E <name> | -G <name>) [(-E <name> | -G <name>) ...] <log>
	logread -K <token> [-H] -A -L <lower> -U <upper> <log>
	logread -K <token> [-H] -B -L <lower1> -U <upper1> -L <lower2> -U <upper2> <log>
	
Here is an example invocation

	./logread -K secret -B -L 2 -U 5 -L 7 -U 9 path/to/log
	James,Matt

As per the above invocations, only one of -S, -R, -T, -I, -A, or -B may be specified at once. Normal output from logread is whitespace sensitive, HTML output will be run through html-tidy before being scored (i.e., superfluous spacing between HTML tags is not important).

In what follows, we refer to employees or visitors who are 'in the gallery'. Each person is expected to first enter the gallery (using logappend option -A) prior to entering any particular room of the gallery. Once in the gallery, he or she may enter and leave various rooms (using logappend options -A -R and options -L -R, respectively). Finally, the person will leave the gallery (using logappend option -L). During this whole sequence of events, a person is considered to be 'in the gallery'. See the examples for more information.

When output elements are comma-separated lists, there will be no spaces before or after the commas.

-K
	token Token used to authenticate the log. This token consists of an arbitrary sized string of alphanumeric characters and will be the same between executions of logappend and logread. If the log cannot be authenticated with the token (i.e., it is not the same token that was used to create the file), then "security error" should be printed to stderr and -1 should be returned.

-H 
	Specifies output to be in HTML (as opposed to plain text). Details in options below.

-S 
	Print the current state of the log to stdout. The state should be printed to stdout on at least two lines, with lines separated by the \n (newline) character. The first line should be a comma-separated list of employees currently in the gallery. The second line should be a comma-separated list of guests currently in the gallery. The remaining lines should provide room-by-room information indicating which guest or employee is in which room. Each line should begin with a room ID, printed as a decimal integer, followed by a colon, followed by a space, followed by a comma-separated list of guests and employees. Room IDs should be printed in ascending integer order, all guest/employee names should be printed in ascending lexicographic string order. If -H is specified, the output should instead be formatted as HTML conforming to the following HTML specification.

-R 
	Give a list of all rooms entered by an employee or guest. Output the list of rooms in chronological order. If this argument is specified, either -E or -G must be specified. The list is printed to stdout in one comma-separated list of room identifiers. If -H is specified, the format should instead by in HTML conforming to the following HTML specification.

-T 
	Gives the total time spent in the gallery by an employee or guest. If the employee or guest is still in the gallery, print the time spent so far. Output in an integer on a single line. Specifying -T and -H is not valid. This feature is optional. If the specified employee or guest does not appear in the gallery, then nothing is printed.

-I 
	Prints the rooms, as a comma-separated list of room IDs, that were occupied by all the specified employees and guests at the same time over the complete history of the gallery. Room IDs should be printed in ascending numerical order. If -H is specified, the output should instead be HTML conforming to the following HTML specification. This feature is optional. If a specified employee or guest does not appear in the gallery, it is ignored. If no room ever contained all of the specified persons, then nothing is printed.

-A
	Must be followed by a time bound specified by -L and -U. Outputs a list of employees (in a comma-separated list) in the gallery during the specified time boundary, for example -L x -U y should print employees present during the interval [x,y]. Not valid if only one of -L and -U is supplied, or the -U value is greater than the -L value. Names should be printed in ascending lexicographical string order. If -H is specified, the output should instead be HTML conforming to the following HTML specification. This feature is optional.

-B 
	Must be followed by a pair of time bounds specified by -L and -U. Outputs a list of employees (in a comma-separated list) in the gallery during the first time boundary but not the second. Names should be printed in ascending lexicographical string order. If -H is specified, the output should instead be HTML conforming to the following HTML specification. This feature is optional.

-E 
	Employee name. May be specified multiple times when used with -I.

-G 
	Guest name. May be specified multiple times when used with -I.

-L 
	Lower time bound in seconds. May be specified twice when used with -B. Always followed by -U. The range is inclusive on both lower and upper bounds, and requires the -L value to be less than or equal to the -U value.

-U 
	Upper time bound in seconds. May be specified twice when used with -B. As mentioned above, the bound is inclusive.

log The path to the file log used for recording events. The filename may be specified with a string of alphanumeric characters (including underscores).

If logread is given an employee or guest name that does not exist in the log, it should print nothing about that employee (which may result in empty output). If logread is given an employee or guest name that does not exist in the log, and HTML output is specified, logread should output an HTML table with no entries.

If logread cannot validate that an entry in the log was created with an invocation of logappend using a matching token, then logread should return a -1 and print "integrity violation" to stderr.

Return values and error conditions

Some examples of conditions that would result in printing "invalid" or "integrity violation" (and return -1):

	-I or -T used specifying an employee that does not exist in the log, should print nothing and exit with return 0

	If the logfile has been corrupted, the program should exit and print "integrity violation" to stderr and exit with return -1

	Intersection queried via -A or -B does not exist, should print nothing and exit with return -1

