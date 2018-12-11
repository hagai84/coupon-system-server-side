package core.test;

/**
 * Tests the implementation of the coupon system and it's classes.
 * @author Ron
 *
 */
public class RestTest {
	
	public static void main(String[] args) {
		
		RestTestThread testThread1 = new RestGenerateThread();	
		RestTestThread testThread2 = new RestGenerateThread();	
		RestTestThread testThread3 = new RestGenerateThread();	
		RestTestThread testThread4 = new RestGenerateThread();	
		RestTestThread testThread5 = new RestGenerateThread();	
		RestTestThread testThread6 = new RestGenerateThread();				
		testThread1.start();	
//		testThread2.start();	
//		testThread3.start();	
//		testThread4.start();	
//		testThread5.start();	
//		testThread6.start();	
		try {
			testThread1.join();
			testThread2.join();
			testThread3.join();
			testThread4.join();
			testThread5.join();
			testThread6.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

