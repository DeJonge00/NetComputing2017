#include <stdlib.h>
#include <stdio.h>
#include <string.h>

int main(int argc, char *argv[]) {
	printf("aaaaaaaaaaaaaaaaaaaaaaaaa\n");
	if(argc>1) {
		fprintf(stdout, "Hello %s!\n", argv[1]);
		if(argc>2) {
			fprintf(stderr, "%s asdf", argv[2]);
			if(strcmp(argv[2],"inf") == 0) {
				while(1) {
					int i = 1 + 1;
				}
			}
		} else {
			fprintf(stdout, "goodbye\n");
		}	
	} else {
		fprintf(stderr, "Wrong input format was given\n");
		fprintf(stdout, "Usage: infinite arg1\n  will print 'hello 					arg1'\n       infinite arg1 inf\n will start an infinite loop consuming 100 percent of one core. It can be interrupted from the interface'");
		exit(-1);
	}
	return 0;
}
