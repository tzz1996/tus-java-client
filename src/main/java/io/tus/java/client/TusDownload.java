package io.tus.java.client;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class TusDownload {
	private InputStream input;
    private TusInputStream tusInputStream;
    private String fingerprint;
    private String file_total_size;
    private Map<String, String> metadata;
    private String save_file_path;
    private String offset_string;
    private URL download_url;
    
    public final static String TUS_VERSION = "1.0.0";
    
    public TusDownload() {
    	
    }
    
    
    public TusDownload(String save_path, String save_file_name, URL download_url) {
    	File file=new File(save_path);
//    	if (!file.exists())  
//        {  
//           //如果文件夹不存在，则创建新的的文件夹  
//            file.mkdirs();  
//        }  
    	
    	this.download_url = download_url;
    	//this.offset_string = "269157376";
    	save_file_path = save_path;
    	fingerprint = save_file_path;
    	metadata = new HashMap<String, String>();
        metadata.put("filename", save_file_name);
        
        //通过连接资源获取文件大小
        try {
        	HttpURLConnection connection = (HttpURLConnection) download_url.openConnection();
        	connection.setRequestMethod("HEAD");
        	connection.addRequestProperty("Tus-Resumable", TUS_VERSION);
        	connection.connect();	
            int responseCode = connection.getResponseCode();
            if(!(responseCode >= 200 && responseCode < 300)) {
                throw new ProtocolException("unexpected status code (" + responseCode + ") while resuming download", connection);
            }
            file_total_size = connection.getHeaderField("Upload-Length");
            connection.disconnect();
        } catch(Exception e) {
			e.printStackTrace();
		}
    }
    
    public int getFileTotalSize() {
    	int tmp = Integer.valueOf(file_total_size).intValue();
    	return tmp;
    }
    
    public String getFingerprint() {
        return fingerprint;
    }
    
    public String getSaveFilePath() {
    	return save_file_path;
    }
    
    public URL getDownloadURL() {
    	return download_url;
    }
    
    public String getOffsetString() {
    	return offset_string;
    }
    
    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }
    
    public InputStream getInputStream() {
        return input;
    }

    TusInputStream getTusInputStream() {
        return tusInputStream;
    }
    
    public void setInputStream(InputStream inputStream) {
        input = inputStream;
        tusInputStream = new TusInputStream(inputStream);
    }
    /*public TusDownload(String url, String save_path) {
    	File file=new File(save_path);  
        //判断文件夹是否存在  
        if (!file.exists())  
        {  
           //如果文件夹不存在，则创建新的的文件夹  
            file.mkdirs();  
        }  
        FileOutputStream output_stream = null;  
        HttpURLConnection conn = null;  
        InputStream input_stream = null;  
        try  
        {  
             // 建立链接  
             URL httpUrl=new URL(url);  
             conn=(HttpURLConnection) httpUrl.openConnection();  
             //以Post方式提交表单，默认get方式  
             conn.setRequestMethod("GET");  
             conn.setDoInput(true);    
             conn.setDoOutput(true);  
             // post方式不能使用缓存   
             conn.setUseCaches(false);  
             //连接指定的资源   
             conn.connect();  
             int responseCode = conn.getResponseCode();
             //获取网络输入流  
             input_stream=conn.getInputStream();  
             BufferedInputStream bis = new BufferedInputStream(input_stream);
             output_stream = new FileOutputStream(save_path + "123.mkv");  
             BufferedOutputStream bos = new BufferedOutputStream(output_stream);  
               
             byte[] buf = new byte[4096];  
             int length = bis.read(buf);  
             //保存文件  
             while(length != -1)  
             {  
                 bos.write(buf, 0, length);  
                 length = bis.read(buf);  
             }  
             bos.close();  
             bis.close();  
             conn.disconnect();  
        } catch (Exception e)  
        {  
             e.printStackTrace();  
             System.out.println("抛出异常！！");  
        }  
    }*/
}