package test;

/**
 * Test for type based analysis.
 * Use *m.class file to verify the stack.
 * @author Sergej Alekseev
 */
public class OperandSatck_TypeBased_Test {
	
	int test(int a, int b){
		return diff(a > b ? a: b, a < b ? a: b);
	}
	
	int diff(int a, int b){
		if( a > b){
			return a;
		}
		return b;
	}
	
	public static void main(String argv[]){
		OperandSatck_TypeBased_Test c5 = new OperandSatck_TypeBased_Test();
		int a = c5.test(2, 3);
		System.out.println(a);
	}
}
