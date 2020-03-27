package com.lfy.convertor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 图形化界面
 */
public class MainFrame extends JFrame {

    private JLabel ffmpegLabel=new JLabel("选择程序文件夹：");
    private JTextField jtf2=new JTextField(25);
    private JButton button2=new JButton("浏览");

    private JLabel sourceLabel=new JLabel("选择转换文件夹：");
    private JTextField jtf=new JTextField(25);
    private JButton button=new JButton("浏览");

    private JLabel distLabel=new JLabel("选择生成文件夹：");
    private JTextField jtf1=new JTextField(25);
    private JButton button1=new JButton("浏览");

    private JLabel distLabel3=new JLabel("选择要转换的文件类型：");
    JComboBox jComboBox = new JComboBox();

    private JButton convertorButton =new JButton("转换");

    private JTextArea jTextArea = new JTextArea("",14,47);
    JScrollPane jsp = new JScrollPane(jTextArea);

    public JComboBox getjComboBox() {
        return jComboBox;
    }

    public JTextArea getjTextArea() {
        return jTextArea;
    }

    public String getFFmpegPath() {
        return jtf2.getText();
    }

    public String getSourcePath() {
        return jtf.getText();
    }

    public String getDistPath() {
        return jtf1.getText();
    }

    //初始化图形化界面
    public void init() {

        this.setTitle("B站缓存视频--->MP4");
        this.setSize(535,410);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //水平间距10, 垂直间距5
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.add(ffmpegLabel);
        this.add(jtf2);
        this.add(button2);
        this.add(sourceLabel);
        this.add(jtf);
        this.add(button);
        this.add(distLabel);
        this.add(jtf1);
        this.add(button1);
        this.add(distLabel3);
        jComboBox.addItem("blv->mp4");
        jComboBox.addItem("m4s->mp4");
        this.add(jComboBox);
        this.add(convertorButton);
        jTextArea.setEditable(false);
        Dimension size=jTextArea.getPreferredSize();    //获得文本域的首选大小
        jsp.setBounds(110,90,size.width,size.height);
        //this.add(jTextArea);
        this.add(jsp);
        this.add(new JLabel("                                                                         Made By Lifanyu"));
        button.addActionListener(new OpenFileActionListener(0));    //监听按钮事件
        button1.addActionListener(new OpenFileActionListener(1));    //监听按钮事件
        button2.addActionListener(new OpenFileActionListener(2));    //监听按钮事件
        convertorButton.addActionListener(new ConvertorActionListener(this));
        this.setResizable(false);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();
        mainFrame.init();
    }

class OpenFileActionListener implements ActionListener {
    int buttonNum = 0;
    public OpenFileActionListener(int buttonNum) {
        this.buttonNum = buttonNum;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fc=new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int val=fc.showOpenDialog(null);    //文件打开对话框
        if(val==fc.APPROVE_OPTION)
        {
            if (buttonNum == 0) {
                //正常选择文件
                jtf.setText (fc.getSelectedFile().toString());
            } else if (buttonNum == 1) {
                jtf1.setText (fc.getSelectedFile().toString());
            } else {
                jtf2.setText (fc.getSelectedFile().toString());
            }
        }
        else
        {
            //未正常选择文件，如选择取消按钮
            jtf.setText("未选择文件夹");
        }
    }
}

class ConvertorActionListener implements ActionListener {

    private MainFrame mainFrame = null;

    public ConvertorActionListener(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    //视频名字 : entry.json --> page_data --> download_subtitle
    //视频文件夹 : entry.json --> type_tag
    //eg : C:\Users\15099\Desktop\11076511\1\type_tag\xxx.blv
    @Override
    public void actionPerformed(ActionEvent e) {
        String ffmpegPath = mainFrame.getFFmpegPath();
        String sourcePath = mainFrame.getSourcePath();
        String distPath = mainFrame.getDistPath();
        if (ffmpegPath != null && sourcePath != null && distPath != null){
            Convertor convertor = new Convertor(ffmpegPath,sourcePath,distPath,mainFrame);
            convertor.convert();
        }
    }
}

}
