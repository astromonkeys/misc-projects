
//////////////////// ALL ASSIGNMENTS INCLUDE THIS SECTION /////////////////////
//
// Title: MasterMind
// Files: MasterMind.java, TestMasterMind.java, Config.java
// Semester: spring 2019
//
// Author: Noah Zurn
// Email: nzurn@wisc.edu
// CS Login: zurn
// Lecturer's Name: renault
// Lab Section: 341
//
///////////////////////////// CREDIT OUTSIDE HELP /////////////////////////////
//
// Students who get help from sources other than their partner must fully
// acknowledge and credit those sources of help here. Instructors and TAs do
// not need to be credited here, but tutors, friends, relatives, roommates
// strangers, etc do.
//
// Persons: none
// Online Sources: http://pages.cs.wisc.edu/~cs200/programs/bp2
//
/////////////////////////////// 80 COLUMNS WIDE ///////////////////////////////

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Random;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;

public class AdventureStory {

    // used in parseStory method to keep track of what state is being parsed
    enum storyState {
        BEGINNING, COMMENT, ROOMTITLE, ROOMID, ROOMDESC, TRANSITION, TERMINALTRANSITION, NORMALTRANSITION, WEIGHTEDTRANSITION
    };

    /**
     * Prompts the user for a value by displaying prompt. Note: This method should
     * not add a new line to the output of prompt.
     *
     * After prompting the user, the method will consume an entire line of input
     * while reading an int. If the value read is between min and max (inclusive),
     * that value is returned. Otherwise, "Invalid value." terminated by a new line
     * is output and the user is prompted again.
     *
     * @param sc     The Scanner instance to read from System.in.
     * @param prompt The name of the value for which the user is prompted.
     * @param min    The minimum acceptable int value (inclusive).
     * @param max    The maximum acceptable int value (inclusive).
     * @return Returns the value read from the user.
     */

    public static int promptInt(Scanner sc, String prompt, int min, int max) {
        int val = min - 1; // int value to be returned
        int loop = 0; // determines whether the while loop should be executed or not
        int a = 0; // temporary int used in parsing string
        boolean invalid = false; // checks to see if the int is invalid
        while (loop != 1) {
            System.out.print(prompt);
            if (sc.hasNextLine()) {
                String userEntry = sc.nextLine(); // entry by the user in the form of a string
                userEntry = userEntry.trim();
                if (userEntry.charAt(0) == '-') { // checks for negative numbers
                    userEntry = userEntry.substring(1, userEntry.length()); // ignored the '-' in
                                                                            // the input
                    // try-catch block will try to parse the input as an int as given. If it can't,
                    // it will then convert the input into a string that can be parsed to an int
                    try {
                        val = Integer.parseInt(userEntry);
                    } catch (NumberFormatException e) {
                        char[] number = new char[userEntry.length() - 1];
                        for (int i = 1; i < userEntry.length(); i++) {
                            number[i - 1] = userEntry.charAt(i);
                        }
                        for (int i = 0; i < number.length; i++) {
                            if (!(Character.isDigit(number[i])
                                || Character.isWhitespace(number[i]))) {
                                a = i;
                                break;
                            }
                            // takes desired substring of user input(that which can be parsed to an
                            // int) and
                            // assigns val to it
                            userEntry = userEntry.substring(0, a);
                            userEntry = userEntry.trim();
                            val = Integer.parseInt(userEntry);
                        }
                    }
                    // since the desired int is negative and we ignored it when assigning val, we
                    // must correct it now
                    val /= -1;
                } else if (Character.isDigit(userEntry.charAt(0))) {
                    // try-catch block will try to parse the input as an int as given. If it can't,
                    // it will then convert the input into a string that can be parsed to an int
                    try {
                        val = Integer.parseInt(userEntry);
                    } catch (NumberFormatException e) {
                        char[] number = userEntry.toCharArray();
                        for (int i = 0; i < number.length; i++) {
                            if (!(Character.isDigit(number[i])
                                || Character.isWhitespace(number[i]))) {
                                a = i;
                                break;
                            }
                            // takes desired substring of user input(that which can be parsed to an
                            // int) and
                            // assigns val to it
                            userEntry = userEntry.substring(0, a);
                            userEntry = userEntry.trim();
                            val = Integer.parseInt(userEntry);
                        }
                    }
                } else {
                    System.out.println("Invalid value."); // invalid input
                    invalid = true;
                }
            } else {
                System.out.println("Invalid value."); // invalid input
            }

            if ((val >= min && val <= max)) {
                loop = 1; // exits loop, val is a valid input
            } else {
                if (!invalid) {
                    System.out.println("Invalid value."); // invalid input, prompts again
                }
            }
        }

        return val;
    }

    /**
     * Prompts the user for a char value by displaying prompt. Note: This method
     * should not add a new line to the output of prompt.
     *
     * After prompting the user, the method will read an entire line of input and
     * return the first non-whitespace character converted to lower case.
     *
     * @param sc     The Scanner instance to read from System.in
     * @param prompt The user prompt.
     * @return Returns the first non-whitespace character (in lower case) read from
     *         the user. If there are no non-whitespace characters read, the null
     *         character is returned.
     */
    public static char promptChar(Scanner sc, String prompt) {
        char userChar = '\u0000'; // char to be returned, initially null character
        String userEntry = ""; // user input as a string
        System.out.print(prompt);
        if (sc.hasNextLine()) {
            userEntry = sc.nextLine();
            userEntry = userEntry.trim(); // format the input
            if (userEntry.length() == 0) {
                return '\u0000'; // returns null character if there are no no whitespace characters
            } else {
                userChar = userEntry.toLowerCase().charAt(0);
                return userChar; // if successful, return the entered character
            }
        } else {
            return '\u0000'; // returns null character if there is no input
        }
    }

    /**
     * Prompts the user for a string value by displaying prompt. Note: This method
     * should not add a new line to the output of prompt.
     *
     * After prompting the user, the method will read an entire line of input,
     * removing any leading and trailing whitespace.
     *
     * @param sc     The Scanner instance to read from System.in
     * @param prompt The user prompt.
     * @return Returns the string entered by the user with leading and trailing
     *         whitespace removed.
     */
    public static String promptString(Scanner sc, String prompt) {
        String userEntry = ""; // string to be returned, initialized to blank
        System.out.print(prompt);
        if (sc.hasNextLine()) {
            userEntry = sc.nextLine();
            userEntry = userEntry.trim(); // format the string
            if (userEntry.length() == 0) {
                return null; // returns null if there are no non whitespace characters
            } else {
                return userEntry; // if successful, return the entered string
            }
        } else {
            return null; // returns null if there is no input
        }
    }

    /**
     * Saves the current position in the story to a file.
     *
     * The format of the bookmark file is as follows: Line 1: The value of
     * Config.MAGIC_BOOKMARK Line 2: The filename of the story file from storyFile
     * Line 3: The current room id from curRoom
     *
     * Note: use PrintWriter to print to the file.
     *
     * @param storyFile    The filename containing the cyoa story.
     * @param curRoom      The id of the current room.
     * @param bookmarkFile The filename of the bookmark file.
     * @return false on an IOException, and true otherwise.
     */
    public static boolean saveBookmark(String storyFile, String curRoom, String bookmarkFile) {
        try {
            String fileName = bookmarkFile; // name of the bookmark file to write to, set to the
                                            // parameter given
            PrintWriter writer = new PrintWriter(fileName); // new PrintWriter which will write to
                                                            // the file
            // next 3 lines will write the information needed for parseBookmark in the
            // correct format
            writer.println(Config.MAGIC_BOOKMARK);
            writer.println(storyFile);
            writer.print(curRoom);
            if (writer != null) {
                writer.close(); // prevents resource leaks
            }
        } catch (IOException e) {
            return false; // returns false if there is an IOException
        }

        return true; // returns true if there is no IOException
    }

    /**
     * Loads the story and current location from a file either a story file or a
     * bookmark file. NOTE: This method is partially implementd in Milestone 2 and
     * then finished in Milestone 3.
     * 
     * The type of the file will be determined by reading the first line of the
     * file. The first line of the file should be trimmed of whitespace.
     *
     * If the first line is Config.MAGIC_STORY, then the file is parsed using the
     * parseStory method. If the first line is Config.MAGIC_BOOKMARK, the the file
     * is parsed using the parseBookmark method. Otherwise, print an error message,
     * terminated by a new line, to System.out, displaying: "First line:
     * trimmedLineRead does not correspond to known value.", where trimmedLineRead
     * is the trimmed value of the first line from the file.
     *
     * If there is an IOException, print an error message, terminated by a new line,
     * to System.out, saying "Error reading file: fName", where fName is the value
     * of the parameter.
     *
     * If there is an error reading the first line, print an error message,
     * terminated by a new line, to System.out, displaying: "Unable to read first
     * line from file: fName", where fName is the value of the parameter.
     *
     * This method will be partially implemented in Milestone #2 and completed in
     * Milestone #3 as described below.
     *
     * Milestone #2: Open the file, handling the IOExceptions as described above. Do
     * not read the the first line: Assume the file is a story file and call the
     * parseStory method.
     *
     * Milestone #3: Complete the implementation of this method by reading the first
     * line from the file and following the rules of the method as described above.
     *
     * @param fName   The name of the file to read.
     * @param rooms   The ArrayList structure that will contain the room details. A
     *                parallel ArrayList trans.
     * @param trans   The ArrayList structure that will contain the transition
     *                details. A parallel ArrayList to rooms. Since the rooms can
     *                have multiple transitions, each room will be an
     *                ArrayList<String[]> with one String[] per transition with the
     *                overall structure being an ArrayList of ArrayLists of
     *                String[].
     * @param curRoom An array of at least length 1. The current room id will be
     *                stored in the cell at index 0.
     * @return false if there is an IOException or a parsing error. Otherwise, true.
     */
    public static boolean parseFile(String fName, ArrayList<String[]> rooms,
        ArrayList<ArrayList<String[]>> trans, String[] curRoom) {
        if (fName != null) { // will only try to parse a file if the name is non-null
            try {
                File myFile = new File(fName); // new file object with the path of the given
                                               // filename
                Scanner sc = new Scanner(myFile); // scanner to read the file

                // first line of the file, should either be Config.MAGIC_STORY or
                // Config.MAGIC_BOOKMARK when trimmed
                String firstLine = sc.nextLine().trim();

                // if the first line is null, print error message
                if (firstLine == null) {
                    sc.close();
                    System.out.println("Unable to read first line from file: " + fName);
                }
                // if first line is Config.MAGIC_STORY, call parseStory
                else if (firstLine.equals(Config.MAGIC_STORY)) {
                    parseStory(sc, rooms, trans, curRoom);
                }
                // if first line is Config.MAGIC_BOOKMARK, call parseBookmark
                else if (firstLine.equals(Config.MAGIC_BOOKMARK)) {
                    parseBookmark(sc, rooms, trans, curRoom);
                }
                // if first line is not null or either Config.MAGIC_STORY or
                // Config.MAGIC_BOOKMARK, result in a parse error
                else {
                    System.out.println(
                        "First line: " + firstLine + " does not correspond to known value.");
                    sc.close();
                    return false;
                }
            }
            // catches the various exceptions that could occur while parsing a file, prints
            // error message if any exception is caught and returns false
            catch (IOException e) {
                System.out.println("Error reading file: " + fName);
                return false;
            } catch (NullPointerException e) {
                System.out.println("Error reading file: " + fName);
                return false;
            } catch (NoSuchElementException e) {
                System.out.println("Error reading file: " + fName);
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Loads the story and the current room from a bookmark file. This method
     * assumes that the first line of the file, containing Config.MAGIC_BOOKMARK,
     * has already been read from the Scanner.
     *
     * The format of a bookmark file is as follows: Line No: Contents 1:
     * Config.MAGIC_BOOKMARK 2: Story filename 3: Current room id
     *
     * As an example, the following contents would load the story Goldilocks.story
     * and set the current room to id 7.
     *
     * #!BOOKMARK Goldilocks.story 7
     *
     * Your method should not duplicate the code from the parseFile method. It must
     * use the parseFile method to populate the rooms and trans methods based on the
     * contents of the story filename read and trimmed from line 2 of the file. The
     * value of for the cell at index 0 of curRoom is the trimmed value read on line
     * 3 of the file.
     *
     * @param sc      The Scanner object buffering the input file to read.
     * @param rooms   The ArrayList structure that will contain the room details. A
     *                parallel ArrayList trans.
     * @param trans   The ArrayList structure that will contain the transition
     *                details. A parallel ArrayList to rooms.
     * @param curRoom An array of at least length 1. The current room id will be
     *                stored in the cell at index 0.
     * @return false if there is a parsing error. Otherwise, true.
     */
    public static boolean parseBookmark(Scanner sc, ArrayList<String[]> rooms,
        ArrayList<ArrayList<String[]>> trans, String[] curRoom) {
        boolean success = true; // boolean to be returned, determines whether the bookmark is
                                // successfully parse
                                // or not
        String storyFileName = sc.nextLine().trim(); // story file name used in the call to
                                                     // parseFile, read from the
                                                     // bookmark file
        String roomID = sc.nextLine().trim(); // room Id read from the bookmark file

        // call to parseFile, curRoom is null so parseStory isn't called
        if (!parseFile(storyFileName, rooms, trans, null)) {
            success = false; // false if there is a parsing error
        }
        curRoom[0] = roomID; // assign curRoom element 0 to the room id parsed from the bookmark
        return success;
    }

    /**
     * This method parses a story adventure file.
     *
     * The method will read the contents from the Scanner, line by line, and
     * populate the parallel ArrayLists rooms and trans. As such the story files
     * have a specific structure. The order of the rooms in the story file
     * correspond to the order in which they will be stored in the parallel
     * ArrayLists.
     *
     * When reading the file line-by-line, whitespace at the beginning and end of
     * the line should be trimmed. The file format described below assumes that
     * whitespace has been trimmed.
     *
     * Story file format:
     *
     * - Any line (outside of a room's description) that begins with a '#' is
     * considered a comment and should be ignored. - Room details begin with a line
     * starting with 'R' followed by the room id, terminated with a ':'. Everything
     * after the first colon is the room title. The substrings of the room id and
     * the room title should be trimmed. - The room description begins on the line
     * immediate following the line prefixed with 'R', containing the room id, and
     * continues until a line of ";;;" is read. - The room description may be
     * multi-line. Every line after the first one, should be prefixed with a newline
     * character ('\n'), and concatenated to the previous description lines read for
     * the current room. - The room transitions begin immediately after the line of
     * ";;;", and continue until a line beginning with 'R' is encountered. There are
     * 3 types of transition lines: - 1 -- Terminal Transition: A terminal
     * transition is either Config.SUCCESS or Config.FAIL. This room is the end of
     * the story. This value should be stored as a transition with the String at
     * index Config.TRAN_DESC set to the value read. The rest of the Strings in the
     * transition String array should be null. A room with a terminal transition can
     * only have one transition associated with it. Any additional transitions
     * should result in a parse error. - 2 -- Normal Transition: The line begins
     * with ':' followed by the transition description, followed by " -> " (note the
     * spaces), followed by the room id to transition to. For normal transitions
     * (those without a transition weight), set the value at index Config.TRAN_PROB
     * to null. - 3 -- Weighted Transition: Similar to a normal transition except
     * that there is a probability weight associated with the transition. After the
     * room id (as described in the normal transition) is a '?' followed by the
     * probability weight. - You can assume that room ids do not contain a '?'. -
     * You can assume that Config.SUCCESS and Config.FAIL do not start with a ':'.
     *
     * In the parallel ArrayLists rooms and trans, the internal structures are as
     * follows:
     *
     * The String array structure for each room has a length of Config.ROOM_DET_LEN.
     * The entries in the array are as follows: Index | Description
     * ------------------------------- Config.ROOM_ID | The room id
     * Config.ROOM_TITLE | The room's title Config.ROOM_DESC | The room's
     * description
     *
     * The String array structure for each transition. Note that each room can have
     * multiple transitions, hence, the ArrayList of ArrayLists of String[]. The
     * length of the String[] is Config.TRAN_DET_LEN. The entries in the String[]
     * are as follows: Index | Description
     * ------------------------------------------------------------------
     * Config.TRAN_DESC | The transition description Config.TRAN_ROOM_ID | The
     * transition destination (id of the room) Config.TRAN_PROB | The probability
     * weight for the transition
     *
     * If you encounter a line that violates the story file format, the method
     * should print out an error message, terminated by a new line, to System.out
     * displaying: "Error parsing file on line: lineNo: lineRead", where lineNo is
     * the number of lines read by the parseStory method (i.e. ignoring the magic
     * number if Milestone #3), and lineRead is the offending trimmed line read from
     * the Scanner.
     *
     * After parsing the file, if rooms or trans have zero size, or they have
     * different sizes, print out an error message, terminated by a new line, to
     * System.out displaying: "Error parsing file: rooms or transitions not properly
     * parsed."
     *
     * After parsing the file, if curRoom is not null, store the reference of the id
     * of the room at index 0 of the rooms ArrayList into the cell at index 0 of
     * curRoom.
     *
     * Hint: This method only needs a single loop, reading the file line-by-line.
     * 
     * Hint: To successfully parse the file, you will need to maintain a state of
     * where you are in the file. I.e., are you parsing the description, parsing the
     * transitions; is there an error; etc? One suggestion would be to use an enum
     * to enumerate the different states.
     *
     * @param sc      The Scanner object buffering the input file to read.
     * @param rooms   The ArrayList structure that will contain the room details.
     * @param trans   The ArrayList structure that will contain the transition
     *                details.
     * @param curRoom An array of at least length 1. The current room id will be
     *                stored in the cell at index 0.
     * @return false if there is a parsing error. Otherwise, true.
     */
    public static boolean parseStory(Scanner sc, ArrayList<String[]> rooms,
        ArrayList<ArrayList<String[]>> trans, String[] curRoom) {
        storyState currentState = storyState.BEGINNING;

        String currentLine = ""; // current line to be read
        String currRoomID = ""; // id of the current room being populated
        String currRoomTitle = "";// title of the current room being populated
        String currRoomDesc = "";// description of the current room being populated
        String currTransDesc = "";// id of the current transition being populated
        String currTransWeight = "";// transition weight of the current transition being
                                    // populated(will be null if
                                    // not applicable)
        String currTransDestID = "";// destination room id of the current transition being populated
        final String ARROW = " -> "; // string used to find when a transition description ends
        final int ARROW_LENGTH = ARROW.length(); // length of the above arrow string

        boolean parseSuccess = true; // boolean to be returned, tells whether or not file was
                                     // successfully parsed
        boolean checkMultipleTermTrans = false; // boolean used when making sure a room does not
                                                // have multiple
                                                // terminal transitions

        int roomDescLineNum = 1; // measures the current line number of the room description since
                                 // they may have
                                 // multiple lines
        int currRoomIndex = -1; // index of the current room string[] in the rooms ArrayList, starts
                                // at -1
                                // because it will be incremented before it is first used, thus the
                                // value will
                                // become 0 when appropriate
        int currTransNum = 0; // current transition number(index) in the trans ArrayList
        int currentLineNumber = 1; // current line of the story file

        // only will process another line if there is one to be processed
        while (sc.hasNextLine()) {
            currentLine = sc.nextLine();
            currentLine = currentLine.trim(); // formats the current line

            /**
             * Parses comments. Checks if the current line is a comment. If it is, ignore
             * it, increment currentLineNumber, and move on to the next line. Else,
             * continue.
             */
            if (!currentLine.equals(null) && !currentLine.equals("") && currentLine.charAt(0) == '#'
                && currentState != storyState.ROOMDESC) {
                currentLineNumber++; // increments currentLineNumber when moving on to the next line
                                     // in the story
                                     // file
                continue; // go to the next line, if it exists
            }

            /**
             * Parses the room id and title. Checks if the current line is a room id/title.
             * If it is, start with the room id. The room id will be the string starting at
             * the beginning of the line and ending when the next character is a colon. Trim
             * the room id. Add a new room string[] to the rooms ArrayList and a new
             * transition(to correspond to the current room) to the trans ArrayList. Set the
             * current room's id to the id that was just parsed(currRoomID). Then reset
             * currRoomID to a blank string so it is ready for the next room. Next will be
             * the room title, starting on the same line.
             * 
             * The room title will be the substring of the current line from(not including)
             * the first colon to the end of the current line. Trim the room title and set
             * the current room's title to the title that was just parsed(currRoomTitle).
             * Then reset currRoomTitle to a blank string so it is ready for the next room.
             * Next will be the room description, starting on the next line.
             */
            else if (!currentLine.equals(null) && !currentLine.equals("")
                && currentState != storyState.ROOMDESC && currentLine.charAt(0) == 'R') {
                currentState = storyState.ROOMID; // sets story state to room id
                currRoomIndex++; // increments the current room index(in the rooms ArrayList)
                currTransNum = 0; // resets current transition number to 0 since this is a new room
                for (int i = 1; i < currentLine.length() && currentLine.charAt(i) != ':'; i++) {
                    currRoomID += currentLine.charAt(i);
                }
                currRoomID = currRoomID.trim(); // formats room id
                rooms.add(currRoomIndex, new String[Config.ROOM_DET_LEN]); // adds new string[] to
                                                                           // rooms and transition
                                                                           // ArrayList to trans
                trans.add(currRoomIndex, new ArrayList<String[]>());
                rooms.get(currRoomIndex)[Config.ROOM_ID] = currRoomID; // sets the appropriate room
                                                                       // id to the current
                                                                       // room id that was just
                                                                       // parsed
                currRoomID = ""; // resets to blank string

                currentState = storyState.ROOMTITLE; // sets story state to room title
                currRoomTitle =
                    currentLine.substring(currentLine.indexOf(":") + 1, currentLine.length());
                currRoomTitle = currRoomTitle.trim(); // formats room title
                rooms.get(currRoomIndex)[Config.ROOM_TITLE] = currRoomTitle;
                currRoomTitle = ""; // resets to blank string
                currentState = storyState.ROOMDESC; // sets story state to room description
                currentLineNumber++; // increments currentLineNumber when moving on to the next line
                                     // in the story
                                     // file
                continue; // go to the next line, if it exists
            }

            /**
             * Parses the room description. Checks if the current line is a part of a room
             * description or the end of the room description. If the line does not begin
             * with 3 semicolons, then it is part of the room description, so we add it to
             * the current room's description(currRoomDesc), adding newline characters after
             * the first line. If the line does begin with 3 semicolons, that signals the
             * end of the room description. Trim currRoomDesc and set the current room's
             * description equal to it. Then reset currRoomDesc to a blank string so it is
             * ready for the next use. Next will be the transitions.
             */
            else if (!currentLine.equals(null) && !currentLine.equals("")
                && currentState == storyState.ROOMDESC) {
                if (!currentLine.equalsIgnoreCase(";;;")) {
                    if (roomDescLineNum == 1) {
                        currRoomDesc += currentLine; // add the current line to currRoomDesc
                    } else {
                        currRoomDesc += "\n" + currentLine; // add a newline + the current line to
                                                            // currRoomDesc
                    }
                    roomDescLineNum++; // increment the room description line number, for
                                       // tracing/debugging purposes
                } else {
                    currRoomDesc = currRoomDesc.trim(); // format currRoomDesc
                    rooms.get(currRoomIndex)[Config.ROOM_DESC] = currRoomDesc; // set the current
                                                                               // room's description
                                                                               // equal to
                                                                               // currRoomDesc
                    currRoomDesc = ""; // resets the description to a blank string
                    checkMultipleTermTrans = false; // resets checkMultipleTermTrans b/c this is a
                                                    // new room
                    currentState = storyState.TRANSITION; // sets story state to generic transition
                }
                currentLineNumber++; // increments currentLineNumber when moving on to the next line
                                     // in the story
                                     // file
                continue; // go to the next line, if it exists
            }

            /**
             * Parses transitions for the current room. Checks if the currentLine is a
             * transition. Then checks if there are multiple transitions after encountering
             * a terminal transition(checks again after parsing the current line).
             * 
             * Checks if the current transition equals Config.SUCCESS or Config.FAIL,
             * meaning it's a terminal transition. Set the current transition to whichever
             * terminal the current transition is. Then set all other strings in the current
             * string[] to null except the transition description. Set
             * checkMultipleTermTrans to true, so a parse error occurs if the given room has
             * any more transitions.
             * 
             * If the current transition isn't a terminal transition, check if it's a normal
             * transition. Create a new string[] in trans to represent the transition. set
             * the current transition description(trimmed) to be the current line up until
             * the first occurrence of the arrow. Set the current transitions destination id
             * to the first string(trimmed) after the arrow and before a question mark, if
             * there is one.
             * 
             * If the transition is a normal transition and has a weight(probability)
             * associated with it, set the weight equal to the string(trimmed) after the
             * first occurrence of a question mark.
             * 
             * Prepare variables for the next transition by setting them to blank strings
             * where applicable. Increment the current transition number.
             * 
             * If current line is null where there should be a transition, result in a parse
             * error.
             */
            else if (!currentLine.equals(null) && !currentLine.equals("")
                && currentState == storyState.TRANSITION) {
                if (checkMultipleTermTrans) {
                    System.out.println(
                        "Error parsing file on line: " + currentLineNumber + " " + currentLine);
                    parseSuccess = false;
                    return parseSuccess; // parse error if there are multiple transitions and one of
                                         // them is a terminal
                                         // transition
                }
                if (currentLine.equalsIgnoreCase(Config.SUCCESS)
                    || currentLine.equalsIgnoreCase(Config.FAIL)) {
                    currentState = storyState.TERMINALTRANSITION; // sets story state to terminal
                                                                  // transition

                    // creates new transition string[] and sets it to the current line
                    trans.get(currRoomIndex).add(currTransNum, new String[Config.TRAN_DET_LEN]);
                    trans.get(currRoomIndex).get(currTransNum)[Config.TRAN_DESC] = currentLine;
                    for (int i = 0; i < trans.get(currRoomIndex).get(currTransNum).length; i++) {
                        if (i != Config.TRAN_DESC) {
                            trans.get(currRoomIndex).get(currTransNum)[i] = null; // sets all other
                                                                                  // strings to null
                        }
                    }
                    checkMultipleTermTrans = true; // will result in a parseError if another
                                                   // transition is encountered
                                                   // after a terminal transition is found
                    currentLineNumber++; // increments currentLineNumber when moving on to the next
                                         // line in the story
                                         // file
                    continue; // go to the next line, if it exists
                }
                if (currentLine.charAt(0) != 'R') {
                    currentState = storyState.NORMALTRANSITION; // sets story state to normal
                                                                // transition
                    trans.get(currRoomIndex).add(currTransNum, new String[Config.TRAN_DET_LEN]);

                    // sets the current transition description equal to the substring of the current
                    // line from the beginning to the first occurrence of an arrow
                    currTransDesc = currentLine.substring(1, currentLine.indexOf(ARROW)).trim();
                    trans.get(currRoomIndex).get(currTransNum)[Config.TRAN_DESC] = currTransDesc;

                    // sets the transition destination id equal to whatever follows the arrow up
                    // until a question mark if there is one, else go to the end of the line
                    int indexOfArrow = currentLine.indexOf(ARROW); // index of the arrow string

                    // had problems with a ? being in the transition description, so corrected this
                    // by setting indexOfQuestionMark to the first ? after the arrow instead of just
                    // the first ? in the current line
                    int indexOfQuestionMark =
                        currentLine.substring(indexOfArrow).indexOf('?') + indexOfArrow;

                    for (int i =
                        indexOfArrow + ARROW_LENGTH; (indexOfQuestionMark == indexOfArrow - 1
                            || i < indexOfQuestionMark) && i < currentLine.length(); i++) {
                        currTransDestID += currentLine.charAt(i);
                    }
                    currTransDestID = currTransDestID.trim();
                    trans.get(currRoomIndex).get(currTransNum)[Config.TRAN_ROOM_ID] =
                        currTransDestID;

                    // sets the current transition weight to whatever follows the question mark to
                    // the end of the line, then resets it for next use
                    for (int i = currentLine.indexOf(ARROW); i < currentLine.length(); i++) {
                        if (currentLine.charAt(i) == '?') {
                            currentState = storyState.WEIGHTEDTRANSITION; // sets story state to
                                                                          // weighted transition
                            for (int j = i + 1; j < currentLine.length(); j++) {
                                currTransWeight += currentLine.charAt(j);
                            }
                            currTransWeight = currTransWeight.trim();
                            trans.get(currRoomIndex).get(currTransNum)[Config.TRAN_PROB] =
                                currTransWeight;
                            currTransWeight = "";
                        }
                    }
                }
                currTransDestID = ""; // resets the current transition destination id and
                                      // description to blank strings
                                      // so they are ready for the next transition
                currTransDesc = "";
                currentState = storyState.TRANSITION; // sets story state to generic transition
                currTransNum++; // increments the current transition number for a given room
            } else {
                // gives parse error if current line is not blank when it should be, meaning the
                // line cannot be read as it does not fit any of the story states
                if (!currentLine.equals("")) {
                    System.out.println(
                        "Error parsing file on line: " + currentLineNumber + " " + currentLine);
                }
            }
            if (checkMultipleTermTrans && trans.get(currRoomIndex).size() > 1) {
                System.out.println(
                    "Error parsing file on line: " + currentLineNumber + " " + currentLine);
                parseSuccess = false;
                return parseSuccess; // parse error if there are multiple transitions and one of
                                     // them is a terminal
                                     // transition
            }
            currentLineNumber++; // increments currentLineNumber when moving on to the next line in
                                 // the story
                                 // file
        }

        /**
         * checks to make sure curRoom is not null if it is, do nothing if it is not,
         * set the curRoom's room id to that of the room at index 0 in the rooms
         * ArrayList
         */
        if (curRoom != null) {
            try {
                curRoom[0] = rooms.get(0)[Config.ROOM_ID];
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }

        /**
         * checks to make sure there are no parsing errors. Error occurs if any of these
         * are true: 1- the rooms ArrayList is empty
         * 
         * 2- the trans ArrayList is empty
         * 
         * 3-the rooms and trans ArrayLists are different sizes
         */
        if (rooms.size() == 0 || trans.size() == 0 || rooms.size() != trans.size()) {
            System.out.println("Error parsing file: rooms or transitions not properly parsed");
            parseSuccess = false;
            return parseSuccess; // returns parseSuccess if there is an error
        }
        return parseSuccess; // returns parseSuccess once file is finished parsing
    }

    /**
     * Returns the index of the given room id in an ArrayList of rooms.
     *
     * Each entry in the ArrayList contain a String array, containing the details of
     * a room. The String array structure, which has a length of
     * Config.ROOM_DET_LEN, and has the following entries: Index | Description
     * -------------------------------------------- Config.ROOM_ID | The room id
     * Config.ROOM_TITLE | The room's title Config.ROOM_DESC | The room's
     * description
     *
     * @param id    The room id to search for.
     * @param rooms The ArrayList of rooms.
     * @return The index of the room with the given id if found in rooms. Otherwise,
     *         -1.
     */
    public static int getRoomIndex(String id, ArrayList<String[]> rooms) {
        for (int i = 0; i < rooms.size(); i++) { // cycles through all rooms
            if (id.equals(rooms.get(i)[Config.ROOM_ID])) {
                return i;// returns i if given id is found in rooms
            }
        }

        return -1; // returns -1 if given id isn't found in rooms
    }

    /**
     * Returns the room String array of the given room id in an ArrayList of rooms.
     *
     * Remember to avoid code duplication!
     *
     * @param id    The room id to search for.
     * @param rooms The ArrayList of rooms.
     * @return The reference to the String array in rooms with the room id of id.
     *         Otherwise, null.
     */

    public static String[] getRoomDetails(String id, ArrayList<String[]> rooms) {
        for (int i = 0; i < rooms.size(); i++) { // cycles through rooms
            if (id.equals(rooms.get(i)[Config.ROOM_ID])) {
                return rooms.get(i); // returns string[] reference if it is found
            }
        }
        return null; // returns null if given reference isn't found
    }

    /**
     * Prints out a line of characters to System.out. The line should be terminated
     * by a new line.
     *
     * @param len The number of times to print out c.
     * @param c   The character to print out.
     */
    public static void printLine(int len, char c) {
        for (int i = 0; i < len; i++) { // prints out char c len number of times
            System.out.print(Character.toString(c));
        }
    }

    /**
     * Prints out a String to System.out, formatting it into lines of length no more
     * than len characters.
     * 
     * This method will need to print the string out character-by-character,
     * counting the number of characters printed per line. If the character to
     * output is a newline, print it out and reset your counter. If it reaches the
     * maximum number of characters per line, len, and the next character is: -
     * whitespace (as defined by the Character.isWhitespace method): print a new
     * line character, and move onto the next character. - NOT a letter or digit (as
     * defined by the Character.isLetterOrDigit method): print out the character, a
     * new line, and move onto the next character. - Otherwise: - If the previous
     * character is whitespace, print a new line then the character. - Otherwise,
     * print a '-', a new line, and then the character. Remember to reset the
     * counter when starting a new line.
     *
     * After printing out the characters in the string, a new line is output.
     *
     * @param len The maximum number of characters to print out.
     * @param val The string to print out.
     */
    public static void printString(int len, String val) {
        val = val.trim(); // formats the string input
        int currentStringLength = val.length(); // assigns the string length to a variable
        int currentCharIndex = 0; // index of the current character in val
        char[] valAsCharArray = val.toCharArray(); // string val represented as a char[]
        System.out.println();
        while (currentCharIndex < valAsCharArray.length) { // while there is a character to be
                                                           // printed
            // prints out characters until the max number of characters per line has been
            // printed, then begins a new line and resets. reset i and break each time a
            // character is printed so the loop counts the characters correctly.
            for (int i = 0; i < len && currentCharIndex < currentStringLength; i++) {
                // if the character is a newline, print it twice, unless its after a finished
                // sentence, then only print it once
                if (valAsCharArray[currentCharIndex] == '\n') {
                    if (valAsCharArray[currentCharIndex - 1] == '.'
                        && Character.isLetter(valAsCharArray[currentCharIndex + 1])) {
                        System.out.println();
                        System.out.println();
                        currentCharIndex++;
                        i = 0;
                        break;
                    } else {
                        System.out.println();
                        currentCharIndex++;
                        i = 0;
                        break;
                    }
                    // if the end of the line has been reached
                } else if (i == len - 1) {
                    // if character is whitespace, print a newline
                    if (Character.isWhitespace(valAsCharArray[currentCharIndex])) {
                        System.out.println();
                        i = -1; // resets i properly as it is incremented
                        // if character is a letter or digit
                    } else if (Character.isLetterOrDigit(valAsCharArray[currentCharIndex])) {
                        // if character before it is whitespace, print a newline then the character
                        if (Character.isWhitespace(valAsCharArray[currentCharIndex - 1])) {
                            System.out.println();
                            System.out.print(Character.toString(valAsCharArray[currentCharIndex]));
                        }
                        // if character before it is not whitespace, print a hyphen, newline, then
                        // the
                        // character
                        if (!Character.isWhitespace(valAsCharArray[currentCharIndex - 1])) {
                            System.out.print(Character.toString('-') + "\n"
                                + Character.toString(valAsCharArray[currentCharIndex]));
                        }
                        i = 0;
                        // if character is not a letter, digit, or whitespace, print it along with a
                        // newline
                    } else if (!Character.isLetterOrDigit(valAsCharArray[currentCharIndex])
                        && !Character.isWhitespace(valAsCharArray[currentCharIndex])) {
                        System.out.println(valAsCharArray[currentCharIndex]);
                        i = 0;
                    }
                    // if it is not at the end of the line, print the character normally
                } else {
                    System.out.print(valAsCharArray[currentCharIndex]);
                }
                // no matter what, increment current character index to stay on the right
                // character in the character array
                currentCharIndex++;
            }
        }
    }

    /**
     * This method prints out the room title and description to System.out.
     * Specifically, it first loads the room details, using the getRoomDetails
     * method. If no room is found, the method should return, avoiding any runtime
     * errors.
     *
     * If the room is found, first a line of Config.LINE_CHAR of length
     * Config.DISPLAY_WIDTH is output. Followed by the room's title, a new line, and
     * the room's description. Both the title and the description should be printed
     * using the printString method with a maximum length of Config.DISPLAY_WIDTH.
     * Finally, a line of Config.LINE_CHAR of length Config.DISPLAY_WIDTH is output.
     *
     * @param id    Room ID to display
     * @param rooms ArrayList containing the room details.
     */
    public static void displayRoom(String id, ArrayList<String[]> rooms) {
        boolean roomFound = false; // boolean to tell whether the room with the id parameter has
                                   // been found
        String[] currRoom = getRoomDetails(id, rooms); // string[] to represent the current room
                                                       // filled with the
                                                       // appropriate details
        for (int i = 0; i < currRoom.length; i++) {
            if (currRoom[Config.ROOM_ID].equals(id)) {
                roomFound = true; // room id found if there is a room with a matching id in the
                                  // rooms arraylist
            }
        }
        // if the room is found, print a line of Config.LINE_CHAR, then print the room
        // title and description, then another line of characters.
        // else, return.
        if (roomFound) {
            printLine(Config.DISPLAY_WIDTH, Config.LINE_CHAR);
            printString(Config.DISPLAY_WIDTH, currRoom[Config.ROOM_TITLE]);
            System.out.println();
            printString(Config.DISPLAY_WIDTH, currRoom[Config.ROOM_DESC]);
            System.out.println();
            printLine(Config.DISPLAY_WIDTH, Config.LINE_CHAR);
        } else {
            return;
        }
    }

    /**
     * Prints out and returns the transitions for a given room.
     *
     * If the room ID of id cannot be found, nothing should be output to System.out
     * and null should be returned.
     *
     * If the room is a terminal room, i.e., the transition list is consists of only
     * a single transition with the value at index Config.TRAN_DESC being either
     * Config.SUCCESS or Config.FAIL, nothing should be printed out.
     *
     * The transitions should be output in the same order in which they are in the
     * ArrayList, and only if the transition probability (String at index TRAN_PROB)
     * is null. Each transition should be output on its own line with the following
     * format: idx) transDesc where idx is the index in the transition ArrayList and
     * transDesc is the String at index Config.TRAN_DESC in the transition String
     * array.
     *
     * See parseStory method for the details of the transition String array.
     *
     * @param id    The room id of the transitions to output and return.
     * @param rooms The ArrayList structure that contains the room details.
     * @param trans The ArrayList structure that contains the transition details.
     * @return null if the id cannot be found in rooms. Otherwise, the reference to
     *         the ArrayList of transitions for the given room.
     */
    public static ArrayList<String[]> displayTransitions(String id, ArrayList<String[]> rooms,
        ArrayList<ArrayList<String[]>> trans) {
        boolean roomFound = false; // boolean to determine whether the room is found
        String[] currRoom = getRoomDetails(id, rooms); // creates and fills a string[] to represent
                                                       // the current room
        int currRoomIndex = getRoomIndex(id, rooms); // current index of the room with the matching
                                                     // id in the rooms
                                                     // ArrayList
        if (currRoom == null || currRoomIndex == -1) {
            return null; // returns null if the room is null or not found
        }
        if (currRoom[Config.ROOM_ID].equals(id)) {
            roomFound = true; // sets room found to true if the room that matches the id parameter
                              // is found
        }
        if (roomFound) {
            if (trans.get(currRoomIndex).get(0)[Config.TRAN_DESC].equals(Config.SUCCESS)
                || trans.get(currRoomIndex).get(0)[Config.TRAN_DESC].equals(Config.FAIL)) {
                // do nothing if the transition is a terminal transition
            } else {
                // if the transition is not terminal, display each transition using the
                // printString method
                for (int i = 0; i < trans.get(currRoomIndex).size(); i++) {
                    if (!trans.get(currRoomIndex).equals(null)
                        && !trans.get(currRoomIndex).get(i).equals(null)) {
                        if (trans.get(currRoomIndex).get(i)[Config.TRAN_PROB] == null) {
                            printString(Config.DISPLAY_WIDTH,
                                (i) + ") " + trans.get(currRoomIndex).get(i)[Config.TRAN_DESC]);
                        }
                    }
                }
            }
            System.out.println(); // end with a newline and return the index of the arraylist of
                                  // transitions for
                                  // the given room
            return trans.get(getRoomIndex(id, rooms));
        } else {
            return null; // return null if room is not found
        }
    }

    /**
     * Returns the next room id, selected randomly based on the transition
     * probability weights.
     *
     * If curTrans is null or the total sum of all the probability weights is 0,
     * then return null. Use Integer.parseInt to convert the Strings at index
     * Config.TRAN_PROB of the transition String array to integers. If there is a
     * NumberFormatException, return null.
     *
     * It is important to follow the specifications of the random process exactly.
     * Any deviation may result in failed tests. The random transition work as
     * follows: - Let totalWeight be the sum of the all the transition probability
     * weights in curTrans. - Draw a random integer between 0 and totalWeight - 1
     * (inclusive) from rand. - From the beginning of the ArrayList curTrans, start
     * summing up the transition probability weights. - Return the String at index
     * Config.TRAN_ROOM_ID of the first transition that causes the running sum of
     * probability weights to exceed the random integer.
     *
     * See parseStory method for the details of the transition String array.
     *
     * @param rand     The Random class from which to draw random values.
     * @param curTrans The ArrayList structure that contains the transition details.
     * @return The room id that was randomly selected if the sum of probabilities is
     *         greater than 0. Otherwise, return null. Also, return null if there is
     *         a NumberFormatException.
     */
    public static String probTrans(Random rand, ArrayList<String[]> curTrans) {
        if (curTrans == null) {
            return null;
        }
        int totalWeight = 0; // sum of all the transition weights
        try {
            for (int i = 0; i < curTrans.size(); i++) {
                if (curTrans.get(i)[Config.TRAN_PROB] == null) {
                    return null;
                }
                totalWeight += Integer.parseInt(curTrans.get(i)[Config.TRAN_PROB]);
            }
        }
        // catches a NumberFormatException in case totalWeight cannot be parsed to an
        // int, returns null if an exception is caught
        catch (NumberFormatException e) {
            return null;
        }
        // if totalWeight turns out to be 0 somehow, return null
        if (totalWeight == 0) {
            return null;
        }
        int randomInt = rand.nextInt(totalWeight); // random int used for determining which
                                                   // transition is chosen
        int runningSum = 0; // running sum of the transition weights

        // for loop will cycle through all the transitions until the current
        // transition's weight added to the previous running sum is greater than the
        // randomly generated integer, then will return that transition
        for (int i = 0; i < curTrans.size(); i++) {
            runningSum += Integer.parseInt(curTrans.get(i)[Config.TRAN_PROB]);
            if (runningSum > randomInt) {
                return curTrans.get(i)[Config.TRAN_ROOM_ID];
            }
        }
        return null;
    }

    /**
     * This is the main method for the Story Adventure game. It consists of the main
     * game loop and play again loop with calls to the various supporting methods.
     * This method will evolve over the 3 milestones.
     * 
     * The Scanner object to read from System.in and the Random object with a seed
     * of Config.SEED will be created in the main method and used as arguments for
     * the supporting methods as required.
     *
     * Milestone #1: - Print out the welcome message: "Welcome to this choose your
     * own adventure system!" - Begin the play again loop: - Prompt for a filename
     * using the promptString method with the prompt: "Please enter the story
     * filename: " - Prompt for a char using the promptChar method with the prompt:
     * "Do you want to try again? " - Repeat until the character returned by
     * promptChar is an 'n' - Print out "Thank you for playing!", terminated by a
     * newline.
     *
     *
     * Milestone #2: - Print out the welcome message: "Welcome to this choose your
     * own adventure system!" - Begin the play again loop: - Prompt for a filename
     * using the promptString method with the prompt: "Please enter the story
     * filename: " - If the file is successfully parsed using the parseFile method:
     * - Begin the game loop with the current room ID being that in the 0 index of
     * the String array passed into the parseFile method as the 4th parameter -
     * Output the room details via the displayRoom method - Output the transitions
     * via the displayTransitions method - If the current transition is not
     * terminal: - Prompt the user for a number between -1 and the number of
     * transitions minus 1, using the promptInt method with a prompt of "Choose: " -
     * If the returned value is -1: - read a char using promptChar with a prompt of
     * "Are you sure you want to quit the adventure? " - Set the current room ID to
     * Config.FAIL if that character returned is 'y' - Otherwise: Set the current
     * room ID to the room ID at index Config.TRAN_ROOM_ID of the selected
     * transition. - Otherwise, the current transition is terminal: Set the current
     * room ID to the terminal state in the transition String array. - Continue the
     * game loop until the current room ID is Config.SUCCESS or Config.FAIL - If the
     * current room ID is Config.FAIL, print out the message (terminated by a line):
     * "You failed to complete the adventure. Better luck next time!" - Otherwise:
     * print out the message (terminated by a line): "Congratulations! You
     * successfully completed the adventure!" - Prompt for a char using the
     * promptChar method with the prompt: "Do you want to try again? " - Repeat
     * until the character returned by promptChar is an 'n' - Print out "Thank you
     * for playing!", terminated by a newline.
     *
     * Milestone #3: - Print out the welcome message: "Welcome to this choose your
     * own adventure system!" - Begin the play again loop: - Prompt for a filename
     * using the promptString method with the prompt: "Please enter the story
     * filename: " - If the file is successfully parsed using the parseFile method:
     * - Begin the game loop with the current room ID being that in the 0 index of
     * the String array passed into the parseFile method as the 4th parameter -
     * Output the room details via the displayRoom method - Output the transitions
     * via the displayTransitions method - If the current transition is not
     * terminal: - If the value returnd by the probTrans method is null: - Prompt
     * the user for a number between -2 and the number of transitions minus 1, using
     * the promptInt method with a prompt of "Choose: " - If the returned value is
     * -1: - read a char using promptChar with a prompt of "Are you sure you want to
     * quit the adventure? " - Set the current room ID to Config.FAIL if that
     * character returned is 'y' - If the returned value is -2: - read a String
     * using the promptString method with a prompt of: "Bookmarking current
     * location: curRoom. Enter bookmark filename: ", where curRoom is the current
     * room ID. - Call the saveBookmark method and output (terminated by a new
     * line): - if successful: "Bookmark saved in fSave" - if unsuccessful: "Error
     * saving bookmark in fSave" where fSave is the String returned by promptString.
     * - Otherwise: Set the current room ID to the room id at index
     * Config.TRAN_ROOM_ID of the selected transition. - Otherwise, the value
     * returned by probTrans is not null: make this value the current room ID. -
     * Continue the game loop until the current room ID is Config.SUCCESS or
     * Config.FAIL. - If the current room ID is Config.FAIL, print out the message
     * (terminated by a line): "You failed to complete the adventure. Better luck
     * next time!" - Otherwise: print out the message (terminated by a line):
     * "Congratulations! You successfully completed the adventure!" - Prompt for a
     * char using the promptChar method with the prompt: "Do you want to try again?
     * " - Repeat until the character returned by promptChar is an 'n' - Print out
     * "Thank you for playing!", terminated by a newline.
     * 
     * @param args Unused
     */
    public static void main(String[] args) {
        // prints out welcome message
        System.out.println("Welcome to this choose your own adventure system!");
        Scanner scnr = new Scanner(System.in); // scanner object used throughout the program
        Random rand = new Random(Config.SEED); // random object used in probTrans
        int transInput = 0; // user input when dealing with transitions
        char exitInput = '\u0000'; // user input when dealing with exiting the adventure
        boolean runGame = false; // boolean for the main game while loop
        boolean gameOverPrompt = false; // boolean for the game over prompt loop
        boolean promptFile = true; // boolean for the file prompt loop
        boolean playAgain = true; // boolean for the play again loop
        String storyFileName = ""; // story filename, will be set to the user input when appropriate
        String fSave = ""; // bookmark file name, will be set to user input when appropriate
        String currentRoomID = ""; // current room id, used to keep track of the current room
        String currProbTrans = ""; // current transition probability(when applicable)

        // rooms ArayList, stores each room as a String[]. Parallel ArrayList to trans
        ArrayList<String[]> rooms = new ArrayList<String[]>();

        // Parallel ArrayList to rooms, stores the transitions(ArrayLists) and their
        // information(String[]s) that correspond to each room in rooms
        ArrayList<ArrayList<String[]>> trans = new ArrayList<ArrayList<String[]>>();

        // current room id, obtained when story/bookmark is initially parsed
        String[] curRoom = new String[1];

        while (playAgain) { // will keep playing until user inputs an 'n' when prompted
            while (promptFile) { // prompts for the file name until a valid one is entered
                storyFileName = promptString(scnr, "Please enter the story filename: ");
                if (storyFileName == null) {
                    break;
                }
                // if valid filename is entered, program will proceed to main game loop after
                // parsing the file and getting the correct room id to start at
                // if invalid filename the user will be asked if they want to try to enter
                // another one, if they don't the game ends otherwise the prompting loop resets.
                if (parseFile(storyFileName, rooms, trans, curRoom)) {
                    runGame = true;
                    promptFile = false;
                    break;
                } else {
                    gameOverPrompt = true;
                    while (gameOverPrompt) {
                        if (promptString(scnr, "Do you want to try again? ").charAt(0) == 'n') {
                            System.out.println("Thank you for playing!");
                            gameOverPrompt = false;
                            promptFile = false;
                            runGame = false;
                            playAgain = false;
                            break;
                        } else {
                            break;
                        }
                    }
                }
            }
            if (gameOverPrompt == false && runGame == false) {
                break;
            }

            // sets the starting room ID
            currentRoomID = curRoom[0];

            // start of main game loop
            while (runGame && !currentRoomID.equals(Config.FAIL)
                && !currentRoomID.equals(Config.SUCCESS)) {
                displayRoom(currentRoomID, rooms); // display room and transitions to the user by
                                                   // calling the
                                                   // appropriate methods
                displayTransitions(currentRoomID, rooms, trans);
                // checks if the current transition is terminal
                if (!(trans.get(getRoomIndex(currentRoomID, rooms)).get(0)[Config.TRAN_DESC]
                    .equals(Config.SUCCESS)
                    || trans.get(getRoomIndex(currentRoomID, rooms)).get(0)[Config.TRAN_DESC]
                        .equals(Config.FAIL))) {
                    // updates currProbTrans, will be null unless the current transition has a
                    // weight
                    currProbTrans = probTrans(rand, trans.get(getRoomIndex(currentRoomID, rooms)));
                    // if the current transition is not weights, proceed as normal, prompting the
                    // user for an int corresponding to the user selected transition or their desire
                    // to quit/bookmark the adventure
                    if (currProbTrans == null) {
                        transInput = promptInt(scnr, "Choose: ", -2,
                            trans.get(getRoomIndex(currentRoomID, rooms)).size());
                        // if input is -1 ask if they want to quit the adventure, prompting for a
                        // char,
                        // if the input is anything except 'y', proceed as normal, checking for an
                        // inputOutOfBoundsException
                        if (transInput == -1) {
                            exitInput =
                                promptChar(scnr, "Are you sure you want to quit the adventure? ");
                            if (exitInput == 'y') {
                                currentRoomID = Config.FAIL;
                            } else {
                                if (Character.isDigit(exitInput)) {
                                    try {
                                        currentRoomID = trans
                                            .get(getRoomIndex(currentRoomID, rooms)).get(Character
                                                .getNumericValue(exitInput))[Config.TRAN_ROOM_ID];
                                    } catch (IndexOutOfBoundsException e) {

                                    }
                                }
                            }
                        }
                        // if the returned value is -2, read a String using the promptString
                        // method with a prompt of: "Bookmarking current location: curRoom. Enter
                        // bookmark filename: ", where curRoom is the current room ID. Then call
                        // saveBookmark and print the appropriate message depending on if the return
                        // value is true or false
                        else if (transInput == -2) {
                            fSave = promptString(scnr, "Bookmarking current location: "
                                + currentRoomID + ". Enter bookmark filename: ");
                            if (saveBookmark(storyFileName, currentRoomID, fSave)) {
                                System.out.println("Bookmark saved in " + fSave);
                            } else {
                                System.out.println("Error saving bookmark in " + fSave);
                            }
                        }
                        // otherwise, set the current room ID to the room id at index
                        // Config.TRAN_ROOM_ID of the selected transition
                        else {
                            currentRoomID = trans.get(getRoomIndex(currentRoomID, rooms))
                                .get(transInput)[Config.TRAN_ROOM_ID];
                        }
                    }
                    // otherwise, if currProbTrans is not null, assign the value to the current room
                    // ID
                    else {
                        currentRoomID = currProbTrans;
                    }
                } else {
                    currentRoomID =
                        trans.get(getRoomIndex(currentRoomID, rooms)).get(0)[Config.TRAN_DESC];
                }
            }
            // prints out failure message if the user fails to complete the adventure
            // successfully, then goes to game over loop, which asks if the user wants to
            // play again and prompts for a string.
            if (currentRoomID.equals(Config.FAIL)) {
                System.out.println("You failed to complete the adventure. Better luck next time!");
                gameOverPrompt = true;
            }
            // prints out success message if user completes the adventure successfully, then
            // goes to game over loop, which asks if the user wants to play again and
            // prompts for a string.
            if (currentRoomID.equals(Config.SUCCESS)) {
                System.out.println("Congratulations! You successfully completed the adventure!");
                gameOverPrompt = true;
            }
            // enters game over loop, which asks if the user wants to play again and
            // prompts for a string to determine if the user should be prompted for another
            // file name, or terminate the program.
            while (gameOverPrompt) {
                if (promptString(scnr, "Do you want to try again? ").charAt(0) == 'n') {
                    System.out.println("Thank you for playing!");
                    gameOverPrompt = false;
                    playAgain = false;
                    promptFile = false;
                    runGame = false;
                } else {
                    promptFile = true;
                    break;
                }
            }
        }
    }
}
