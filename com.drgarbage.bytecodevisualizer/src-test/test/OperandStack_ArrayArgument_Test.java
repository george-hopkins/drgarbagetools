package test;

/**
 * Test for Array argument instructions.
 * @author Sergej Alekseev
 */
public class OperandStack_ArrayArgument_Test {
	
	public void a(){
		int[] a = {0, 1};
		short[] b = {0, 1};
		boolean[] c = {false, true};
		String[] d = {"H", "e"};
		
		b(a);
		b(b);
		b(c);
		b(d);
		
	}

	public void b(int[] a){
	}
	
	public void b(short[] a){
	}
	
	public void b(boolean[] a){
	}
	
	public void b(String[] a){
	}
	
}
