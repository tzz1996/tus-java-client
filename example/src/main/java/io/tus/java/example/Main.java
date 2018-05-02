package io.tus.java.example;

import io.tus.java.client.Window;



public class Main  {
	public static void main(String[] args)
	{
		new Window();
//		try {
//			FileInputStream input = new FileInputStream("F:\\迅雷下载\\缝纫机乐队\\缝R机乐队.2017.HD1080P.中英双字.mkv");
//			FileOutputStream output = new FileOutputStream("d:\\123.mkv");
//			byte[] buffer = new byte[1024];
//			while (input.read(buffer, 0, 1024) > -1) {
//				output.write(buffer, 0, 1024);
//			}
//		} catch (Exception e){
//			e.printStackTrace();
//		}
	}
}



/*import java.awt.FlowLayout;  

import javax.swing.JFrame;  
import javax.swing.JPanel;  
import javax.swing.JProgressBar;  
import javax.swing.border.EmptyBorder;  
import java.awt.Robot;
  
public class Main extends JFrame {  
    public Main(){  
        this.setTitle("进度条的使用");  
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        this.setBounds(100, 100, 250, 100);  
        JPanel contentPane=new JPanel();  
        contentPane.setBorder(new EmptyBorder(5,5,5,5));  
        this.setContentPane(contentPane);  
        contentPane.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));  
        final JProgressBar progressBar=new JProgressBar();  
        progressBar.setStringPainted(true);  
        new Thread(){  
            public void run(){  
                for(int i=0;i<=100;i++){  
                    try{  
                        Thread.sleep(100);  
                    }catch(InterruptedException e){  
                        e.printStackTrace();  
                    }  
                      progressBar.setValue(i);  
                }  
                progressBar.setString("升级完成！");  
            }  
        }.start(); 
        contentPane.add(progressBar);  
        this.setVisible(true);  
    }  
    public static void main(String[]args){  
        Main example=new Main();  
    }  
} */