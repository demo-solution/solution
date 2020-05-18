package com.spar.hcl.core.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/***
 * This utility compresses a list of files to standard ZIP format file.* It is able to compresses all sub files and sub
 * directories, recursively.
 **/

public class ZipUtil
{

	/** * A constants for buffer size used to read/write data */
	private static final int BUFFER_SIZE = 4096;

	/**
	 * * Compresses a collection of files to a destination zip file.
	 * 
	 * @param listFiles
	 *           A collection of files and directories
	 * @param destZipFile
	 *           The path of the destination zip file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void compressFiles(final List<File> listFiles, final String destZipFile) throws FileNotFoundException, IOException
	{
		final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destZipFile));
		for (final File file : listFiles)
		{
			if (file.isDirectory())
			{
				addFolderToZip(file, file.getName(), zos);
			}
			else
			{
				addFileToZip(file, zos);
			}
		}
		zos.flush();
		zos.close();
	}

	/**
	 * * Adds a directory to the current zip output stream
	 *
	 * @param folder
	 *           the directory to be added
	 * @param parentFolder
	 *           the path of parent directory
	 * @param zos
	 *           the current zip output stream
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void addFolderToZip(final File folder, final String parentFolder, final ZipOutputStream zos)
			throws FileNotFoundException, IOException
	{
		for (final File file : folder.listFiles())
		{
			if (file.isDirectory())
			{
				addFolderToZip(file, parentFolder + "/" + file.getName(), zos);
				continue;
			}
			zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));
			final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			//long bytesRead = 0;
			final byte[] bytesIn = new byte[BUFFER_SIZE];
			int read = 0;
			while ((read = bis.read(bytesIn)) != -1)
			{
				zos.write(bytesIn, 0, read);
				//bytesRead += read;
			}
			bis.close();
			zos.closeEntry();
		}
	}

	/**
	 * * Adds a file to the current zip output stream
	 * 
	 * @param file
	 *           the file to be added
	 * @param zos
	 *           the current zip output stream
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void addFileToZip(final File file, final ZipOutputStream zos) throws FileNotFoundException, IOException
	{
		zos.putNextEntry(new ZipEntry(file.getName()));
		final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		//long bytesRead = 0;
		final byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = bis.read(bytesIn)) != -1)
		{
			zos.write(bytesIn, 0, read);
			//bytesRead += read;
		}
		bis.close();
		zos.closeEntry();
	}
}