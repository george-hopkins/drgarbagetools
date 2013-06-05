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
	
	static void loop1(){
		loops:
			for (int i = 0; i < 10; i++) {
			    for (int j = 0; j < 20; j++) {
			    	if(i > j)
			    		break loops;
			    }
			}
	}

	
	static void loop2(){
	      int a = 0;
	        out: // label
	        for (int i = 0; i < 3; ++i) {
	            System.out.println("I am here");
	            for (int j = 0; j < 20; ++j) {
	                if(a==0) {
	                    System.out.println("j: " + j);
	                    if (j == 1) {
	                        a = j;
	                        continue out; // goto label "out"
	                    }
	                }
	            }
	        }
	        System.out.println("a = " + a);
	    }

}
