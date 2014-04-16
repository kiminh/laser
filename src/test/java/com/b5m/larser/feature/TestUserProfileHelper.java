package com.b5m.larser.feature;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

import com.b5m.conf.Configuration;

public class TestUserProfileHelper {
	private static final String PROPERTIES = "src/test/properties/laser.properties.examble";

	@BeforeTest
	public void setup() throws IOException {
		Path pro = new Path(PROPERTIES);
		org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();

		FileSystem fs = pro.getFileSystem(conf);
		Configuration.getInstance().load(pro, fs);
	}

	@Test
	public void test() throws ClassNotFoundException, IOException {
		UserProfileHelper helper = UserProfileHelper.getInstance();
		String key1 = "page_categories玩乐爱好";
		Integer val1 = helper.map(key1, true);
		String key2 = "page_categories运动户外";
		Integer val2 = helper.map(key2, true);

		Path metaq = Configuration.getInstance().getMetaqOutput();
		Path serializePath = new Path(metaq, "USER_FEATURE_MAP");
		FileSystem fs = serializePath
				.getFileSystem(new org.apache.hadoop.conf.Configuration());
		DataOutputStream out = fs.create(serializePath);

		helper.write(out);
		out.close();

		// deserialize
		helper = null;
		DataInputStream in = fs.open(serializePath);
		helper = UserProfileHelper.read(in);
		in.close();

		assertEquals(val2, helper.map(key2, false));
		assertEquals(val1, helper.map(key1, false));
	}

	@AfterTest
	public void close() throws IOException {
		Path metaq = Configuration.getInstance().getMetaqOutput();
		FileSystem fs = metaq
				.getFileSystem(new org.apache.hadoop.conf.Configuration());
		fs.delete(metaq, true);
	}
}
