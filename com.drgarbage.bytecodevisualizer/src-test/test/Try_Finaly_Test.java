package test;

public class Try_Finaly_Test {

	static int m2() {
		int i = 0; 
		try {
			i++;
		} finally {
			i--; 
		}
		return i; 
	}
}
