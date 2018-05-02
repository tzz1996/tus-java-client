package io.tus.java.client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class TusDownloader {
	private URL downloadURL;
	private String save_file_path;
    private InputStream input;
    private FileInputStream config_input;

    private long offset;
    private long file_total_size;
    private Properties download_properties;
    private TusClient client;
    private byte[] buffer;
    private int requestPayloadSize = 1024 * 1024 * 1024;
    private int bytesRemainingForRequest;
    private int counter = 0;
    private boolean stop = false;

    private HttpURLConnection connection;
    //private OutputStream output;
    private RandomAccessFile output;
    
    public TusDownloader(TusClient client, URL downloadURL, String save_file_path, long offset) throws IOException, ProtocolException {
        this.downloadURL = downloadURL;
        this.offset = offset;
        this.client = client;
        this.file_total_size = Long.valueOf(file_total_size);
        download_properties = new Properties();
//        config_input = new FileInputStream("F:\\Workspace_for_jee\\tus-java-client-master\\download_offset.properties");
//        config_output = new FileOutputStream("F:\\Workspace_for_jee\\tus-java-client-master\\download_offset.properties");
        
        this.save_file_path = save_file_path;
        output = new RandomAccessFile(save_file_path,"rw");
        output.seek(offset);

        setChunkSize(2 * 1024 * 1024);
    }
    
    public void stop() {
    	stop = true;
    }
    
    private void openConnection() throws IOException, ProtocolException {
        // Only open a connection, if we have none open.
        if(connection != null) {
            return;
        }

        bytesRemainingForRequest = requestPayloadSize;
        //input.mark(requestPayloadSize);

        connection = (HttpURLConnection) downloadURL.openConnection();
        //connection.setRequestProperty("RANGE", "bytes=" + Long.toString(offset) +"-");
        connection.setRequestMethod("GET");
        client.prepareConnection(connection);
    	connection.setDoInput(true);    
        connection.setDoOutput(true);    
        connection.setUseCaches(false);
    	connection.connect();
    	
        int responseCode = connection.getResponseCode();
        if(!(responseCode >= 200 && responseCode < 300)) {
            throw new ProtocolException("unexpected status code (" + responseCode + ") while resuming download", connection);
        }
        
        input = connection.getInputStream();
        input.skip(offset);
    }
    
    public void setOutput() {
    	try {
    		output = new RandomAccessFile(save_file_path,"rw");
    		output.seek(offset);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public void setChunkSize(int size) {
        buffer = new byte[size];
    }
    
    public String getSavePath() {
    	return save_file_path;
    }
    
    public int getChunkSize() {
        return buffer.length;
    }
    
    public void setRequestPayloadSize(int size) throws IllegalStateException {
        if(connection != null) {
            throw new IllegalStateException("payload size for a single request must not be " +
                    "modified as long as a request is in progress");
        }

        requestPayloadSize = size;
    }
    
    public int downloadChunk() throws IOException, ProtocolException {
    	openConnection();
    	if (stop) {
    		stop = false;
    		return -1;
    	}
    	
        //int bytesToRead = Math.min(getChunkSize(), bytesRemainingForRequest);
    	int bytesToRead = 1024;
    	
        int bytesRead = input.read(buffer, 0, bytesToRead);
        if(bytesRead == -1) {
            // No bytes were read since the input stream is empty
            return -1;
        }

        // Do not write the entire buffer to the stream since the array will
        // be filled up with 0x00s if the number of read bytes is lower then
        // the chunk's size.
        output.write(buffer, 0, bytesRead);
        
        offset += bytesRead;
        
        //每次下载1024*1024 bytes(1M)时记录offset的值
        //TODO
        if (counter % 1024 == 0) {
        	download_properties.load(new FileInputStream("F:\\Workspace_for_jee\\tus-java-client-master\\download_offset.properties"));
        	download_properties.setProperty(save_file_path, Long.toString(offset));
        	//FileOutputStream tmp = new FileOutputStream("F:\\Workspace_for_jee\\tus-java-client-master\\download_offset.properties");
        	download_properties.store(new FileOutputStream("F:\\Workspace_for_jee\\tus-java-client-master\\download_offset.properties"), "每次下载chunk记录offset");
        	//download_properties.clear();
        	//config_output.flush();
        }
        
        bytesRemainingForRequest -= bytesRead;

//        if(bytesRemainingForRequest <= 0) {
//            finishConnection();
//        }

        counter++;
        return bytesRead;
    }
    
    public long getOffset() {
        return offset;
    }

    public URL getDownloadURL() {
        return downloadURL;
    }
    
    public void finish() throws ProtocolException, IOException {
        finishConnection();
        // Close the TusInputStream after checking the response and closing the connection to ensure
        // that we will not need to read from it again in the future.
        //input.close();
        //config_input.close();
        //config_output.close();
    }
    
    public void finishConnection() throws ProtocolException, IOException {
        if(output != null) output.close();
        if(input != null) input.close();
        
        download_properties.load(new FileInputStream("F:\\Workspace_for_jee\\tus-java-client-master\\download_offset.properties"));
    	download_properties.setProperty(save_file_path, Long.toString(offset));
    	download_properties.store(new FileOutputStream("F:\\Workspace_for_jee\\tus-java-client-master\\download_offset.properties"), "每次下载chunk记录offset");
        
        if(connection != null) {
            int responseCode = connection.getResponseCode();
            connection.disconnect();

            if (!(responseCode >= 200 && responseCode < 300)) {
                throw new ProtocolException("unexpected status code (" + responseCode + ") while downloading chunk", connection);
            }
            connection = null;
        }
    }

}