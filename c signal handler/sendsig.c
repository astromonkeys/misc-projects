////////////////////////////////////////////////////////////////////////////////
// Main File:        sendsig.c
// This File:        sendsig.c
// Other Files:      mySigHandler.c, sendsig.c, division.c
// Semester:         CS 354 Fall 2020
// Instructor:       deppeler
//
// Discussion Group: 641
// Author:           Noah Zurn
// Email:            nzurn@wisc.edu
// CS Login:         zurn
//
/////////////////////////// OTHER SOURCES OF HELP //////////////////////////////
//                   fully acknowledge and credit all sources of help,
//                   other than Instructors and TAs.
//
// Persons:          Identify persons by name, relationship to you, and email.
//                   Describe in detail the the ideas and help they provided.
//
// Online sources:   avoid web searches to solve your problems, but if you do
//                   search, be sure to include Web URLs and description of
//                   of any information you find.
//////////////////////////// 80 columns wide ///////////////////////////////////

#include <signal.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

int main(int argc, char *argv[]){
	//check num of command line args
	if(argc != 3){
		printf("Usage: <signal type> <pid>\n");
		exit(0);
	}

	int id_no = atoi(argv[2]); //integer parsed from process id argument
	int kill_val; //checks return value of kill()	
	//send SIGUSR1
	if(strcmp(argv[1], "-u") == 0)
		kill_val = kill(id_no, 10);
	//send SIGINT
	if(strcmp(argv[1], "-i") == 0)
		kill_val = kill(id_no, 2);
	
	if(kill_val != 0){ //check return value of kill()
		printf("There was an error sending a signal to the process.\n");
		exit(0);
	}
	return 0;

}
