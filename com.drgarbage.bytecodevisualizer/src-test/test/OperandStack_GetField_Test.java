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
	
	/* arrays */

	int[] a1 = {1, 2}; 
	int[][] a2 = {{1, 2}, {3, 4}};
	
	byte[] b1 = {2, 3};
	byte[][] b2 = {{2, 3},{3, 4}};
	
	char[] c1 = {'a', 'b'};
	char[][] c2 = {{'a', 'b'}, {'c', 'e'}};
	
	double[] d1 = {1.2, 2.2};
	double[][] d2 = {{1.2, 2.2}, {2.6, 3.7}};
	
	float[] e1 = {1.3F, 2.4F};
	float[][] e2 = {{1.3F, 2.4F}, {2.3F, 3.4F}};
	
	long[] f1 = {4, 5};
	long[][] f2 = {{4, 5}, {6, 7}};
	
	String[] s1 = {"H", "E"};
	String[][] s2 = {{"H", "E"}, {"L", "L"}};

	
	public void a1(){
		System.out.println(a1);
		System.out.println(a1[0]);
		
		System.out.println(a2);
		System.out.println(a2[0]);
		System.out.println(a2[0][1]);
		
		_a1(a2);
		_a1(a2[0]);
		_a1(a2[0][1]);
	}
	
	public void _a1(int i){}
	public void _a1(int[] i){}
	public void _a1(int[][] i){}

	public void b1(){
		System.out.println(b1);
		System.out.println(b1[0]);
		
		System.out.println(b2);
		System.out.println(b2[0]);
		System.out.println(b2[0][1]);
		
		_b1(b2);
		_b1(b2[0]);
		_b1(b2[0][1]);
	}
	
	public void _b1(byte i){}
	public void _b1(byte[] i){}
	public void _b1(byte[][] i){}
	
	public void c1(){
		System.out.println(c1);
		System.out.println(c1[0]);
		
		System.out.println(c2);
		System.out.println(c2[0]);
		System.out.println(c2[0][1]);
		
		_c1(c2);
		_c1(c2[0]);
		_c1(c2[0][1]);
	}
	
	public void _c1(char i){}
	public void _c1(char[] i){}
	public void _c1(char[][] i){}
	
	public void d1(){
		System.out.println(d1);
		System.out.println(d1[0]);
		
		System.out.println(d2);
		System.out.println(d2[0]);
		System.out.println(d2[0][1]);
		
		_d1(d2);
		_d1(d2[0]);
		_d1(d2[0][1]);
	}

	public void _d1(double i){}
	public void _d1(double[] i){}
	public void _d1(double[][] i){}
	
	public void e1(){
		System.out.println(e1);
		System.out.println(e1[0]);
		
		System.out.println(e2);
		System.out.println(e2[0]);
		System.out.println(e2[0][1]);
		
		_e1(e2);
		_e1(e2[0]);
		_e1(e2[0][1]);
	}
	
	public void _e1(float i){}
	public void _e1(float[] i){}
	public void _e1(float[][] i){}
	
	public void f1(){
		System.out.println(f1);
		System.out.println(f1[0]);
		
		System.out.println(f2);
		System.out.println(f2[0]);
		System.out.println(f2[0][1]);
		
		_f1(f2);
		_f1(f2[0]);
		_f1(f2[0][1]);
	}
	
	public void _f1(long i){}
	public void _f1(long[] i){}
	public void _f1(long[][] i){}
	
	public void s1(){
		System.out.println(s1);
		System.out.println(s1[0]);
		
		System.out.println(s2);
		System.out.println(s2[0]);
		System.out.println(s2[0][1]);
		
		_s1(s2);
		_s1(s2[0]);
		_s1(s2[0][1]);
	}
	
	public void _s1(String i){}
	public void _s1(String[] i){}
	public void _s1(String[][] i){}
	
}
