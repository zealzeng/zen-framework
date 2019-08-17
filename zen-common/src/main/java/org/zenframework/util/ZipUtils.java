/*
 * Copyright (c) 2017, All rights reserved.
 */
package org.zenframework.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Zeal 2017年5月3日
 */
public class ZipUtils {
	
	/**
	 * Unzip 
	 * @param targetDir
	 */
	public static void unzip(File sourceFile, File targetDir, Charset charset) throws Exception {

		FileUtils.forceMkdir(targetDir);
		
		byte[] buffer = new byte[512];
		//ZipInputStream zis = null;
		ZipEntry entry = null;
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(sourceFile), charset)) {
		    while ((entry = zis.getNextEntry()) != null) {
		    	System.out.println(entry.getName());
		    	File file = new File(targetDir, entry.getName());
		    	if (entry.isDirectory()) {
		    		FileUtils.forceMkdir(file);
		    		continue;
		    	}
		    	File parentFile = file.getParentFile();
		    	if (parentFile != null) {
		    		FileUtils.forceMkdir(parentFile);
		    	}
				try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));) {
					int count = 0;
					while ( (count = zis.read(buffer)) != -1) {
						bos.write(buffer, 0, count);
					}
					zis.closeEntry();
					bos.flush();
				}
		    }
		}
	}
	
	public static void zip(File[] sourceFiles, File targetFile, Charset charset) throws Exception {
		zip(sourceFiles, targetFile, charset, Deflater.DEFAULT_COMPRESSION);
	}
	
	/**
	 * 
	 * @param sourceFiles
	 * @param targetFile
	 * @param charset
	 * @param zipLevel Refer to java.util.zip.Deflater level constants 
	 * @throws Exception
	 */
	public static void zip(File[] sourceFiles, File targetFile, Charset charset, final int zipLevel) throws Exception {
		
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(targetFile), charset)) {
			zos.setLevel(zipLevel);
			for (File sourceFile : sourceFiles) {
				if (sourceFile.isFile()) {
					zipFile(zos, "", sourceFile);
				}
				else if (sourceFile.isDirectory()) {
					zipDirectory(zos, "", sourceFile);
				}
			}
		}
	}
	
	private static void zipFile(ZipOutputStream zos, String parentEntryPath, File file) throws IOException {
		
		try (FileInputStream fis = new FileInputStream(file)) {
			String entryName = parentEntryPath + file.getName();
			zos.putNextEntry(new ZipEntry(entryName));
			IOUtils.copy(fis, zos);
			zos.closeEntry();
		}
	}
	
	private static void zipDirectory(ZipOutputStream zos, String parentEntryPath, File dir) throws IOException {
		
		String dirPath = parentEntryPath + dir.getName() + '/';
		zos.putNextEntry(new ZipEntry(dirPath));
		zos.closeEntry();
		
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				zipFile(zos, dirPath, file);
			}
			else if (file.isDirectory()) {
				String _parentEntryPath = dirPath + file.getName();
				zos.putNextEntry(new ZipEntry(_parentEntryPath));
				zipDirectory(zos, dirPath, file);
			}
		}
	}
	
	
	
	public static void main(String[] args) throws Exception {
//		File sourceFile = new File("d:/male02.zip");
//		File targetDir = new File("d:/tmp");
//		Charset charset = Charset.forName("ISO-8859-1");
//		unzip(sourceFile, targetDir, charset);
		
		File file1 = new File("e:/基于WEB企业进销存系统的研究与实现.doc");
		File target = new File("e:/基于WEB企业进销存系统的研究与实现.zip");
		File[] sources = new File[] {file1};
		long start = System.currentTimeMillis();
		//zip(sources, target, Charset.forName("UTF-8"), Deflater.BEST_COMPRESSION);
		zip(sources, target, Charset.forName("UTF-8"));
		long end = System.currentTimeMillis();
		System.out.println("done=" + (end-start)/1000);
		
	}


}
