package com.mobileleader.edoc.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 파일 함수 유틸
 */
public class FileUtil
{
	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * 파일삭제
	 *
	 * @param File file 대상파일
	 * @return void
	 * @throws Exception 
	 */
	public static void fileDelete(File file) throws Exception
	{
		try
		{
			file.delete();
		}
		catch(Exception e)
		{
			logger.error("file delete fail", e);
			throw e;
		}
	}

	/**
	 * 파일삭제
	 *
	 * @param String filePath 디렉토리경로
	 * @param String fileName 파일명
	 * @return void
	 * @throws Exception 
	 */
	public static void fileDelete(String filePath, String fileName) throws Exception
	{
		fileDelete(new File(filePath,fileName));
	}

	/**
	 * 디렉토리삭제
	 *
	 * @param String filePath 디렉토리경로
	 * @return void
	 */
	public static boolean deleteDir(String path)
	{
		return deleteDir(new File(path));
	}

	/**
	 * 디렉토리전체삭제
	 *
	 * @param File file 디렉토리
	 * @return void
	 */
	public static boolean deleteDir(File file)
	{
		try {
			logger.debug("Delete Directory : {}", file.getPath());
			
			if (!file.exists()) {
				logger.info("Directory not exist - {}", file.getPath());
				return false;
			}

			File[] files = file.listFiles();

			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isDirectory())
				{
					deleteDir(files[i]);
				}
				else
				{
					fileDelete(files[i]);
				}
			}
			return file.delete();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	/**
	 * 디렉토리생성
	 *
	 * @param String dirPath 디렉토리경로
	 * @return void
	 */
	public static boolean makeFolder(String dirPath) throws SecurityException, NullPointerException
	{
		return makeFolder(new File(dirPath));
	}

	/**
	 * 디렉토리생성
	 *
	 * @param File file 디렉토리
	 * @return void
	 */
	public static boolean makeFolder(File dirPath) throws SecurityException, NullPointerException
	{
		boolean isMake = true;

		if(dirPath.isFile()) dirPath = new File(dirPath.getParent());
		if(!dirPath.exists()) isMake = dirPath.mkdirs();

		return isMake;
	}

	/**
	 * 전자문서 압축파일 해제
	 *
	 * @param String inPath 압축파일경로
	 * @param String outPath 압축해제파일경로
	 * @param String edocIdxNo 전자문서인덱스번호
	 * @return void
	 *
	 * @throws EdsException
	 */
	public static void unzip(String inPath, String outPath) throws Exception
	{
		ZipFile zipFile = null;
		ZipEntry zipEntry = null;
		File file = null;
		InputStream is = null;
		FileOutputStream fos = null;
		byte[] bytes = null;

		try
		{
			@SuppressWarnings("unused")
			boolean bRet = FileUtil.makeFolder(outPath);

			logger.info("inPath = " + inPath);
			zipFile = new ZipFile(inPath);

			Enumeration<?> enu = zipFile.entries();
			while(enu.hasMoreElements())
			{
				zipEntry = (ZipEntry)enu.nextElement();

				String name = zipEntry.getName();
				if("/".equals(File.separator))
					name = name.replace("\\", File.separator);
				else
					name = name.replace("/", File.separator);

				if(!outPath.endsWith(File.separator))
					outPath += File.separator;

				file = new File(outPath + name);
				if(name.endsWith(File.separator))
				{
					bRet = file.mkdirs();
					continue;
				}
				else
				{
					String parentDir = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator) + 1);
					File parent = new File(parentDir);
					bRet = parent.mkdirs();
					parent = null;
				}

				is = zipFile.getInputStream(zipEntry);
				fos = new FileOutputStream(file);

				bytes = new byte[1024];
				int length;
				while((length = is.read(bytes)) >= 0)
				{
					fos.write(bytes, 0, length);
				}

				is.close();
				fos.close();
			}

			zipFile.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
			zipFile = null;
			zipEntry = null;
			file = null;
			is = null;
			fos = null;
			bytes = null;
		}
	}

	public static int fileCopy(String srcPath, String destPath) throws FileNotFoundException
	{
		File srcFile = new File(srcPath);
		File destFile = new File(destPath);
		if(!srcFile.exists())
		{
			throw new FileNotFoundException(srcPath + " 파일이 존재하지 않습니다.");
		}

		if(srcFile.isFile()){
			copyFile(srcFile, destFile);
		}
		return 1;
	}

	private static void copyFile(File source, File dest)
	{
		int count = 0;
		byte[] b = new byte[128];

		FileInputStream in = null;
		FileOutputStream out = null;
		BufferedInputStream bin = null;
		BufferedOutputStream bout = null;
		try
		{
			in = new FileInputStream(source);
			bin = new BufferedInputStream(in);
			out = new FileOutputStream(dest);
			bout = new BufferedOutputStream(out);
			while((count = bin.read(b)) != -1)
			{
				bout.write(b,0, count);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(bout!= null) bout.close();
				if(out != null) out.close();
				if(bin != null) bin.close();
				if(in != null) in .close();
			}
			catch(IOException r)
			{
				// Nothing to do.
			}
		}
	}

	public static void zip(String sourcePath, String targetPath) throws Exception
	{
		File sourceFile = new File(sourcePath);
		if(!sourceFile.isFile() && !sourceFile.isDirectory())
			throw new Exception("압축 대상 파일이나 폴더를 찾을 수가 없습니다.");

		if(!StringUtils.substringAfterLast(targetPath, ".").equalsIgnoreCase("zip"))
			throw new Exception("압축 저장 파일명의 확장자가 \"zip\"이 아닙니다.");

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ZipOutputStream zos = null;

		try
		{
			fos = new FileOutputStream(targetPath);
			bos = new BufferedOutputStream(fos);
			zos = new ZipOutputStream(bos);
			zos.setLevel(8);	// 압축 레벨 : 최대 압축률은 9, 디폴트 8

			zipEntry(sourceFile, sourceFile.getPath(), zos);

			zos.finish();
		}
		finally
		{
			if(zos != null) zos.close();
			if(bos != null) bos.close();
			if(fos != null) fos.close();
		}
	}

	private static void zipEntry(File sourceFile, String basePath, ZipOutputStream zos) throws Exception
	{
		if(sourceFile.isDirectory())
		{
			if(sourceFile.getName().equalsIgnoreCase(".metadata"))
				return;

			File[] fileArray = sourceFile.listFiles();
			for(int i = 0; i < fileArray.length; i++)
				zipEntry(fileArray[i], basePath, zos);
		}
		else
		{
			BufferedInputStream bis = null;
			try
			{
				String sourceFilePath = sourceFile.getPath();
				String zipEntryName = sourceFilePath.substring(basePath.length() + 1);

				bis = new BufferedInputStream(new FileInputStream(sourceFile));
				ZipEntry zentry = new ZipEntry(zipEntryName);
				zentry.setTime(sourceFile.lastModified());
				zos.putNextEntry(zentry);

				byte[] buffer = new byte[2048];
				int cnt = 0;
				while((cnt = bis.read(buffer, 0, 2048)) > 0)
					zos.write(buffer, 0, cnt);
				zos.closeEntry();
			}
			finally
			{
				if(bis != null) bis.close();
			}
		}
	}

	/**
	 * 소스 폴더를 목적 폴더로 복사한다.
	 * 
	 * @param srcFolder
	 * @param dstFolder
	 * @return
	 * @throws SecurityException
	 * @throws IOException
	 * @throws Exception
	 */
	public static boolean folderCopy(String srcFolder, String dstFolder) throws SecurityException, IOException, Exception
	{
		boolean ret = false;

		File src = new File(srcFolder);
		if(!src.isDirectory()) throw new Exception("지정된 소스 폴더가 없습니다. srcFolder = " + srcFolder);

		File dst = new File(dstFolder);

		copyDirectory(src, dst);

		return ret;
	}

	/**
	 * 
	 * @param srcLocation
	 * @param dstDirectory
	 * @throws SecurityException
	 * @throws IOException
	 * @throws Exception
	 */
	public static void copyDirectory(File srcLocation, File dstDirectory) throws SecurityException, IOException, Exception
	{
		// 소스가 디렉토리인 경우
		if(srcLocation.isDirectory())
		{
			// 목적 directory가 없으면 생성
			if(!dstDirectory.exists()) dstDirectory.mkdir();

			String[] children = srcLocation.list();
			for(String child : children)
				copyDirectory(new File(srcLocation, child), new File(dstDirectory, child));
		}
		// 소스가 파일인 경우
		else
		{
			InputStream in = new FileInputStream(srcLocation);
			OutputStream out = new FileOutputStream(dstDirectory);

			// 파일 복사
			byte[] buf = new byte[1024];
			int len;
			while((len = in.read(buf)) > 0)
				out.write(buf, 0, len);

			in.close();
			out.close();
		}
	}

}
