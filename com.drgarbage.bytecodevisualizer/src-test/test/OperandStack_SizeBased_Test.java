package test;

/**
 * Test for size based analysis.
 * Use *m.class file to verify the stack.
 * @author Sergej Alekseev
 */
public class OperandStack_SizeBased_Test {
	
	void test(int a, int b){
		diff(a > b ? a: b, 2);
	}
	
	static void diff(int a, int b){
		try{
		int i = 0;
		i++;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return;
		
	}
}
