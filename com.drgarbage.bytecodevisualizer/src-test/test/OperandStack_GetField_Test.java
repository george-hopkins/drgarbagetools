package test;

/**
 * Test for OPCODE_GETSTATIC and OPCODE_GETFIELD.
 * @author Sergej Alekseev
 */
public class OperandStack_GetField_Test {

	int a;
	byte b;
	char c;
	double d;
	float e;
	long f;
	String s;
	
	public void a(){
		a = 1;
		System.out.println(a);
	}

	public void b(){
		b = 2;
		System.out.println(b);
	}
	
	public void c(){
		c = '2';
		System.out.println(c);
	}
	
	public void d(){
		d = 20;
		System.out.println(d);
	}
	
	public void e(){
		e = 30.3F;
		System.out.println(e);
	}
	
	public void f(){
		f = 23;
		System.out.println(f);
	}
	
	public void s(){
		s = "Hello";
		System.out.println(s);
	}
}
