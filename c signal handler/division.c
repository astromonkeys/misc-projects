////////////////////////////////////////////////////////////////////////////////
// Main File:        division.c
// This File:        division.c
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
#include <stdlib.h>

//globals
int num_divisions; //count of number of divisions performed

//handler to handle a SIGFPE or divide by zero error
void handler_SIGFPE(){
	printf("Error: a division by 0 operation was attempted.\n");
	printf("Total number of divisions completed successfully: %d\n", num_divisions);
	exit(0);
}

//handler to handle SIGINt (ctrl + c)
void handler_SIGINT(){
	printf("Total number of divisions completed successfully: %d\n", num_divisions);
        exit(0);
}

int main(){
	//register signal handlers
	struct sigaction fpe;
	memset(&fpe, 0, sizeof(fpe));
	fpe.sa_handler = &handler_SIGFPE;
	if(sigaction(SIGFPE, &fpe, NULL) != 0)
		printf("Error binding SIGFPE handler\n");
	
	struct sigaction intr;
        memset(&intr, 0, sizeof(intr));
        intr.sa_handler = &handler_SIGINT;
        if(sigaction(SIGINT, &intr, NULL) != 0)
                printf("Error binding SIGINT handler\n");

	//enter infinite loop
	while(1){
		char str1[100], str2[100];
		printf("Enter first integer: ");
		int one = atoi(fgets(str1, 100, stdin)); //process first input integer
		printf("Enter second integer: ");
		int two = atoi(fgets(str2, 100, stdin)); //process second input integer
		//perform math operations
		int quotient = one / two;
		int remainder = one % two;
		printf("%d / %d is %d with a remainder of %d\n", one, two, quotient, remainder);
		num_divisions++;
	}
}
