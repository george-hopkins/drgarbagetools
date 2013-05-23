package test;

/*
 *      Num of IF .. Num of Stack Entries
 *              2 .. 24
 *              4 .. 64
 *              8 .. 198
 *             16 .. 682
 *             32 .. 2514
 *             64 .. 9634
 *            128 .. 37698
 *            256 .. 149122
 *            512 .. ?????? 
 */


public class ExponentialTest {

	boolean test2(boolean a, boolean b){
		return test2(a,b||a);
	}
	boolean test4(boolean a, boolean b){
		return test2(a,b||test2(a,b||a));
	}
	boolean test8(boolean a, boolean b){
		return test2(a,b||test2(a,b||test2(a,b||test2(a,b||a))));
	}
	boolean test16(boolean a, boolean b){
		return test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||a))))))));
	}
	boolean test32(boolean a, boolean b){
		return test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||a))))))))))))))));
	}
	boolean test64(boolean a,boolean b){
		return test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||a))))))))))))))))))))))))))))))));
	}
	boolean test128(boolean a,boolean b){
		return test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||a))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))));
	}
	// boolean test256(boolean a,boolean b){
// 		return test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||a))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))));
// 	}
	// boolean test512(boolean a,boolean b){
	// 	return test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||test2(a,b||a))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))));
	// }
}
