package test;

public class OperandStack_InvokeType_Test {

	void test(){
		test2(1, 2, 3, 4);
		test3(1, 2, 3, 4, "H");
	}

	static void _test1(){
		String s = "Hello";
		test1(s);
		test1("Hello World");
	}
	
	static void test1(String s){
	}
	
	void test2(int a, int b, double c, float d){
	}
	
	char test3(int a, int b, double c, float d, String s){
		return 10;
	}
	
	void test4(){
		test5(true);
		test6(false, 10);
	}
	
	void test5(boolean b){
	}
	
	void test6(boolean b, int i){
	}
	
	static void _test11(){
		boolean[] b = {true};
		test11(b);
	}
	
	static void test11(boolean[] b){
	}
	
	static void _test11a(){
		int[] b = {1};
		test11a(b);
	}
	
	static void test11a(int[] b){
	}
	
	static void _test11b(){
		short[] b = {1};
		test11b(b);
	}
	
	static void test11b(short[] b){
	}
	
	
	static void _test11c(){
		double[] b = {1};
		test11c(b);
	}
	
	static void test11c(double[] b){
	}
	
	static void _test11d(){
		long[] b = {1};
		test11d(b);
	}
	
	static void test11d(long[] b){
	}
	
	static void _test11e(){
		byte[] b = {1};
		test11e(b);
	}
	
	static void test11e(byte[] b){
	}
	
	static void _test11f(){
		String[] b = {"1"};
		test11f(b);
	}
	
	static  void test11f(String[] b){
	}
	
	static  void _test11g(){
		String[] b = {"1"};
		test11g(b);
	}
	
	static void test11g(Object[] b){
	}
}
