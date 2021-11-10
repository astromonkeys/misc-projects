#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <pthread.h>
#include <sys/sysinfo.h>

long int getFileSize(FILE* file) {
    fseek(file, 0L, SEEK_END);
    long int number = ftell(file);
    fseek(file, 0L, SEEK_SET);
    return number;
}

/*
 * Gets content of file and returns it as a char pointer
 */
void *getFileContent(void* arg)
{
	FILE* file = (FILE*) arg;
	long int numbytes = getFileSize(file);
    char* addString = malloc(numbytes * sizeof(char));
	int retval;
	int i = 0;
	while((retval = fgetc(file)) != EOF) {
		if(retval != '\0') {
			addString[i++] = (char) retval;
		}
	}
	//if((!addString) | (fread(addString, sizeof(char), numbytes, file) != numbytes))
        //exit(EXIT_FAILURE);
    fclose(file);
	return (void*) addString;
}

int main(int argc, char* argv[])
{
	
	if(argc < 2) { // check args
		printf("pzip: file1 [file2 ...]\n");
		exit(EXIT_FAILURE);
	}

	int numThreadsToCreate = 0;
	int divideSingleFile = 0;
	//if(argc == 2 && getFileSize(argv[1]) > 50000000) {
	//	divideSingleFile = 1;
	//	numThreadsToCreate = get_nprocs();
	//} else {
		for(int i = 1; i < argc; i++) {
			if(access(argv[i], F_OK) == 0)
				numThreadsToCreate++;
		}
	//}

	pthread_t threads[numThreadsToCreate];
	char* inputArr[numThreadsToCreate];

	// - have each thread process the input of one file -> store into index of an array
	// - once all the files are done, piece them together in order
	// - print string as normal

	// TODO if divide single file == 1, divide the file into get_nprocs smaller files before sending them off

	FILE* currentInputFile;
	if(!divideSingleFile) {
		int threadIndex = 0;
		for(int i = 0; threadIndex < numThreadsToCreate; i++) {
			if(access(argv[i + 1], F_OK) != 0) continue;
			currentInputFile = fopen(argv[i + 1], "r");
			if(!currentInputFile) continue;
			pthread_create(&threads[threadIndex], NULL, getFileContent, (void*) currentInputFile);
			threadIndex++;
		}
	} else {
		// TODO how to divide single file among threads
	}
	// need to wait for threads to complete before we continue
	for(int i = 0; i < numThreadsToCreate; i++) pthread_join(threads[i], (void**) &inputArr[i]);
	//  piece files together into one string
	int inputLength = 0;
	for(int i = 0; i < numThreadsToCreate; i++) inputLength += strlen(inputArr[i]);
	char* completeInput = malloc(inputLength * sizeof(char) + 1); // super long char array of our concatenated input
	char* dest = completeInput;
	for(int i = 0; i < numThreadsToCreate; i++) {
		char* src = inputArr[i];
		while(*src)
			*dest++ = *src++;
	}

	// process the input, character by character using run-length encoding
	// store the output in another string, then print it
	// chars and counts are parallel arrays
	
	char* chars = malloc(inputLength); // compressed chars in the order they appear
	int* counts = malloc(sizeof(int) * inputLength); // counts of chars
	for(int i = 0; i < inputLength; i++) counts[i] = 0; // initialize all to 0 so we know what gets filles
	char currentCharacter;
	int charIndex = 0;
	for(int i = 0; i < inputLength; i++, charIndex++) {
		currentCharacter = completeInput[i];
		int j = i + 1;
		int currentInstances = 1;
		while(completeInput[j] == currentCharacter) {
			currentInstances++;
			j++;
		}
		// store the characters and their counts in parallel arrays
		chars[charIndex] = currentCharacter;
		counts[charIndex] = currentInstances;

		i = j - 1; // go to the next different character --> -1 is to account for i++
	}
	counts[charIndex] = 0;
	int i = 0;
	while(counts[i] != 0) {
		fwrite(&counts[i], 4, 1, stdout);
		fwrite(&chars[i], 1, 1, stdout);
		i++;
	}
	// free stuff
	free(completeInput);
	free(chars);
	free(counts);
	return 0;
}
