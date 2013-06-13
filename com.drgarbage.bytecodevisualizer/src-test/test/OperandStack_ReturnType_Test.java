package test;

public class OperandStack_ReturnType_Test {

	byte test1(){
		return 10;
	}

	byte test1m(){
		return 10;
	}
	
	char test2(){
		return 10;
	}
	
	char test2m(){
		return 10;
	}
	
	int test3(){
		return 10;
	}
	
	int test3m(){
		return 10;
	}
	
	long test4(){
		return 10;
	}
	
	long test4m(){
		return 10;
	}
	
	double test5(){
		return 10;
	}
	
	double test5m(){
		return 10;
	}
	
	float test6(){
		return 10;
	}
	
	float test6m(){
		return 10;
	}
	
	Object test7(){
		return "10";
	}
	
	Object test7m(){
		return "10";
	}
	
	String test8(){
		return "10";
	}
	
	String test8m(){
		return "10";
	}
	
	static void test10(){
		String[]  s = test10AS();
		int[]  i = test10AI();
		long[]  l = test10AL();
		boolean[]  b = test10AB();
	}
	
	static String[] test10AS(){
		return null;
	}
	
	static int[] test10AI(){
		return null;
	}
	
	static long[] test10AL(){
		return null;
	}
	
	static boolean[] test10AB(){
		return null;
	}
}
