package com.example.gmall.manage;


import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class GmallManageWebApplicationTests {

	@Test
	void contextLoads() throws IOException, MyException {
		String tracker = GmallManageWebApplicationTests.class.getResource("/tracker.conf").getPath();
		ClientGlobal.init(tracker);

		TrackerClient trackerClient = new TrackerClient();

		//获得trackerServer的实例
		TrackerServer trackerServer = trackerClient.getConnection();

		// 通过trackerServer获得StorageClient
		StorageClient storageClient = new StorageClient(trackerServer,null);

		String[] uplods = storageClient.upload_file("C:\\Users\\86152\\Desktop\\123.PNG","PNG",null);

		for (String uplod : uplods) {
			System.out.println(uplod);
		}
	}

}
