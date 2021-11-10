#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/wait.h>
#include <unistd.h>
#include <ctype.h>

#define MAX_COMMAND_LEN 100

char* path[100]; // supports 100 path locations, including /bin (probably won't ever hit this limit)
int next_path_index = 1;
int batch_mode = 0; // 0 for interactive mode, 1 for batch mode
char errormsg[30] = "An error has occurred\n";

void execute_command(char* args[], int arg_count, int loop_counter) {
	int redirect = 0; // 0 means no redirection, 1 means yes

	// check for redirection
	for(int j = 1; j <= arg_count; j++) {
		if(strcmp(args[j], ">") == 0) {
			if((j != (arg_count - 1)) | (redirect == 1)) { redirect = 0; write(STDERR_FILENO, errormsg, strlen(errormsg)); return; }
			else { redirect = 1; }
		}
	}

	// process built-in commands before forking to do other work -> need to return	
	if(strcmp(args[0], "exit") == 0) { 
		if(arg_count != 0) { 
			write(STDERR_FILENO, errormsg, strlen(errormsg));
			return;
		} // error, print statement is filler
        else exit(0);
	} else if(strcmp(args[0], "cd") == 0) {
        if(arg_count != 1) { 
			write(STDERR_FILENO, errormsg, strlen(errormsg));
			exit(0); 
		}
        else { chdir(args[1]); return; }
    } else if(strcmp(args[0], "path") == 0) { 
		for(int i = 0; i < next_path_index; i++) {
			path[i] = "";
		}
		next_path_index = 0;
		int i = 1;
        while(args[i] != NULL) {
            path[next_path_index] = args[i];
            i++; next_path_index++;
        }
		return;
    }

	int rc = fork();
	if(rc == 0) { // child
		char* args_to_use[100];
		if(redirect == 1) {
			freopen(args[arg_count], "w", stdout);
			freopen(args[arg_count], "w", stderr);
			// need to get subset of args before >
			for(int i = 0; i < arg_count - 1; i++) {
				args_to_use[i] = args[i];
			}
			args_to_use[arg_count - 1] = NULL;
		} else {
			for(int i = 0; i <= arg_count; i++) {
				args_to_use[i] = args[i];
			}
			args_to_use[arg_count + 1] = NULL;
		}
		
		for(int i = 0; i < arg_count; i++) if(args_to_use[i] != NULL && strcmp(args_to_use[i], "$loop") == 0) sprintf(args_to_use[i], "%d", loop_counter); 

		for(int i = 0; i < next_path_index; i++) {
			char* full_path = strdup(path[i]);
			strcat(full_path, "/");
			strcat(full_path, args[0]);
			if(access(full_path, X_OK) == 0) 
				execv(full_path, args_to_use); 
		}
		if(access(args[0], X_OK) == 0) // check for absolute path
			execv(args[0], args_to_use);
		else {
			write(STDERR_FILENO, errormsg, strlen(errormsg));
			exit(1);
		}
	} else if(rc > 0) { // parent, rc = child pid
		(void) wait(NULL);
		return;
	} else { // fork failure
		exit(1);
	}
}

char* trimString(char *str) {
	char *end;

    while(isspace((unsigned char)*str)) str++;

    if(*str == 0)
        return str;

    end = str + strlen(str) - 1;
    while(end > str && isspace((unsigned char)*end)) end--;

    end[1] = '\0';

    return str;
}

int main(int argc, char* argv[]) {
	
	path[0] = "/bin"; // init path

	if(argc == 1) { // interactive mode
		char* line = malloc(MAX_COMMAND_LEN * sizeof(char));
		while(1) { // exit loop once exit is called
			printf("wish> ");
			size_t line_length = MAX_COMMAND_LEN;
			getline(&line, &line_length, stdin);
			if(line != NULL && line[strlen(line) - 1] == '\n')
                line[strlen(line) - 1] = 0; // remove trailing newline
			line = trimString(line);
			if(strcmp(line, "") == 0) continue;
			// process input here instead of in execute_command	
    		char* command = strdup(line);
    		char* args[100]; // max 100 args, probably won't hit this limit ever
    		args[0] = strsep(&command, " ");

    		int i = 0;
    		while(args[i] != NULL) { // last arg should always be null terminated
        		i++;
        		char* current_arg = strsep(&command, " ");
        		if(current_arg != NULL && current_arg[strlen(current_arg) - 1] == '\n')
            		current_arg[strlen(current_arg) - 1] = 0; // remove trailing newline
        		// check for >
        		if(current_arg != NULL && strcmp(current_arg, ">") == 0) args[i] = current_arg;
        		else if(current_arg != NULL && strstr(current_arg, ">") != NULL) {
            		args[i] = strsep(&current_arg, ">");
            		args[i + 1] = ">";
            		args[i + 2] = current_arg;
            		i += 2;
        		} else args[i] = current_arg;
    		}
			int arg_count = i - 1; // does not include command itself, "ls -l a gjk" gives an arg_count of 3
			// process looping
            int num_times_to_loop;
            if(strcmp(args[0], "loop") == 0) {
                if(!(!(args[1] == NULL) && !(!isdigit(args[1][0])))) { write(STDERR_FILENO, errormsg, strlen(errormsg)); continue; }
                num_times_to_loop = atoi(args[1]);
            } else num_times_to_loop = 1;

            char* args_to_use[98];
            int use_alt_args = 0;
            if(num_times_to_loop != 1) {
                for(int i = 2; i <= arg_count; i++) {
                    args_to_use[i - 2] = args[i];
                }
                use_alt_args = 1;
                arg_count -= 2;
            }

            for(int i = 0; i < num_times_to_loop; i++){
                int count = i + 1;
                if(use_alt_args) execute_command(args_to_use, arg_count, count);
                else execute_command(args, arg_count, count);
            }
		}
	} else if (argc == 2) { // batch mode
		batch_mode = 1;
		char* filepath = strdup(argv[1]);
		FILE* batchfile = fopen(filepath, "r");
		if(batchfile == NULL) { write(STDERR_FILENO, errormsg, strlen(errormsg)); exit(1); }
		
		char *line = NULL; size_t len = 0;
		while (getline(&line, &len, batchfile) != -1) {
    		if(line != NULL && line[strlen(line) - 1] == '\n')
	            line[strlen(line) - 1] = 0; // remove trailing newline
			line = trimString(line);
			if(strcmp(line, "") == 0) continue;
			// process input here instead of in execute command
					// process input here instead of in execute_command
            char* command = strdup(line);
            char* args[100]; // max 100 args, probably won't hit this limit ever
            args[0] = strsep(&command, " ");

            int i = 0;
            while(args[i] != NULL) { // last arg should always be null terminated
                i++;
                char* current_arg = strsep(&command, " ");
                if(current_arg != NULL && current_arg[strlen(current_arg) - 1] == '\n')
                    current_arg[strlen(current_arg) - 1] = 0; // remove trailing newline
                // check for >
                if(current_arg != NULL && strcmp(current_arg, ">") == 0) args[i] = current_arg;
                else if(current_arg != NULL && strstr(current_arg, ">") != NULL) {
                    args[i] = strsep(&current_arg, ">");
                    args[i + 1] = ">";
                    args[i + 2] = current_arg;
                    i += 2;
                } else args[i] = current_arg;
            }
            int arg_count = i - 1; // does not include command itself, "ls -l a gjk" gives an arg_count of 3
			
			// process looping
			int num_times_to_loop;
			if(strcmp(args[0], "loop") == 0) {
				if(!(!(args[1] == NULL) && !(!isdigit(args[1][0])))) { write(STDERR_FILENO, errormsg, strlen(errormsg)); continue; }
				num_times_to_loop = atoi(args[1]);
			} else num_times_to_loop = 1;
		
			char* args_to_use[98];
			int use_alt_args = 0;
			if(num_times_to_loop != 1) {
				for(int i = 2; i <= arg_count; i++) {
                	args_to_use[i - 2] = args[i];
            	}
				use_alt_args = 1;
				arg_count -= 2;
			}	

			for(int i = 0; i < num_times_to_loop; i++){ 
				int count = i + 1;
				if(use_alt_args) execute_command(args_to_use, arg_count, count);
				else execute_command(args, arg_count, count); 
			}	
		}
	} else { // error
		write(STDERR_FILENO, errormsg, strlen(errormsg));
		exit(1); // exit failure
	}
}
