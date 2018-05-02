package io.tus.java.client;

//import java.awt.Color;  
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;  
import javax.swing.JPanel;  
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder; 

public class JProcessBar extends JFrame{
	private static final long serialVersionUID = 2L;
	private JPanel contentPane = new JPanel(); 
	private final JProgressBar processBar; 
	private TusUploader uploader;
	private TusDownload download;
	private TusDownloader downloader;
	private JButton btnStop = new JButton("暂停");
	private JButton btnResume = new JButton("继续");
	
	public JProcessBar(int fileSize) {
		setTitle("进度条使用");    
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 350, 100);         
        //JPanel contentPane = new JPanel();  
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));  
        setContentPane(contentPane);     
        contentPane.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));     
        processBar = new JProgressBar();       
        processBar.setStringPainted(true); 
        //processBar.setBackground(Color.red); 
        processBar.setMaximum(fileSize);
        contentPane.add(processBar);
        
        btnResume.setBounds(50, 60, 180, 30);
        btnStop.setBounds(50, 60, 180, 30);
        contentPane.add(btnResume);
        contentPane.add(btnStop);
        this.setVisible(true);
	}
	
	public void setValue(long currentValue) {
		int tmp = (int)currentValue;
		processBar.setValue(tmp);
	}
	
	public void setDownload(TusDownload download, TusDownloader downloader) {
		this.download = download;
		this.downloader = downloader;
		btnResume.addActionListener(new DownloadListener(0, download, downloader));
		btnStop.addActionListener(new DownloadListener(1, download, downloader));
	}
	
	public void setUpload(TusUploader uploader) {
		this.uploader = uploader;
		btnResume.addActionListener(new UploadListener(0, uploader));
		btnStop.addActionListener(new UploadListener(1, uploader));
	}
	
	private class DownloadListener implements ActionListener {
		private int ActionFLag = -1;
		private TusDownloader downloader;
		private TusDownload download;
		
		public DownloadListener(int pActionFLag, TusDownload download, TusDownloader downloader) {
			this.ActionFLag = pActionFLag;
			this.download = download;
			this.downloader = downloader;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (this.ActionFLag) {
			case 0:  //继续下载
				new Thread() {
					public void run() {
						try {
							downloader.setOutput();
							do {
								setValue(downloader.getOffset());
							} while(downloader.downloadChunk() > -1);
							downloader.finish();
							
							if (download.getFileTotalSize() == downloader.getOffset()) {
								System.out.println("Download finished.");
								System.out.format("Download available at: %s", download.getFingerprint());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.start();
				break;
			case 1:  //暂停下载，并写配置文件
				try {
					downloader.stop();
					//downloader.finishConnection();
//					FileInputStream config_input = new FileInputStream("F:\\Workspace_for_jee\\tus-java-client-master\\download_offset.properties");
//					Properties download_properties = new Properties();
//					download_properties.load(config_input);
//		        	download_properties.setProperty(downloader.getSavePath(), Long.toString(downloader.getOffset()));
//		        	FileOutputStream tmp = new FileOutputStream("F:\\Workspace_for_jee\\tus-java-client-master\\download_offset.properties");
//		        	download_properties.store(tmp, "每次下载chunk记录offset");
//		        	tmp.close();
//		        	config_input.close();
//		        	download_properties.clear();
				} catch(Exception e1) {
					e1.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
	}
	
	private class UploadListener implements ActionListener {
		private int ActionFLag = -1;
		private TusUploader uploader;
		
		public UploadListener(int pActionFLag, TusUploader uploader) {
			this.ActionFLag = pActionFLag;
			this.uploader = uploader;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (this.ActionFLag) {
			case 0:  //继续上传
				new Thread() {
					public void run() {
						try {
							uploader.setInput();
							do {
								setValue(uploader.getOffset());
							} while(uploader.uploadChunk() > -1);
							uploader.finish();

							if (uploader.getOffset() == uploader.getFileTotalSize()) {
			                    System.out.println("Upload finished.");
			                    System.out.format("Upload available at: %s", uploader.getUploadURL().toString());
							}
						} catch(Exception e2) {
							e2.printStackTrace();
						}
					}
				}.start();
				break;
			case 1:  //暂停上传
				try {
					//uploader.finishConnection();
					uploader.stop();
				} catch(Exception e1) {
					e1.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
	}
}
