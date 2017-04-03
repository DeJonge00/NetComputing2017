#include <stdio.h>
#include <stdlib.h>

int main(int argc, char *argv[]) {
	if(argc != 2) {
		fprintf(stdout, "usage: ./multiplyInput <int>");
		return -1;
	}

	int num = atoi(argv[1]);
	int x, i = 0;
	while(i < 10) {
		i++;
		fscanf(stdin, "%d", &x);
		fprintf(stdout, "%d\n", x*num);
	}
	return 0;
}
