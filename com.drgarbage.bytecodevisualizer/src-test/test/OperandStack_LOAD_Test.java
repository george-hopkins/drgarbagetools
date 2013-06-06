package test;

/**
 * Test for LOAD instructions.
 * @author Sergej Alekseev
 */
public class OperandStack_LOAD_Test {
	
	public int a(){
		int a = 1;
		return a;
	}

	public short b(){
		short a = 1;
		return a;
	}
	
	public int b2(){
		short a = 1;
		return a;
	}

	public long b3(){
		short a = 1;
		return a;
	}
	
	public boolean c(){
		boolean a = true;
		return a;
	}
	
	public char a0(){
		char b = 1;
		char a = 'a';
		b = 255;
		return a;
	}
	
}
