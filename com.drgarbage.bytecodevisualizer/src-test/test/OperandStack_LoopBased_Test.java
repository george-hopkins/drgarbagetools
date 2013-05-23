package test;

public class OperandStack_LoopBased_Test {

	public  void test(int a) {
		for(int i = 0; i < a; i++){
			max(i, a);
		}
	}
	
	public  void test1(int a) {
		int i = 0;
		while( i < a){
			max(i, a);
		}
	}
	
	public  void test2(int a) {
		int i = 0;
		do{
			max(i, a);
		}
		while( i < a);
	}
	
	
	public int max(int a, int b){
		return a > b ? a : b;
	}

}
