package io.tus.java.client;

import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Window extends JFrame{
	private static final long serialVersionUID = 1L;
	private Label label = new Label();
	private Label labelVersion = new Label();
	private Label labelAuthor = new Label();
	private JButton btnUpload = new JButton("上传文件");
	private JButton btnDownload = new JButton("下载文件");
	private ArrayList<JButton> buttons = new ArrayList<JButton>();
	//private String filePath;
	
	private MyThreadFactory factory = new MyThreadFactory("MyThreadFactory");
	
	public Window() {
		super("tus断点续传客户端");
		this.setBounds(0, 0, 500, 260);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(null);
		this.setLocationRelativeTo(null);
		
		buttons.add(btnUpload);
		buttons.add(btnDownload);
		
		label.setBounds(100, 10, 260, 30);
		label.setText("tus java client");
		label.setFont(new java.awt.Font("MS Tang", Font.BOLD, 20));
		this.getContentPane().add(label);

		labelVersion.setBounds(360, 18, 50, 20);
		labelVersion.setText("v1.0");
		labelVersion.setFont(new java.awt.Font("MS Tang", Font.BOLD, 15));
		//		labelVersion.setBackground(Color.RED);
		this.getContentPane().add(labelVersion);

		labelAuthor.setBounds(350, 30, 150, 30);
		labelAuthor.setText("2018.4    by Tzz");
		labelAuthor.setFont(new java.awt.Font("MS Tang", Font.ITALIC, 10));
		this.getContentPane().add(labelAuthor);
		
		btnUpload.setBounds(50, 60, 180, 30);
		this.getContentPane().add(btnUpload);
		btnDownload.setBounds(50, 100, 180, 30);
		this.getContentPane().add(btnDownload);

		//设置监听
		for (int i = 0; i < buttons.size(); i++)
		{
			buttons.get(i).addActionListener(new MyListener(i, factory));
		}
	}
	
	private class MyListener implements ActionListener {
		private int ActionFLag = -1;
		private MyThreadFactory factory = new MyThreadFactory("MyThreadFactory");

		public MyListener(int pActionFLag, MyThreadFactory factory)
		{
			this.ActionFLag = pActionFLag;
			this.factory = factory;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {  //上传
			switch (this.ActionFLag) {
			case 0:  //上传
				TusUploadRun run1 = new TusUploadRun(Window.this);
				Thread thread1;
				thread1 = factory.newThread(run1);
				thread1.start();
				break;
				
			case 1:  //下载
				TusDownloadRun run2 = new TusDownloadRun();
				Thread thread2;
				thread2 = factory.newThread(run2);
				thread2.start();
				break;
				
			default:
				break;
			}
		}
	}
}
