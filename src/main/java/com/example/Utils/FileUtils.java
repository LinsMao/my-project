package com.example.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class FileUtils {
    /**
     * 从微信临时文件链接下载图片，保存到指定目录，返回新文件名
     * @param tempUrl 微信临时路径（如 http://tmp/xxx.jpeg）
     * @param uploadDir 服务器保存目录（绝对路径，例如 D:/my-images/avatar/）
     * @return 新的文件名（含扩展名）
     */
    public static String downloadWechatTempFile(String tempUrl, String uploadDir){
        try{

            // 确保目录存在
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 从 URL 中提取扩展名（如 .jpeg, .png）
            String ext = "";
            int dotIndex = tempUrl.lastIndexOf('.');
            if (dotIndex > 0) {
                ext = tempUrl.substring(dotIndex);
            } else {
                ext = ".jpg";
            }

            //重新命名文件
            String fileName= UUID.randomUUID().toString()+ext;
            String filePath=uploadDir+fileName;

            // 使用 HttpURLConnection 下载
            URL url = new URL(tempUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            try (InputStream in = conn.getInputStream()) {
                Files.copy(in, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            }

            conn.disconnect();

            return fileName;


        }catch (Exception e){
            throw new RuntimeException("头像下载失败：" + e.getMessage(), e);
        }

    }



















}
