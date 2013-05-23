package test;

public class OperandStack_ContentBased_Test {

	public  void test(int a, int b) {
		max((a > b ? a : b), 
				(a < b ? a : b));
	}

	public  void test2(int a, int b) {
		max((a > b ? a : b), 
				(a < b ? a : b));
	}
	
	public int max(int a, int b){
		return a > b ? a : b;
	}

	public  void test2() {
		arg(1, '2', 3.0, true, 5L, 6F, null, "H",
				1, '2', 3.0, false, 5L, 6F, null, "H");
	}
	
	public int arg(
			int a, 
			char b,
			double c,
			boolean d,
			long e,
			float f,
			Object o,
			String s,
			int a1, 
			char a2,
			double c2,
			boolean d2,
			long e2,
			float f2,
			Object o2,
			String s2
			){
		return 0;
	}

}
