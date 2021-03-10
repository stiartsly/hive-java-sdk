package org.elastos.hive;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.FilesService;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FilesServiceTest {

	@Test
	public void test01_uploadText() {
		FileReader fileReader = null;
		Writer writer = null;
		try {
			writer = filesApi.upload(remoteTextPath, Writer.class).exceptionally(e -> {
				fail();
				return null;
			}).get();
			fileReader = new FileReader(textLocalPath);
			char[] buffer = new char[1];
			while (fileReader.read(buffer) != -1) {
				writer.write(buffer);
			}
			System.out.println("write success");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
				if (null != fileReader) fileReader.close();
				if (null != writer) writer.close();
			} catch (Exception e) {
				fail();
			}
		}
	}


	@Test
	public void test02_uploadBin() {
		try {
			OutputStream outputStream = filesApi.upload(remoteImgPath, OutputStream.class)
					.exceptionally(e-> {
						fail();
						return null;
					}).get();
			byte[] bigStream = Utils.readImage(imgLocalPath);
			outputStream.write(bigStream);
			outputStream.close();
			System.out.println("write success");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test03_downloadText() {
		try {
			Reader reader = filesApi.download(remoteTextPath, Reader.class)
					.exceptionally(e-> {
						fail();
						return null;
					}).get();
			Utils.cacheTextFile(reader, rootLocalCachePath, "test.txt");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test04_downloadBin() {
		try {
			InputStream inputStream = filesApi.download(remoteImgPath, InputStream.class)
					.exceptionally(e-> {
						fail();
						return null;
					}).get();
			Utils.cacheBinFile(inputStream, rootLocalCachePath, "big.png");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test05_list() {
		//TODO:
	}

	@Test
	public void test06_hash() {
		try {
			CompletableFuture<Boolean> future = filesApi.hash(remoteTextPath)
					.handle((result, ex) -> ex == null);
			assertTrue(future.get());
			assertFalse(future.isCompletedExceptionally());
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test07_move() {
		try {
			CompletableFuture<Boolean> future = filesApi.delete(remoteTextBackupPath)
					.thenCompose(result -> filesApi.move(remoteTextPath, remoteTextBackupPath))
					.handle((result, ex) -> ex == null);
			assertTrue(future.get());
			assertFalse(future.isCompletedExceptionally());
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test08_copy() {
		try {
			CompletableFuture<Boolean> future = filesApi.copy(remoteTextBackupPath, remoteTextPath)
					.handle((result, ex) -> ex == null);
			assertTrue(future.get());
			assertFalse(future.isCompletedExceptionally());
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}


	@Test
	public void test09_deleteFile() {
		try {
			CompletableFuture<Boolean> future = filesApi.delete(remoteTextPath)
					.thenCompose(result -> filesApi.delete(remoteTextBackupPath))
					.handle((result, ex) -> ex == null);
			assertTrue(future.get());
			assertFalse(future.isCompletedExceptionally());
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}


	@BeforeClass
	public static void setUp() {
		try {
			filesApi = TestData.getInstance().getVault().thenApplyAsync(vault -> vault.getFilesService()).join();
		} catch (HiveException|DIDException e) {
			e.printStackTrace();
		}
	}

	private final String textLocalPath;
	private final String imgLocalPath;
	private final String rootLocalCachePath;
	@SuppressWarnings("unused")
	private final String textLocalCachePath;
	@SuppressWarnings("unused")
	private final String imgLocalCachePath;

	private final String remoteRootPath;
	private final String remoteTextPath;
	private final String remoteImgPath;
	private final String remoteTextBackupPath;

	private static FilesService filesApi;

	public FilesServiceTest() {
		String localRootPath = System.getProperty("user.dir") + "/src/test/resources/local/";
		textLocalPath = localRootPath +"test.txt";
		imgLocalPath = localRootPath +"big.png";
		rootLocalCachePath = localRootPath + "cache/file/";
		textLocalCachePath = rootLocalCachePath + "test.txt";
		imgLocalCachePath = rootLocalCachePath + "/big.png";

		remoteRootPath = "hive";
		remoteTextPath = remoteRootPath + File.separator + "test.txt";
		remoteImgPath = remoteRootPath + File.separator + "big.png";
		remoteTextBackupPath = "backup" + File.separator + "test.txt";
	}
}
