package test;

public class OperandStack_InvokeType_Test {

	void test(){
		test2(1, 2, 3, 4);
		test3(1, 2, 3, 4, "H");
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
}
