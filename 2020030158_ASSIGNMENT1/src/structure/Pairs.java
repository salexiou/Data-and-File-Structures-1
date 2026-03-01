package structure;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Pairs implements Comparable<Pairs> {
	
	int key;
	int index;
	
	public Pairs(int key,int index) {
		this.key = key;
		this.index = index;
	}
	

	@Override
	public int compareTo(Pairs o) {
		if(this.key < o.key ) 
			return -1;
		
		if(this.key > o.key)
			return 1;
		return 0;
	}
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//													Functions to convert to bytes and reverse                                                             //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	public byte[] convertToBytes() throws IOException {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bos);
		
		out.writeInt(key);
		out.writeInt(index);
			
		out.close();

		byte[] buffer = bos.toByteArray();
		bos.close();
		return buffer; //return the array of bytes 
	}
	
	
	public static int[] convertToInstance(RandomAccessFile file,int page,DataPage dp) throws IOException{

		byte[] ReadDataPage = new byte[DataPage.pageCapacity];
		file.seek(page*DataPage.pageCapacity);
		file.read(ReadDataPage);	
		
		ByteArrayInputStream bis = new ByteArrayInputStream(ReadDataPage);
		DataInputStream din = new DataInputStream(bis);
	
		int[] info = new int[2*dp.calculateMaxPairsPerPage()];
		for(int i = 0; i < info.length; i+=2) {
			info[i] = din.readInt();
			info[i+1] = din.readInt();
		}
		return info;	// return an array that contains all [key[0],index[0],..key[32],index[32]] of one page 	
	}
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//																Getters and Setters                                                                       //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
}
