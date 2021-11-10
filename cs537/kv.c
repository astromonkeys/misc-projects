#include <stdio.h>
#include <stddef.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <ctype.h>

#define DATABASE_PATH "database_kv.txt"

/* element of a singly linked list */
struct list_element {
	int key;
	char* value;
	struct list_element* next;
};

int main(int argc, char *argv[])
{
	/* if there are no arguments, do nothing */
	if(argc == 1) exit(EXIT_SUCCESS);
	
	/* load database into memory using a doubly linked list*/
	FILE *file_ptr;
	if(access(DATABASE_PATH, F_OK ) == 0 ) {
    	// file exists
		file_ptr = fopen(DATABASE_PATH, "r+");
	} else {
    	// file doesn't exist
		file_ptr = fopen(DATABASE_PATH, "w");
	}
	if(file_ptr == NULL) {
		printf("%s\n", "Error opening file");
		exit(EXIT_FAILURE);
	}

	struct list_element* head = NULL;
	struct list_element* current_tail = NULL;
	char* line = NULL; size_t len = 0; ssize_t read;
	while ((read = getline(&line, &len, file_ptr)) != -1) {
		/* parse data from line */
		char* string = strdup(line);
		int key = atoi(strsep(&string, ","));
		char* value = strsep(&string, "\n");
		
		if(head == NULL) {
			head = malloc(sizeof(struct list_element));
			head->key = key; head->value = value; head->next = NULL;
			current_tail = head;
		} else {
			struct list_element* element = malloc(sizeof(struct list_element));
			element->key = key; element->value = value;	current_tail->next = element;
			current_tail = element;
		}
	}
	
	struct list_element* current = head;
	char *string; int key, success, fail;
	/* process commands */ 
	for(int i = 1; i < argc; i++) {
		int string_length = strlen(argv[i]);
		switch(argv[i][0]) {
			case 'p':; // put a new key 
				if(string_length < 5) { printf("%s", "bad command\n"); break; }

				string = strdup(argv[i]);
                strsep(&string, ","); // get rid of input command
                char *keystring = strsep(&string, ",");
                fail = 0;
                for(int j = 0; j < strlen(keystring); j++) {
                    if(isdigit(keystring[j]) == 0) fail = 1;
                }
                if(fail == 1) { printf("%s", "bad command\n"); break; }

				key = atoi(keystring);
                char* value = strsep(&string, ",");
				strsep(&string, ","); // should be NULL
                if(string != NULL) { printf("%s", "bad command\n"); break; }
	
				/* check for duplicate keys, update the value if there is */
				int updated = 0;
				current = head;
				while(current != NULL) { 
					if(current->key == key) { 
						current->value = value;
						updated = 1;
						}
					current = current->next;
				}
				
				if(updated == 1) break;

				if(head == NULL) {
					head = malloc(sizeof(struct list_element));
					head->key = key; head->value = value; head->next = NULL;
					current_tail = head;
				} else {
					struct list_element* new = malloc(sizeof(struct list_element));
					new->key = key;	new->value = value;	new->next = NULL;
					current_tail->next = new;
					current_tail = new;
				}
				break;
			case 'g':
				// get a key, cycling through the list checking keys
				if(string_length < 3) { printf("%s", "bad command\n"); break; }

				string = strdup(argv[i]);
				strsep(&string, ",");
				key = atoi(string);

				strsep(&string, ","); // should be NULL
                if(string != NULL) { printf("%s", "bad command\n"); break; }
				success = 0;
                current = head;
				while(current != NULL) {
					if(current->key == key) {
						printf("%i,%s\n", current->key, current->value);
						success = 1;
					}
					if(success == 1) break;
					current = current->next;
				}
                if(success == 0) printf("%i not found\n", key);
				break;
			case 'd':
				// delete a key, updating pointers
				if(string_length < 3) { printf("%s", "bad command\n"); break; }

				string = strdup(argv[i]);
				strsep(&string, ",");
				key = atoi(string);

				strsep(&string, ","); // should be NULL
				if(string != NULL) { printf("%s", "bad command\n"); break; }
				success = 0;
				if(head->key == key) { /* if key is first element */
					struct list_element* oldhead = head;
					head = head->next;
					free(oldhead);
					success = 1;
				} else { /* key is not the head */
					struct list_element* previous = head;
					current = head->next;
					while(current != NULL) {
						if(current->key == key) {
							previous->next = current->next;
							free(current);
							success = 1;
						}
						previous = current;
						current = current->next;
					}
				}
				if(success == 0) printf("%i not found\n", key);  
				break;
			case 'c':
				/* delete/free the list from memory then clear the file */
    			if(string_length > 1) { printf("%s", "bad command\n"); break; }
				
				current = head;
    			struct list_element *next;
    			while(current != NULL) {
        			next = current->next;
        			free(current);
        			current = next;
    			}
				head = NULL;
				break;
			case 'a':; // print all keys (formatted)
				if(string_length > 1) { printf("%s", "bad command\n"); break; }

				current = head;
				while(current != NULL) {
					printf("%i,%s\n", current->key, current->value);
					current = current->next;
				}
				break;
			default:
				printf("%s", "bad command\n");
		}
	}

	/* write to persistence */
	current = head; 
	fclose(file_ptr);
	fclose(fopen(DATABASE_PATH, "w"));
	file_ptr = fopen(DATABASE_PATH, "r+");
	if(file_ptr == NULL) {
        printf("%s\n", "Error opening file");
        exit(EXIT_FAILURE);
    }
	while(current != NULL) {
		fprintf(file_ptr, "%i,%s\n", current->key, current->value);
		current = current->next;
    }
		
	/* delete/free the list from memory after writing it to a file */
	if(head != NULL) {
		current = head;
		struct list_element *next;
		while(current != NULL) {
			next = current->next;
			free(current);
			current = next;
		}
	}
	return 0;
}
