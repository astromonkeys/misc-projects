////////////////////////////////////////////////////////////////////////////////
// Main File:        csim.c, our p4B cache simulator
// This File:        csim.c
// Other Files:      test-csim, csim-ref, csim, /traces
// Semester:         CS 354 Fall 2020
// Instructor:       deppeler
// 
// Discussion Group: DISC 641 
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
//
////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2013,2019-2020, Jim Skrentny, (skrentny@cs.wisc.edu)
// Posting or sharing this file is prohibited, including any changes/additions.
// Used by permission, CS354-Fall 2020, Deb Deppeler (deppeler@cs.wisc.edu) 
//
////////////////////////////////////////////////////////////////////////////////

/**
 * csim.c:  
 * A cache simulator that can replay traces (from Valgrind) and 
 * output statistics for the number of hits, misses, and evictions.
 * The replacement policy is LRU.
 *
 * Implementation and assumptions:
 *  1. Each load/store can cause at most 1 cache miss plus a possible eviction.
 *  2. Instruction loads (I) are ignored.
 *  3. Data modify (M) is treated as a load followed by a store to the same
 *  address. Hence, an M operation can result in two cache hits, or a miss and a
 *  hit plus a possible eviction.
 */  

#include <getopt.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <assert.h>
#include <math.h>
#include <limits.h>
#include <string.h>
#include <errno.h>
#include <stdbool.h>

/*****************************************************************************/
/* DO NOT MODIFY THESE VARIABLES *********************************************/

//Globals set by command line args.
int b = 0;  //number of block (b) bits
int s = 0;  //number of set (s) bits
int E = 0;  //number of lines per set

//Globals derived from command line args.
int B;  //block size in bytes: B = 2^b
int S;  //number of sets: S = 2^s

//Global counters to track cache statistics in access_data().
int hit_cnt = 0; 
int miss_cnt = 0; 
int evict_cnt = 0; 

//Global to control trace output
int verbosity = 0;  //print trace if set
/*****************************************************************************/
  
  
//Type mem_addr_t: Use when dealing with addresses or address masks.
typedef unsigned long long int mem_addr_t; 

//Type cache_line_t: Use when dealing with cache lines.
typedef struct cache_line {                    
    char valid; // 'y' denotes valid, 'n' denotes invalid
    mem_addr_t tag; 
    //Add a data member as needed by your implementation for LRU tracking.
    //access to a line will set its counter to one greater than the current maximum value
    //of all the counters in its set. In the event of an eviction, the line with the
    //smallest counter value is the least recently used and is evicted
    int counter;
} cache_line_t; 

//Type cache_set_t: Use when dealing with cache sets
//Note: Each set is a pointer to a heap array of one or more cache lines.
typedef cache_line_t* cache_set_t; 
//Type cache_t: Use when dealing with the cache.
//Note: A cache is a pointer to a heap array of one or more sets.
typedef cache_set_t* cache_t; 

// Create the cache (i.e., pointer var) we're simulating.
cache_t cache;   

/**
 * init_cache:
 * Allocates the data structure for a cache with S sets and E lines per set.
 * Initializes all valid bits and tags with 0s.
 */                    
void init_cache() {
	//initialize S and B using args
	B = 2;
	S = 2;
	for(int i = 1; i < b; i++){
		B *= 2;
	}
	for(int i = 1; i < s; i++){
		S *= 2;
	}
	// allocate cache as an array of sets, with S being the number of sets	
	cache = malloc(sizeof(cache_set_t) * S);
	if(cache == NULL)
		exit(1);
	// allocate each set as an array of lines, with E being the number of lines
	for(int i = 0; i < S; i++){
		cache[i] = malloc(sizeof(cache_line_t) * E);
		if(cache[i] == NULL)
			exit(1);
		// initialize valid bits and tags
		for(int j = 0; j < E; j++){
			cache[i][j].valid = 'n';
			cache[i][j].tag = 0;
			cache[i][j].counter = 0;
		}
	}
}
  

/**
 * free_cache:
 * Frees all heap allocated memory used by the cache.
 */                    
void free_cache() {
	//free each set in the cache
	for(int i = S - 1; i >= 0; i--) {
		free(cache[i]);
		cache[i] = NULL;
	}
	//free the cache itself
	free(cache);
	cache = NULL;	
}
   
   
/** 
 * access_data:
 * Simulates data access at given "addr" memory address in the cache.
 *
 * If already in cache, increment hit_cnt
 * If not in cache, cache it (set tag), increment miss_cnt
 * If a line is evicted, increment evict_cnt
 */                    
void access_data(mem_addr_t addr) {
  	//cycle through all lines in set, in order
  	//to find a line whose tag field matches addr parameter
  	//if a match is found, increment hit_cnt and update that line's counter variable
	bool match_found = false; //boolean to tell if a hit was found
	int tag_number = addr >> (s + b); //tag number derived from addr to be searched for
	int t = 64 - s - b; //number of t bits
	int set_index = (addr << t) >> (t + b); //index of the set addr will be in, if it exists in the cache
	// current line is cache[set_index][i]
	for(int i = 0; i < E; i++){

		//if address tag bits match the tag of the current line being looked at
		 if((tag_number == cache[set_index][i].tag) && (cache[set_index][i].valid == 'y')){
                        //tag match is found and it's valid
                        hit_cnt++;
                        int max_counter = 0; //represents highest counter variable in the set
                        //cycle through the lines in this set to find the highest counter value, store it in max_counter
			for(int j = 0; j < E; j++){
                                int curr_counter = cache[set_index][j].counter;
                                if(curr_counter > max_counter)
                                        max_counter = curr_counter;
                        }
                        //then set this line's counter to max_counter++;
                        cache[set_index][i].counter = (max_counter + 1);
                        match_found = true;
			break;
                }
	} 
  	//if a tag match was not found, add addr to the cache, increment miss_cnt and evict if necessary using
  	//least recently used replacement policy, in which case increment evict_cnt as well
  	if((match_found == false)) {
		miss_cnt++;
		//cycle through all lines in set, in order
		//to find a place to put the new line(update tag)
		int LRU_index = 0; // index of line to be evicted
		int min_counter; // counter value of LRU line
		for(int i = 0; i < E; i++){
			//pointer to current cache line we're working with
	                	
			//if current line's counter is zero, evict it(it's treated as a free line) regardless of how many lines we still have to search
			if(cache[set_index][i].counter == 0){
				//eviction not necessary, we found an empty line
				cache[set_index][i].tag = tag_number;
				cache[set_index][i].valid = 'y';
				cache[set_index][i].counter = 1;
				break;
			}
			//else we found the new smallest counter variable and its respective index
			else if(cache[set_index][i].counter < min_counter){
				min_counter = cache[set_index][i].counter;
				LRU_index = i;
			}
			if(i == (E - 1)){				
				//we have reached the end of our set, gotta evict someone, employ LRU replacement policy
				cache[set_index][LRU_index - i].tag = tag_number;
			        cache[set_index][LRU_index - i].valid = 'y';
				cache[set_index][LRU_index - i].counter = 1;
				evict_cnt++;	
			}	
  		}
	}
}  

/**
 * replay_trace:
 * Replays the given trace file against the cache.
 *
 * Reads the input trace file line by line.
 * Extracts the type of each memory access : L/S/M
 * TRANSLATE each "L" as a load i.e. 1 memory access
 * TRANSLATE each "S" as a store i.e. 1 memory access
 * TRANSLATE each "M" as a load followed by a store i.e. 2 memory accesses 
 */                    
void replay_trace(char* trace_fn) {           
    char buf[1000];   
    mem_addr_t addr = 0; 
    unsigned int len = 0; 
    FILE* trace_fp = fopen(trace_fn, "r");  

    if (!trace_fp) { 
        fprintf(stderr, "%s: %s\n", trace_fn, strerror(errno)); 
        exit(1);    
    }

    while (fgets(buf, 1000, trace_fp) != NULL) {
        if (buf[1] == 'S' || buf[1] == 'L' || buf[1] == 'M') {
            sscanf(buf+3, "%llx,%u", &addr, &len); 
      
            if (verbosity)
                printf("%c %llx,%u ", buf[1], addr, len); 

            // GIVEN: 1. addr has the address to be accessed
            //        2. buf[1] has type of acccess(S/L/M)
            // call access_data function here depending on type of access
	    access_data(addr);
	    if(buf[1] == 'M')
		    access_data(addr);

            if (verbosity)
                printf("\n"); 
        }
    }

    fclose(trace_fp); 
}  
  
  
/**
 * print_usage:
 * Print information on how to use csim to standard output.
 */                    
void print_usage(char* argv[]) {                 
    printf("Usage: %s [-hv] -s <num> -E <num> -b <num> -t <file>\n", argv[0]); 
    printf("Options:\n"); 
    printf("  -h         Print this help message.\n"); 
    printf("  -v         Optional verbose flag.\n"); 
    printf("  -s <num>   Number of s bits for set index.\n"); 
    printf("  -E <num>   Number of lines per set.\n"); 
    printf("  -b <num>   Number of b bits for block offsets.\n"); 
    printf("  -t <file>  Trace file.\n"); 
    printf("\nExamples:\n"); 
    printf("  linux>  %s -s 4 -E 1 -b 4 -t traces/yi.trace\n", argv[0]); 
    printf("  linux>  %s -v -s 8 -E 2 -b 4 -t traces/yi.trace\n", argv[0]); 
    exit(0); 
}  
  
  
/**
 * print_summary:
 * Prints a summary of the cache simulation statistics to a file.
 */                    
void print_summary(int hits, int misses, int evictions) {                
    printf("hits:%d misses:%d evictions:%d\n", hits, misses, evictions); 
    FILE* output_fp = fopen(".csim_results", "w"); 
    assert(output_fp); 
    fprintf(output_fp, "%d %d %d\n", hits, misses, evictions); 
    fclose(output_fp); 
}  
  
  
/**
 * main:
 * Main parses command line args, makes the cache, replays the memory accesses
 * free the cache and print the summary statistics.  
 */                    
int main(int argc, char* argv[]) {                      
    char* trace_file = NULL; 
    char c; 
    
    // Parse the command line arguments: -h, -v, -s, -E, -b, -t 
    while ((c = getopt(argc, argv, "s:E:b:t:vh")) != -1) {
        switch (c) {
            case 'b':
                b = atoi(optarg); 
                break; 
            case 'E':
                E = atoi(optarg); 
                break; 
            case 'h':
                print_usage(argv); 
                exit(0); 
            case 's':
                s = atoi(optarg); 
                break; 
            case 't':
                trace_file = optarg; 
                break; 
            case 'v':
                verbosity = 1; 
                break; 
            default:
                print_usage(argv); 
                exit(1); 
        }
    }

    //Make sure that all required command line args were specified.
    if (s == 0 || E == 0 || b == 0 || trace_file == NULL) {
        printf("%s: Missing required command line argument\n", argv[0]); 
        print_usage(argv); 
        exit(1); 
    }

    //Initialize cache.
    init_cache(); 

    //Replay the memory access trace.
    replay_trace(trace_file); 

    //Free memory allocated for cache.
    free_cache(); 

    //Print the statistics to a file.
    //DO NOT REMOVE: This function must be called for test_csim to work.
    print_summary(hit_cnt, miss_cnt, evict_cnt); 
    return 0;    
}  


// end csim.c
