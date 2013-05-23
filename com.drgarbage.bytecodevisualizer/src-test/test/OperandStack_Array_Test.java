package test;

/**
 * Test for load array elements.
 * @author Sergej Alekseev
 */
public class OperandStack_Array_Test {
	int a[] = {0, 1};
	short b[] = {0, 1};
	byte c[] = {0, 1};
	float d[] = {0, 1};
	long e[] = {0, 1};
	double f[] = {0, 1};
	static char g[] = {0, 1};
	Object h[] = {0, 1};
	
	int test(){
		return a[1];
	}
	
	short test2(){
		return b[1];
	}
	
	byte test3(){
		return c[1];
	}
	
	byte test3a(){
		byte ca[] = {0, 1};
		return ca[1];
	}
	
	byte test3b(){
		byte b = 0;
		for(int i = 0; i < 3; i++){
			b = c[i];
		}
		return b;
	}
	
	float test4(){
		return d[1];
	}
	
	long test5(){
		return e[1];
	}
	
	double test6(){
		return f[1];
	}
	
	char test7(){
		return g[23];
	}
	
	Object test8(){
		return h[10];
	}
}
