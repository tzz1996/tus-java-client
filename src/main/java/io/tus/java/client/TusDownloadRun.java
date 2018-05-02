package io.tus.java.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class TusDownloadRun implements Runnable{
	private File file;
	
	@Override
	public void run() {
		try {
			System.setProperty("http.strictPostRedirect", "true");

			//查询服务器上已有的完整文件
			Properties properties = new Properties();
			FileInputStream input = new FileInputStream("F:\\Workspace_for_jee\\tus-java-client-master\\server_file.properties");
	        properties.load(input);
	        input.close();
	        List<String> tmp ;//= new ArrayList();
	        tmp = new ArrayList(properties.stringPropertyNames());
	        //String inputValue = JOptionPane.showInputDialog("Please input a value");
	        Object[] possibleValues = tmp.toArray(); 
	        Object selectedValue = JOptionPane.showInputDialog(null, "Choose one", "Input",JOptionPane.INFORMATION_MESSAGE, null, possibleValues, possibleValues[0]);
	        
			final TusClient client = new TusClient();
			client.setDownloadCreationURL(new URL(selectedValue.toString()));
			
			//创建本地保存文件
			JFrame frame = new JFrame();
			JFileChooser fc = new JFileChooser();
			int select = fc.showSaveDialog(frame);
	        if(select == JFileChooser.APPROVE_OPTION) {  
	        	file = fc.getSelectedFile();
	        }  
	        String name = file.getName();
        	String path = file.getPath();
	   
			TusDownload download = new TusDownload(path, name, client.getDownloadCreationURL());
    
			TusURLMemoryStore tus_url_store = new TusURLMemoryStore();
			//String temp_url = "http://localhost:1081/files/93fd9bd4f47024a111d7478f09ba5664";
			//URL url = new URL(new String("http://localhost:1081/files/93fd9bd4f47024a111d7478f09ba5664"));
			//tus_url_store.set(download.getFingerprint(), new URL(new String("http://localhost:1081/files/93fd9bd4f47024a111d7478f09ba5664")));
			client.enableResuming(tus_url_store);
    
			TusDownloader downloader = client.resumeOrCreateDownload(download);

			// Upload the file in chunks of 1KB sizes.
			downloader.setChunkSize(1024);

 
			//设置进度条
			JProcessBar process_bar = new JProcessBar(download.getFileTotalSize());
			process_bar.setDownload(download, downloader);
			// Upload the file as long as data is available. Once the
			// file has been fully uploaded the method will return -1
			do {
				// Calculate the progress using the total size of the uploading file and
				// the current offset.
				//long totalBytes = download.getSize();
				//long bytesUploaded = uploader.getOffset();
				//double progress = (double) bytesUploaded / totalBytes * 100;
				//System.out.printf("%d"+"\n", downloader.getOffset());
				process_bar.setValue(downloader.getOffset());
				//System.out.printf("Upload at %06.2f%%.\n", progress);
			} while(downloader.downloadChunk() > -1);
   	
			// Allow the HTTP connection to be closed and cleaned up
			downloader.finish();

			if (download.getFileTotalSize() == downloader.getOffset()) {
				System.out.println("Download finished.");
				System.out.format("Download available at: %s", download.getFingerprint());
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
