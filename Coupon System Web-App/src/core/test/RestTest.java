package core.test;

/**
 * Tests the implementation of the coupon system and it's classes.
 * @author Ron
 *
 */
public class RestTest {
	
	public static void main(String[] args) {
		
		RestGenericThread testThread1 = new RestCreateThread();	
		RestGenericThread testThread2 = new RestCreateThread();	
		RestGenericThread testThread3 = new RestCreateThread();	
		RestGenericThread testThread4 = new RestCreateThread();	
		RestGenericThread testThread5 = new RestCreateThread();	
		RestGenericThread testThread6 = new RestCreateThread();				
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

