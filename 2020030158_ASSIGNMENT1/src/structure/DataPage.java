package structure;

import java.io.IOException;
import java.io.RandomAccessFile;

public class DataPage {
	
	static final int pageCapacity = 256;// the capacity of a page
	java.nio.ByteBuffer WriteDataPage; //buffer of the page
	final byte[] zeroPageBytes = new byte[pageCapacity]; //  in order to clean our Page buffer
	java.nio.ByteBuffer WriteDataPageForPairs; //buffer of the page for storing pairs
	
	int dataSize; // bytes of the data we want to save. Either 27 or 55
	int records; //total records to save

	int index; // index of the current page;
	int numberOfRecordsPerPage; // number of records saved in one DataPage
	int counter; // counter to count the records saved(Helping to find the last page) 

	int pair_counter;  // counter to count the pairs saved(Helping to find the last page of pairs) 
	int pair_index;  // index of the current pairs page;
	int numberOfPairsPerPage ;  // number of pairs saved in one DataPage

	public DataPage(int dt,int r) {
		this.index = 0;
		this.dataSize = dt;
		this.numberOfRecordsPerPage = 0;
		this.records = r;
		this.counter = 0;
		this.WriteDataPage = java.nio.ByteBuffer.allocate(pageCapacity);
		this.pair_counter = 0;
		this.pair_index = 0;
		this.WriteDataPageForPairs = java.nio.ByteBuffer.allocate(pageCapacity);
		this.numberOfPairsPerPage =0;
	}
		
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//																Read And Write For (A)                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	public void randomWriteOnDisk(RandomAccessFile file,byte[] array) throws IOException {
				
		WriteDataPage.put(array);

		this.numberOfRecordsPerPage++;
		this.counter++;
		
		if(isFull() || isLastPage()) {
			try {
				file.seek(index*pageCapacity);
				index ++;
				file.write(WriteDataPage.array());
				numberOfRecordsPerPage = 0;
				WriteDataPage.rewind();
				WriteDataPage.put(zeroPageBytes); // clean the buffer of the page after it is written on disk
				WriteDataPage.rewind();
			}catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}

	public int randomReadFromDisk(int key, int[] keys) throws IOException {
		
		for(int i = 0; i < keys.length ; i++) {
			if(keys[i] == key) {
				return keys[i];					
			}
		}
	return Integer.MIN_VALUE;
	}
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//																Read And Write For (B)                                                                    //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public void indexedWriteOnDisk(RandomAccessFile file,byte[] array) {

		WriteDataPageForPairs.put(array);
		this.pair_counter++;
		numberOfPairsPerPage++;
		
		if(PairLastPage() || PairPageisFull()) {
			try {
				file.seek(pair_index*pageCapacity);
				pair_index ++;
				file.write(WriteDataPageForPairs.array());
				numberOfPairsPerPage = 0;
				WriteDataPageForPairs.rewind();
				WriteDataPageForPairs.put(zeroPageBytes); // clean the buffer of the page after it is written on disk
				WriteDataPageForPairs.rewind();
			}catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
	
	public int[] indexedUnsortedReadFromDisk(int key, int[] info) throws IOException {
			int[] pair = new int[2];
			
			for(int i = 0; i < info.length ; i+=2) { // i+=2 because we search for keys and not indexes
				if(info[i] == key) {
					pair[0] = info[i]; // key
					pair[1] = info[i+1]; // index
					return pair; // return an array of [key,index]
				}
			}
		return null;
	}
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//																		Read For (Ã)                                                                      //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public int indexedSortedReadFromDisk(int key, int[] info) throws IOException {
		
		for(int i = 0; i < info.length ; i+=2) {
			if(info[i] == key) {
			return 1; // if key is found return 1 
			}
		}
		
		if( info[0] > key) // if key is smaller than the first key on page return 2
			return 2;

			
		for(int i = info.length -1 ; i >= 0 ; i-=2) {
			if(info[i] != 0 && info[i] < key) { // if key is greater than the last key on page (and info[i]!=0 cause the page wont always be full) return 3
				return 3; 
			}
		}
		return -1;
	}	
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 													Functions useful for calculations in (A)                                                              //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public int calculateBytesOfAnInstance() {
		return (dataSize + Integer.BYTES);
	}

	public int calculateMaxInstancesPerPage() {
		return (DataPage.pageCapacity / calculateBytesOfAnInstance());
	}
	
	public boolean isFull() {
		if(numberOfRecordsPerPage == calculateMaxInstancesPerPage())
			return true;
		return false;
	}	
		
	public boolean isLastPage() {
		if(((numberOfRecordsPerPage%calculateMaxInstancesPerPage()) != 0) && counter == records) {
			return true;
		}
		return false;	
	}
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//													Functions useful for calculations in (B&Ã)                                                            //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public int calculateBytesOfAPair() {
		return (2 * Integer.BYTES);
	}

	public int calculateMaxPairsPerPage() {
		return (DataPage.pageCapacity / calculateBytesOfAPair());
	}
	
	public boolean PairPageisFull() {
		if(numberOfPairsPerPage == calculateMaxPairsPerPage())
			return true;
		return false;
	}	
		
	public boolean PairLastPage() {
		if(((numberOfPairsPerPage%calculateMaxPairsPerPage()) != 0) && pair_counter == records) {
			return true;
		}
		return false;	
	}
	
	 public int calculateMaxPagesForIndexedFile(int records) {
		 int i = (records*calculateBytesOfAPair()) % getPagecapacity();
		 
		 if(i == 0)
			 return (records*calculateBytesOfAPair())/getPagecapacity();
		 
		 return ((records*calculateBytesOfAPair())/getPagecapacity()) + 1;
	 }
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//																	Getters and Setters                                                                   //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getDataSize() {
		return dataSize;
	}

	public void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}

	public int getNumberOfRecordsPerPage() {
		return numberOfRecordsPerPage;
	}

	public void setNumberOfRecordsPerPage(int numberOfRecordsPerPage) {
		this.numberOfRecordsPerPage = numberOfRecordsPerPage;
	}

	public int getRecords() {
		return records;
	}

	public void setRecords(int records) {
		this.records = records;
	}

	public static int getPagecapacity() {
		return pageCapacity;
	}

	public java.nio.ByteBuffer getWriteDataPage() {
		return WriteDataPage;
	}

	public void setWriteDataPage(java.nio.ByteBuffer writeDataPage) {
		WriteDataPage = writeDataPage;
	}

	public java.nio.ByteBuffer getWriteDataPageForPairs() {
		return WriteDataPageForPairs;
	}

	public void setWriteDataPageForPairs(java.nio.ByteBuffer writeDataPageForPairs) {
		WriteDataPageForPairs = writeDataPageForPairs;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public int getPair_counter() {
		return pair_counter;
	}

	public void setPair_counter(int pair_counter) {
		this.pair_counter = pair_counter;
	}

	public int getPair_index() {
		return pair_index;
	}

	public void setPair_index(int pair_index) {
		this.pair_index = pair_index;
	}

	public int getNumberOfPairsPerPage() {
		return numberOfPairsPerPage;
	}

	public void setNumberOfPairsPerPage(int numberOfPairsPerPage) {
		this.numberOfPairsPerPage = numberOfPairsPerPage;
	}

	public byte[] getZeroPageBytes() {
		return zeroPageBytes;
	}
	

}