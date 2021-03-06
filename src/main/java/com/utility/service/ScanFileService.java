package com.utility.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.utility.model.FilehashBean;
import com.utility.model.ServerBean;
import com.utility.repository.FilehashRepository;
import com.utility.repository.ServerRepository;

@Service
public class ScanFileService {
    
    @Autowired
    private FilehashRepository filehashRepo;
    private ServerRepository serverRepo;

    public void scanFolder(String filePath, String serverName, int threadNum, int intervalSecond) throws IOException, NoSuchAlgorithmException, InterruptedException {
    	ServerBean serverBean = serverRepo.findByServerName(serverName);
    	File file = new File(filePath);
        if (file.isDirectory()) {
            readFolder(file, serverBean, threadNum, intervalSecond);
        } else {
            updateDatabase(file, serverBean);
        }
        
    }
    private Semaphore semaphore;
    Integer count = new Integer(0);
	Integer threadCount = new Integer(0);

	
	
    private void readFolder(File file, ServerBean serverBean, int threadNum, int intervalSecond) throws IOException, NoSuchAlgorithmException, InterruptedException {
    	long total = Files.list(Paths.get(file.getAbsolutePath())).count();
    	threadCount = threadNum;
    	//while ()
    	File[] listFiles = file.listFiles();
        for (File f : listFiles) {
            if (f.isDirectory()) {
                readFolder(f, serverBean, threadNum, intervalSecond);
            } else {
            	semaphore.acquire();
                System.out.println(semaphore.availablePermits());
                    Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                      public void run() {
                        System.out.println(count + "/" + total);
                      }
                    }, 0, intervalSecond*1000);
                  
                              
                	while(threadCount <= 0){
                		Thread.sleep(100);
                	};
                          threadCount --;
				Thread tr = new Thread() {
                    public void run() {
                        try {
                            try {
								updateDatabase(f, serverBean);
								synchronized(count){
									count ++;
								}
								synchronized(threadCount){
									threadCount ++;
								}
//								Date currentDate = new Date();
//								if (currentDate.getTime() - startDate.getTime() >= intervalSecond){
//									System.out.println(count);
//									startDate.setTime(currentDate.getTime());
//								}
								
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }
                };
                tr.start();
            }
        }
    }
    
    



	// update file database
    private void updateDatabase(File f, ServerBean serverBean) throws IOException, NoSuchAlgorithmException {
        // read metadata
        BasicFileAttributes attr = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
        // read file from data base
        FilehashBean fileHashbean = filehashRepo.findByFullpath(f.getAbsolutePath());
        Date date = new Date();
        if (fileHashbean == null) {      	
			//insert the new file into database 
            FilehashBean fileBean = new FilehashBean();
            fileBean.setFullpath(f.getAbsolutePath());
            Timestamp lastmodifydate = new Timestamp(attr.lastModifiedTime().toMillis());
            fileBean.setLastmodifydate(lastmodifydate);
            fileBean.setLastscandate(new Timestamp(date.getTime()));
            fileBean.setFilesize(new BigInteger(Long.toString(attr.size())));
            String hash = calculateHash(f);
            fileBean.setSha256sum(hash);
            filehashRepo.save(fileBean);
        } else {
            // compare modify time and size
            if (comparefile(attr, fileHashbean)) {
                fileHashbean.setLastscandate(new Timestamp(date.getTime()));
                filehashRepo.save(fileHashbean);
            }else{
                Timestamp timestamp = new Timestamp(attr.lastModifiedTime().toMillis());
                fileHashbean.setLastmodifydate(timestamp);
                fileHashbean.setLastscandate(new Timestamp(date.getTime()));
                fileHashbean.setFilesize(new BigInteger(Long.toString(attr.size())));
                String hash = calculateHash(f);
                fileHashbean.setSha256sum(hash);
                fileHashbean.setServer(serverBean);
                filehashRepo.save(fileHashbean);
                
            }
        }

    }

 // an encode method to take a byte[] and return the hex
 	private static String encodeHex(byte[] digest) {
 	     StringBuilder sb = new StringBuilder();
 	    for (int i = 0; i < digest.length; i++) {
 	        sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
 	    }
 	    return sb.toString();
 	}
 	
 	 // calculate hash for the file
 	private static String calculateHash(File f) throws NoSuchAlgorithmException, IOException {
 				
 		 MessageDigest md = MessageDigest.getInstance("SHA-256");
 		 FileInputStream fis = new FileInputStream(f);
 		 byte[] dataBytes = new byte[1024];
 	   	// byte[] buffer = f.getAbsolutePath().getBytes("UTF-8");
        // md.update(buffer);
         int nread = 0;
         while ((nread = fis.read(dataBytes)) != -1) {
           md.update(dataBytes, 0, nread);
         };
         byte[] digest = md.digest();
         return encodeHex(digest);
     }

    // determine whether lastmodifytime and size of the file has been changed
    private  boolean comparefile(BasicFileAttributes attr, FilehashBean fileHashbean) {
        FileTime lastModifiedTime = attr.lastModifiedTime();
        Timestamp lastmodifydate = fileHashbean.getLastmodifydate();
        long time = lastmodifydate.getTime();
        if (time == lastModifiedTime.toMillis() && attr.size() == fileHashbean.getFilesize().longValue()) {
            return true;
        } else {
            return false;
        }
    }



}
