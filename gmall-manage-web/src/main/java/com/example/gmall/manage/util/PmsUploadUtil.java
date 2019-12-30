package com.example.gmall.manage.util;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class PmsUploadUtil {



    public static String uploadImage(MultipartFile multipartFile) {

        String url ="http://192.168.137.2";

        String tracker = PmsUploadUtil.class.getResource("/tracker.conf").getPath();
        try {
            ClientGlobal.init(tracker);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TrackerClient trackerClient = new TrackerClient();

        //获得trackerServer的实例
        TrackerServer trackerServer = null;
        try {
            trackerServer = trackerClient.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 通过trackerServer获得StorageClient
        StorageClient storageClient = new StorageClient(trackerServer, null);


        try {
            byte[] bytes = multipartFile.getBytes();  // 获得上传的二进制对象
            String originalFilename = multipartFile.getOriginalFilename(); //获得文件的全名

            int i = originalFilename.lastIndexOf(".");
            String extName = originalFilename.substring(i+1);

            String[] uplods = storageClient.upload_file(bytes, extName, null);

            for (String uplod : uplods) {
                url += "/"+ uplod;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;

    }
}
