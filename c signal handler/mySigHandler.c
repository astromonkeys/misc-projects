////////////////////////////////////////////////////////////////////////////////
// Main File:        mySigHandler.c
// This File:        mySigHandler.c
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
#include <string.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <time.h>
#include <stdlib.h>

//globals
int tally = 0; //number of times SIGUSR1 has been received

/*
 * function to handle the alarm signal
 * this handler should print the process id of the program and current time
 * it should also re-arm the alarm to go off again 3 seconds later, then return back to main
 */
void handler_SIGALRM(){
	time_t now;
	//stores time value, to be used in printing ctime
	time(&now);
	printf("PID: %d CURRENT TIME: %s", (int)getpid(), ctime(&now));
        alarm(3);
}

//handler for USR1 signal
void handler_SIGUSR1(){
	//use tally to show SIGUSR1 was received
	tally++;
	printf("SIGUSR1 handled and counted!\n");
}

//handler for ctrl-c (SIGINT)
void handler_SIGINT(){
	printf("\nSIGINT handled.\n");
	printf("SIGUSR1 was handled %d times. Exiting now.\n", tally);
	exit(0);
}

int main(){
	printf("Pid and time print every 3 seconds.\n");
	printf("Enter Ctrl-C to end the program.\n");
	//set up alarm to go off in 3 seconds, causing a SIGALRM signal to be sent to the program
	alarm(3);
	//register a signal handler to handle the SIGALRM signal so that the signal can be received
	struct sigaction alrm;
	memset(&alrm, 0, sizeof(alrm));
	alrm.sa_handler = &handler_SIGALRM;
	if(sigaction(SIGALRM, &alrm, NULL) != 0){
		printf("Error binding SIGALRM handler\n");		
        }
	//register signal handler for SIGUSR1
	struct sigaction usr1;
	memset(&usr1, 0, sizeof(usr1));
	usr1.sa_handler = &handler_SIGUSR1;
	if(sigaction(SIGUSR1, &usr1, NULL) != 0){
                printf("Error binding SIGUSR1 handler\n");
        }
	//register signal handler for SIGINT
        struct sigaction sint;
        memset(&sint, 0, sizeof(sint));
        sint.sa_handler = &handler_SIGINT;
        if(sigaction(SIGINT, &sint, NULL) != 0){
                printf("Error binding SIGINT handler\n");
        }
	//enter infinite loop
	while(1){
	}
}
