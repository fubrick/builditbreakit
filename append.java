import java.util.*;
import java.io.*;

public class append
{
	public static Scanner in = new Scanner(System.in);
	public static void main(String[] args) {
		
		String [] logArray = args;

		if (logArray[0].equals("-B")) {
			BatchReader reader = new BatchReader();
			LogAppender logTester = new LogAppender();

			if (reader.openFile(logArray[1]) == true) {
				reader.readFile(logTester);
				reader.closeFile();
			}
		}
		else
			System.out.println("poop"); //read single logs

	}
}

class BatchReader
{
	private Scanner x;

	public boolean openFile(String dataFile)
	{
		try{
			x = new Scanner(new File(dataFile));
		}
		catch(Exception e){
			System.out.println("could not read file.");
			return false;
		}
		return true;
	}

	public void readFile(LogAppender logger)
	{
		String firstline = x.nextLine();
		//System.out.println(firstline);
		logger.validateLog(firstline);

		//open log based on log name and pw of first line
		//logger.openLog(firstline);
		//if openLog was successful then go to while else error

		while(x.hasNextLine())
		{
			String logLine = x.nextLine();
			logger.validateLog(logLine);
			//read in individual log lines
		}
	}

	public void closeFile()
	{	x.close();	}
}

class DataLoader
{
	private Scanner x;

	public boolean openFile(String fileName)
	{
		try{
			x = new Scanner(new File(fileName));
		} catch(Exception e) {
			System.out.println("Could not find log file");
			return false;
		}
		return true;
	}

	public void loadData(LogFile log)
	{
		// read in file and add data to log
	}

	public void closeFile()
	{	x.close();	}
}

class DataSaver
{
	Scanner sc = new Scanner(System.in);
	private Formatter x;

	public void openFile(String fileName)
	{
		try {
			x = new Formatter(fileName);
		} catch(Exception e) {
			System.out.println("Could not open log");
		}
	}

	public void addRecordsToLog(LogFile log)
	{
		// record data to log
	}

	public void closeFile()
	{	x.close();	}
}

class LogAppender
{
	private String currentPassword = null;
	private String encryptionKey = "blablad93j2wp0s1";
	//private String currentLog = null;
	private LogFile currentLog = null;

	DataLoader reader = new DataLoader();
	DataSaver saver = new DataSaver();


	//make a hashtable of persons with name+log as the key
	//private ArrayList<Person> currentPersons = new ArrayList<>();


	public void validateLog(String log)
	{
		String password, name, logName;
		int time, room;
		boolean arriving, guest, inLobby;

		System.out.println(log);
		StringTokenizer check = new StringTokenizer(log);
		
		// check for valid length
		int logLength = check.countTokens();
		if (logLength == 8)
			inLobby = true;
		else if (logLength == 10)
			inLobby = false;
		else
		{
			System.out.println("Invalid length of log entry");
			return;
		}
		
		// check for -K
		if (check.nextToken().equals("-K") == false) {
			System.out.println("no -K command");
			return;
		}

		// check password
		password = check.nextToken();
		if (password.matches("\\A\\p{Alnum}*\\z") == false) {
			System.out.println("incorrect password (must be alphanumeric)");
			return;
		}

		// check  -T
		if (check.nextToken().equals("-T") == false) {
			System.out.println("no -T command");
			return;
		}

		// check time
		String timeEntry = check.nextToken();
		if (timeEntry.matches("\\A\\p{Digit}*\\z") == false) {
			System.out.println("invalid time entry");
			return;
		}
		else
			time = Integer.parseInt(timeEntry);

		// check -A or -L
		String movement = check.nextToken();
		if (movement.equals("-A"))
			arriving = true;
		else if (movement.equals("-L"))
			arriving = false;
		else
			System.out.println("invalid movement");

		// check if entry has 5 or 3 more elements
		if (check.countTokens() == 5) {

			// check -R
			if (check.nextToken().equals("-R") == false) {
				System.out.println("invalid room command");
				return;
			}

			// check room
			String roomEntry = check.nextToken();
			if (roomEntry.matches("\\A\\p{Digit}*\\z") == false) {
				System.out.println("invalid room number");
				return;
			}
			else
				room = Integer.parseInt(roomEntry);
		}

		// check -G or -E
		String personType = check.nextToken();
		if (personType.equals("-G"))
			guest = true;
		else if (personType.equals("-E"))
			guest = false;
		else
			System.out.println("invalid personType command");

		// check name
		name = check.nextToken();
		if (name.matches("\\A\\p{Alpha}*\\z") == false) {
			System.out.println("Invalid name (must be alphabetic");
			return;
		}

		logName = check.nextToken();
		if (logName.matches("\\A\\p{Alnum}*\\z") == false) {
			System.out.println("invalid log name (must be alphanumeric)");
			return;
		}
		/*
		Valid states:	currentlog exists, inlobby = true
						currentlog exists, inloggy = false
						currentlog = null, inlobby = true
		*/	
		if (currentLog == null){
			System.out.println("   !!must create new log: " + logName);
			saver.openFile(logName);
			saver.closeFile();
			currentLog = new LogFile(logName);
		}
		else if (currentLog.getLogName().equals(logName)){
			System.out.println("  we have current log");
		} else if (currentLog.getLogName().equals(logName) == false) {
			
			//sdssave and encrypt current log

			System.out.println("   !!must create new log: " + logName);
			saver.openFile(logName);
			saver.closeFile();
			currentLog = new LogFile(logName);
		} else {
			System.out.println("  could not process log entry. End of validateLog method");
		}



		//check pw against current log

		//process data into log
	}
	
}

class LogFile
{
	private String logName;
	private int currentTime;
	private String password;
	private String key;

	Hashtable<String, Person> log = new Hashtable<>();

	public LogFile(String logName)
	{
		this.logName = logName;
	}

	public void addPerson(Person newP)
	{
		log.put(newP.getName(), newP);
	}

	public String getLogName()
	{	return logName;	}

	public Person getPerson(String name)
	{
		return log.get(name);
	}

	public boolean varifyPassword(String testPassword)
	{
		return password.equals(testPassword);
	}


}

class Person
{
	public Person(String name, String type, int location, 
		String movement, int time, String listOfRooms)
	{
		this.time = time;			//-T 1
		this.movement = movement;	//-A
		this.type = type;			//-E
		this.name = name; 			//Joe
		this.location = location;	//-R 1
		this.listOfRooms = listOfRooms;
	}

	public String getName()
	{	return name;	}

	public boolean hasLeftLastRoom()
	{
		if (movement.equals("-L"))
			return true;
		return false;
	}

	public int checkTime()
	{	return time;	}

	public void updateMovement(int location, String movement, int time)
	{
		this.location = location;
		this.movement = movement;
		this.time = time;
		listOfRooms.concat(", " + location);
	}


	private int time;
	private String movement;
	private String type;
	private String name;
	private int location;
	private String listOfRooms;
}






