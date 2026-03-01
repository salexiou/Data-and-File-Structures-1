package implementation;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Random;

import structure.AccessCounter;
import structure.DataClass;
import structure.DataPage;
import structure.Pairs;

public class Tests {

	public static void main(String[] args) throws IOException {
		
		int[] data_size = {27,55};
		
		int[] records = {50,100,200,500,800,1000,2000,5000,10000,50000,100000,200000};
		
		RandomAccessFile file1 = new RandomAccessFile("A)Unsorted","rw");
		RandomAccessFile file2 = new RandomAccessFile("B)UnsortedIndexed","rw");
		RandomAccessFile file3 = new RandomAccessFile("C)SortedIndexed","rw");
		
		System.out.println("\n|==========================================================================================================================|");
		System.out.println("|===================================== ALEXIOU STAMATIOS : 2020030158 =====================================================|");
		System.out.println("|==========================================================================================================================|\n\n\n\n\n\n");
		
		for(int i = 0; i < records.length; i++) {
			for(int j = 0; j < data_size.length; j++) {
				System.out.println("============================================================================================================================");
				System.out.println("================================================== DATA_SIZE = " +data_size[j]+" ==========================================================");
				System.out.println("============================================================================================================================" +"\n");
				saveReadAndCalculate(records[i], data_size[j],file1,file2,file3);
			}
		}
		
		file1.close();                  // Do not forget to close
		file2.close();	 				//       all files!!
		file3.close();
		
	}
	



	public static void saveReadAndCalculate(int records ,int data_size,RandomAccessFile file1,RandomAccessFile file2,RandomAccessFile file3) throws IOException {

		
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//		 														      Initializations                                                                      //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		

		final int SIZE_KEYS = 1000;
		long ending1 ;
		long[] time = new long[SIZE_KEYS];
		int[] randomKeys;
		
		
		int[] keys = createRandomUniqueInts(records);
		DataPage dp = new DataPage(data_size,records);				
		DataClass[] instances = new DataClass[records];
		Pairs[] pairs = new Pairs[records];
		
		
		for(int i = 0; i < records ; i++) {
			instances[i] = new DataClass(keys[i],createRandomString(data_size));
			pairs[i] = new Pairs(keys[i],dp.getIndex());
			dp.randomWriteOnDisk(file1, instances[i].convertToBytes()); // write DataClasses on Disk
			dp.indexedWriteOnDisk(file2, pairs[i].convertToBytes()); // write Pairs on Disk
		}
		
		if(records<1000)
			 randomKeys = createRandomInts(records,SIZE_KEYS);
		else
			 randomKeys = createRandomUniqueInts(records,SIZE_KEYS);
		
		
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//																  Results For (A)                                                                         //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	
		for(int i = 0; i < SIZE_KEYS ; i++) {
			long starting1 = System.nanoTime();
				
				if(randomKeys[i] == randomSearchKey(dp, randomKeys[i],file1,i)) {
					ending1 = System.nanoTime();
					time[i] = ending1-starting1;
				}
				else {
					ending1 = System.nanoTime();
					time[i] = ending1-starting1;
				}
		}
		
		printStats(time,records,"A");
		
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//																  Results For (B)                                                                         //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		for(int i = 0; i < SIZE_KEYS ; i++) {
			long starting1 = System.nanoTime();
				
				if(randomKeys[i] == indexedUnsortedSearchKey(dp, randomKeys[i],file1, file2,i)) {
					ending1 = System.nanoTime();
					time[i] = ending1-starting1;
				}
				else {
					ending1 = System.nanoTime();
					time[i] = ending1-starting1;
				}
		}
		
		printStats(time,records,"B");
	
		
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//																Results For (C)                                                                           //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
		DataPage dp1 = new DataPage(data_size,records);		// create new DataPage (writing sorted Pairs)		

		Arrays.sort(pairs); //sorting the array of Pairs
	    
		for (int i = 0; i < records; i++) {
		    dp1.indexedWriteOnDisk(file3, pairs[i].convertToBytes());
		}
		for(int i = 0; i < SIZE_KEYS ; i++) {
			long starting1 = System.nanoTime();
				
				if(randomKeys[i] == binarySearch(dp, 0, dp1.calculateMaxPagesForIndexedFile(records), randomKeys[i], i ,file1, file2)) {
					ending1 = System.nanoTime();
					time[i] = ending1-starting1;
				}
				else {
					ending1 = System.nanoTime();
					time[i] = ending1-starting1;
				}
		}
		
		printStats(time,records,"C");
			
	}
	

	
	
	
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//																  Searching For (A)                                                                       //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public static int randomSearchKey(DataPage dp,int key ,RandomAccessFile file,int count) throws IOException {
				
		for(int j = 0; j < dp.getIndex(); j++) {  //searching the whole file page by page to find the given key
			AccessCounter.increaseCounter(count);
			if (key == dp.randomReadFromDisk(key,DataClass.convertToInstance(file, j,dp))) 
				return key;
			}
			return -1;						
	}
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//															       Searching For (B)                                                                      //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	

	public static int indexedUnsortedSearchKey(DataPage dp,int key ,RandomAccessFile file1,RandomAccessFile file2,int count) throws IOException {
		
		for(int j = 0; j < dp.getPair_index(); j++) {  //searching the whole file page by page to find the given key
			
			AccessCounter.increaseCounter(count);
			int[] func = dp.indexedUnsortedReadFromDisk(key,Pairs.convertToInstance(file2, j,dp));
			
			if(func == null)
				continue;
			
			if (key == func[0]) {
				AccessCounter.increaseCounter(count);
				int page = func[1]; // get the page of the found key on the initial file 
				int final_key = dp.randomReadFromDisk(key,DataClass.convertToInstance(file1, page,dp));
				return final_key;
			}
		}
			return -1;						
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//															Binary Searching For (Ã)                                                                      //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	 public static int binarySearch(DataPage dp,int leftIndex, int rightIndex, int key, int count, RandomAccessFile file1, RandomAccessFile file2) throws IOException {
		 	
	        if (rightIndex >= leftIndex) {
	        	
	        	AccessCounter.increaseCounter(count);
	        					
	            int mid = leftIndex + (rightIndex - leftIndex) / 2; 
	            
				int func = dp.indexedSortedReadFromDisk(key,Pairs.convertToInstance(file2,mid,dp));
				
	            // If the element is present at the middle itself 
	            if (func == 1) {
	            	AccessCounter.increaseCounter(count);
					int final_key = dp.randomReadFromDisk(key,DataClass.convertToInstance(file1, mid ,dp));
					return final_key;
	            }
	            // If element is smaller than mid, then it can only be present in left subarray 
	            else if (func == 2) 
	                return binarySearch(dp,leftIndex, mid - 1, key,count,file1,file2); 
	            // Else If the element can only be present in right subarray
	            else if(func == 3)
	            	return binarySearch(dp,mid + 1, rightIndex, key,count,file1,file2);
	            else
	            	return Integer.MIN_VALUE;
	        } 
	  
	        // We reach here when element is not present in array. 
	        // We return Integer.MIN_VALUE in this case, so the data array can not contain this value!
	        return Integer.MIN_VALUE; 
	    }

	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//												   Useful Functions for calculations and generations                                           	          //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public static void printStats(long time[],int records,String opt) {
		
		long averageTime1 = averageTimeSpentOnSearching(time);
		System.out.println("===================================================");
		System.out.println("||          ("+opt+")  For " + records +" records: 	  	  ||");
		System.out.println("===================================================");
		System.out.println("|| Average time spent:  |" + "| Average disk accesses: ||"+"\n||\t" 
							+averageTime1 + "ns	||	    "+averageAccessOnSearching()+ "\t  ||");
		System.out.println("===================================================");
		System.out.println("\n");
		
		for(int i = 0 ; i < AccessCounter.getCounters().length; i++) {   //
			AccessCounter.resetCounter(i);								 //
		}																 //
																		 //    Reset timer and access counter again for the next measurements 
		for(int i = 0 ; i < time.length; i++) {						     //
			time[i] = 0;											     //
		}	
	}
	
	public static long averageTimeSpentOnSearching(long[] time) {
		
		long x = 0;
		
		for(int i = 0 ; i < time.length ; i++) {
			
			x += time[i];
		}
		return (x/time.length);
	}
	
	
	public static String averageAccessOnSearching() {
		
		long x = 0;
		
		for(int i = 0 ; i < AccessCounter.getCounters().length; i++) {
			x += AccessCounter.getCount(i);
		}
		float y = ((float)x)/AccessCounter.getCounters().length;

		return String.format("%.1f",y);
	}
	
	
	public static int[] createRandomUniqueInts(int N) {
	
		Random randomGenerator = new Random();
		return randomGenerator.ints(1,2*N+1).distinct().limit(N).toArray();
	
		}
	
	public static int[] createRandomUniqueInts(int records,int N) {
		
		Random randomGenerator = new Random();
		return randomGenerator.ints(1,2*records).distinct().limit(N).toArray();
	
		}


	public static String createRandomString(int N) {
	
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
		         					+ "0123456789"
		         					+ "abcdefghijklmnopqrstuvxyz";
		
		StringBuilder sb = new StringBuilder(N);
			
		for (int i = 0; i < N; i++) {
			
			int index= (int)(AlphaNumericString.length()* Math.random());
			
			sb.append(AlphaNumericString.charAt(index));
		}
			
	return sb.toString();
	}
	
	
	public static int[] createRandomInts(int records,int N) {
		java.util.Random randomGenerator = new java.util.Random();
		return randomGenerator.ints(1,2*records).limit(N).toArray();
	}
	
}

