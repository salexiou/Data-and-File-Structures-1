package structure;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class DataClass {
		
		int key;
		String data;
	
	public DataClass(int key, String data) {
		this.key = key;
		this.data = data;
	}
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//												Functions to convert to bytes and reverse                                                                 //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	
	public byte[] convertToBytes() throws IOException {
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(bos);
			
			java.nio.ByteBuffer dst = java.nio.ByteBuffer.allocate(data.length());
			dst.put(data.getBytes(java.nio.charset.StandardCharsets.US_ASCII));
			
			out.writeInt(key);
			out.write(dst.array());
			
			dst.rewind();
			dst.put(new byte[data.length()]);
			dst.rewind();		
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
		int[] keys = new int[dp.calculateMaxInstancesPerPage()];
		for(int i = 0; i < keys.length; i++) {
			keys[i] = din.readInt();
			byte bb[] = new byte[dp.dataSize];
			din.read(bb);
		}
		
		return keys; // return the keys of a page
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
	
	
	public String getData() {
		return data;
	}
	
	
	public void setData(String data) {
		this.data = data;
	}
	

}
