package com.lfy.convertor;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * b站缓存文件 --> MP4 转换器
 * 基于 FFmpeg 程序
 *
 *  @Author 李凡宇
 *
 */
public class Convertor {

    private String ffmpegPath;
    private String sourcePath;
    private String distPath;
    private MainFrame mainFrame;

    public Convertor(String ffmpegPath, String sourcePath, String distPath, MainFrame mainFrame) {
        this.ffmpegPath = ffmpegPath;
        this.sourcePath = sourcePath;
        this.distPath = distPath;
        this.mainFrame = mainFrame;
    }

    /**
     * m4s 转换视频 MP4 方法
     */
    public void m4s2mp4() {
        // 0. 遍历要合成的视频
        File file = new File(sourcePath);
        String[] list = file.list();
        int i = 0;
        for (String name : list) {
            i++;
            // 1. 获取要合成的视频名称
            String videoName = getVedioName(sourcePath + "/" + name);
            // 2. 转换视频
            convertToMP4(sourcePath + "\\"+ i + "\\80", i, videoName);
        }
    }

    /**
     * 转换b站缓存为 MP4 格式
     * @param path 当前需要转换文件的父目录
     * @param i    当前转换的第几个文件
     * @param videoName 文件名
     */
    //ffmpeg -i C:\Users\15099\Desktop\99136477\1\80\audio.m4s
    //       -i C:\Users\15099\Desktop\99136477\1\80\video.m4s
    //       -codec
    //       copy
    //       C:\Users\15099\Desktop\dest\4.mp4
    private void convertToMP4(String path, int i, String videoName) {
        JTextArea jTextArea = mainFrame.getjTextArea();
        jTextArea.setText(jTextArea.getText() + "第"+i+"个视频开始转换.\n");
        ProcessBuilder processBuilder = new ProcessBuilder();
        List<String> commonds =  new ArrayList<>();
        commonds.add(ffmpegPath +"\\bin\\ffmpeg.exe");
        commonds.add("-i");
        commonds.add(path+"\\audio.m4s");
        commonds.add("-i");
        commonds.add(path+"\\video.m4s");
        commonds.add("-codec");
        commonds.add("copy");
        commonds.add(distPath+"\\"+videoName+".mp4");
        processBuilder.command(commonds);
        try {
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        jTextArea.setText(jTextArea.getText() + "第"+i+"个视频结束转换.\n");
    }

    /**
     * 获取要转换的视频的个数
     * @param path
     * @return
     */
    private int getVedioNum(String path,String suffix) {
        File file = new File(path);
        String[] list = file.list();
        int vedioNum = 0;
        for (String name : list) {
            if (name.contains(suffix))
                vedioNum++;
        }
        return vedioNum;
    }

    /**
     * 获取视频名称
     * @param path 路径
     * @return
     */
    private String getVedioName(String path) {
        File file = new File(path+"/entry.json");
        if (file != null) {
            try {
                String s = FileUtils.readFileToString(file, "utf-8");
                JSONObject parse = JSONObject.parseObject(s);
                parse = (JSONObject) parse.get("page_data");
                String regEx="[\n`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？]";
                String fileName = parse.get("download_subtitle").toString();
                return fileName.replaceAll(regEx,"");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //将 blv 后缀改为 mp4
    private void renameFile(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        for (int i = 0; i < getVedioNum(path,"blv"); i++) {
            if (files[i].getName().endsWith(".blv"))
                files[i].renameTo(new File(path+"\\"+files[i].getName()+".mp4"));
        }
    }

    //entry.json --> type_tag
    public static String getVedioPath(String path) {
        File file = new File(path+"/entry.json");
        if (file != null) {
            try {
                String s = FileUtils.readFileToString(file, "utf-8");
                JSONObject parse = JSONObject.parseObject(s);
                return parse.get("type_tag").toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     *  ffmpeg -i 1.mp4 -vcodec copy -acodec copy -vbsf h264_mp4toannexb 1.ts
     *  ffmpeg -i 2.mp4 -vcodec copy -acodec copy -vbsf h264_mp4toannexb 2.ts
     *  ffmpeg -i "concat:1.ts|2.ts" -acodec copy -vcodec copy -absf aac_adtstoasc output.mp4
     */
    private void convertToTS(String path) {

        int vedioNum = getVedioNum(path,"mp4");
        String[] files = new File(path).list();

        ProcessBuilder processBuilder = new ProcessBuilder();
        List<String> commonds = null;
        for (int i = 0; i < vedioNum; i++) {
            commonds =  new ArrayList<>();
            commonds.add(ffmpegPath+"\\bin\\ffmpeg.exe");
            commonds.add("-i");
            commonds.add(path+"\\"+files[i]);
            commonds.add("-vcodec");
            commonds.add("copy");
            commonds.add("-acodec");
            commonds.add("copy");
            commonds.add("-vbsf");
            commonds.add("h264_mp4toannexb");
            commonds.add(path+"\\"+i+".ts");
            processBuilder.command(commonds);
            try {
                processBuilder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 合并 ts 文件
     * ffmpeg -i "concat:1.ts|2.ts" -acodec copy -vcodec copy -absf aac_adtstoasc output.mp4
     */
    private boolean concat(String path, String vedioname) {
        File file = new File(path);
        String[] list = file.list();
        ProcessBuilder processBuilder = new ProcessBuilder();
        List<String> commonds = null;
        commonds =  new ArrayList<>();
        commonds.add(ffmpegPath+"\\bin\\ffmpeg.exe");
        commonds.add("-i");
        String vedioList = "\"concat:";
        for (String vedioName:list) {
            if (vedioName.contains(".ts"))
                vedioList = vedioList+path+"\\"+vedioName+"|";
        }
        vedioList = vedioList.substring(0,vedioList.length()-1)+"\"";
        commonds.add(vedioList);
        commonds.add("-acodec");
        commonds.add("copy");
        commonds.add("-vcodec");
        commonds.add("copy");
        commonds.add("-absf");
        commonds.add("aac_adtstoasc");
        commonds.add(distPath+"\\"+vedioname+".mp4");
        processBuilder.command(commonds);
        try {
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void delete(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        for (File file1 : files) {
            if (file1.getName().endsWith(".ts")) {
                try {
                    file1.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void convert() {
        String select = mainFrame.getjComboBox().getSelectedItem().toString();
        if (select != null) {
            if (select.equals("blv->mp4")) {
                blv2mp4();
            } else if (select.equals("m4s->mp4")) {
                m4s2mp4();
            }
        }
    }

    // blv2 ---> mp4
    private void blv2mp4() {
        JTextArea jTextArea = mainFrame.getjTextArea();
        File file = new File(sourcePath);
        String[] listName = file.list();
        String vedioName = null;
        String vedioPath = null;
        jTextArea.setText(jTextArea.getText()+"\n======共有"+listName.length+"个视频需要处理,请稍后.======");
        jTextArea.setText(jTextArea.getText()+"\n======正在努力处理.======");
        for (int i = 0; i < listName.length; i++) {
            jTextArea.setText(jTextArea.getText()+"\n======正在处理第"+(i+1)+"个视频.======");
            // 0. 获取合并完成后的视频的名称
            vedioName = getVedioName(sourcePath+"/"+listName[i]);
            // 1. 进入到 blv 视频文件的目录中
            vedioPath = sourcePath+"\\"+listName[i] + "\\" + getVedioPath(sourcePath+"\\"+listName[i]);
            // 2. 将 blv 文件重命名为 mp4 格式
            renameFile(vedioPath);
            // 3. 将 mp4 格式转为 ts 格式
            convertToTS(vedioPath);
            // 4. 合并 ts
            concat(vedioPath,vedioName);
            jTextArea.setText(jTextArea.getText()+"\n======第"+(i+1)+"个视频处理完成.======");
        }
        jTextArea.setText(jTextArea.getText()+"\n======"+listName.length+"个视频处理完成.======");
    }

}
