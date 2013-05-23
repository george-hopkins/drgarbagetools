/**
 * Copyright (c) 2008-2013, Dr. Garbage Community
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test;

/**
 * Tests for operand stack and operand stack view.
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class OperandStack_View_Test {
	
	void test(int a, int b){
		max( a > b ? a : b, a > b ? a : b);
	}
	
	static void test111(int a, int b){
		switch(a){
			case 0:
				max( a > b ? a : b, b);
			case 1:
				a++;
		}
	}
	
	static int max(int a, int b){
		return a > b ? a : b;
	}
	
	int max2(int a, int b){
		if(a > b)
			return a;
		else
			return b;
		
	}
	
	int max3(int a, int b){
		int c;
		if(a > b)
			c =  a;
		else
			c = b;
		
		return c;
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int i = 0;
		
		if(i > 0){
			if(args == null){
				i++;
				if(args == null){
					i--;
				}
			}
			i--;
			i++;
		}

		int a = 0;
		if(a < args.length){
			a++;
		}
		else{
			a--;
		}
		
		
		System.out.println(a);
	}

	int  test0(int a){
		if(a > 0)
		return a;
		return 0;
	}
	
	int  test(int a, int b, int c){
		int d;
		d = a + b + c;
		return d;
	}
	
	void test2(){
		int a = 0;
		int d = test(a, 1, 2);
		
		switch(d){
		case 0: a = 2;
		break;
		case 1: d = 10;
		case 2: d = 11;
			break;
		case 6: a--;
		default: a = 0;
		}
		
		a++;
	}
	
	void test3(){
		int a = 0;
		int d = test(a, 1, 2);
		
		switch(d){
		case 0: a = 2;
		break;
		case 1: d = 10;
			if(d > a){
				d++;
			}
		case 2: d = 11;
			break;
		case 6: a--;
		default: a = 0;
		}
		
		a++;
	}
	
	void test4(){
		int a = 0;
		int d = test(a, 1, 2);
		
		switch(d){
		case 10: a = 2;
		break;
		case 20: a = 2;
		break;
		case 1: d = 10;
		case 2: d = 11;
			break;
		case 6: a--;
		default: a = 0;
		}
		
		a++;
	}
	
	void test5(){
		int a = 0;
		int d = test(a, 1, 2);

		switch(d){
		case 10: a = 2;
		break;
		case 20: a = 2;
		break;
		case 1: d = 10;
		case 2: d = 11;
			switch(d){
				case 10: a = 2;
					break;
				case 20: a = 2;
					break;
				default: a = 0;
			}
			a++;
		break;
		case 6: a--;
		default: a = 0;
		}

		a++;
	}
	
	void test6(int a){

		switch(a){
		case 10: a = 2;
			break;
		case 2: a = 11;

			switch(a){
				case 10: a = 2;
					break;
				default: a = 0;
			}
			
			a++;

		}

		a++;
	}
	
	void test7(int a){

		switch(a){
		case 10: a = 2;
			break;
		case 2: a = 11;

			switch(a){
				case 10: a = 2;
					break;
				default: a = 0;
			}
	
		}
        
//		a++;
	}
	
	void test8(int a){

		switch(a){
		case 10: a = 2;
			break;
		case 2: a = 11;

			switch(a){
				case 0: a = 2;
					break;
				case 1: a = 2;
					if(a > 0){
						a++;
					}
					else{
						a--;
					}
					break;
				default: a = 0;
			}
	
		}
        
//		a++;
	}
	
	void test9(int a){

		switch(a){
		case 10: a = 2;
			break;
		case 2: a = 11;

					if(a > 0){
						a++;
						if(a > 0){
							a--;
						}
					}
					else{
						a--;
					}

	
		}
        
//		a++;
	}

	
	void test300(int a){
		for(int i = 0; i < a ; i++){
			a++;
		}
	}
	
	void test301(int a){
		for(int i = 0; i < a ; i++){
			a++;
			for(int j = 0; j < a ; j++){
				a++;
			}
		}
	}
	
	void test302(int a){
		while(a > 10){
			a++;
		}
	}
	
	void test303(int a){
		do{
			a++;
		}while(a > 10);
	}

	void test400(){
		do{
			new Object();
		}while(true);
	}
	
	void test500(){
		max2(1, 2);
		max2(1, 2);
	}
	
	void test501(int a){
		max2(a, 2);
		max2(1, 2);
	}
	
	void test502(int a){
		max2(a, 2);
		a++;
		max2(1, 2);
	}
	
}
