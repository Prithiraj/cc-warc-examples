package org.commoncrawl.test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.zip.GZIPInputStream;

public class WARCReadTest {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		AmazonS3Client s3 = new AmaozonS3Client();
		String s3domain= "s3://aws-publicdatasets";
		
		InputStream fn = new FileInputStream("s3://commoncrawloutput/wat.list.gz");
		GZIPInputStream gis = new GZIPInputStream(fn);
		InputStreamReader r = new InputStreamReader(gis);// gis = new GZIPInputStream(fn);
		
		BufferedReader br = new BufferedReader(r);
		
		String line="";
		int lineNumber=0;
		
		while ((line = br.readLine()) != null) {
	        
			System.out.println(line);
			
	    }
		
	}

}
