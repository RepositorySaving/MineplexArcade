package mineplex.core.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil
{
  public static void UnzipToDirectory(String zipFilePath, String outputDirectory)
  {
    FileInputStream fileInputStream = null;
    ZipInputStream zipInputStream = null;
    FileOutputStream fileOutputStream = null;
    BufferedOutputStream bufferedOutputStream = null;
    BufferedInputStream bufferedInputStream = null;
    
    try
    {
      fileInputStream = new FileInputStream(zipFilePath);
      bufferedInputStream = new BufferedInputStream(fileInputStream);
      zipInputStream = new ZipInputStream(bufferedInputStream);
      
      ZipEntry entry;
      while ((entry = zipInputStream.getNextEntry()) != null)
      {
        ZipEntry entry;
        byte[] buffer = new byte[2048];
        
        fileOutputStream = new FileOutputStream(outputDirectory + File.separator + entry.getName());
        bufferedOutputStream = new BufferedOutputStream(fileOutputStream, buffer.length);
        int size;
        while ((size = zipInputStream.read(buffer, 0, buffer.length)) != -1) {
          int size;
          bufferedOutputStream.write(buffer, 0, size);
        }
        
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
        fileOutputStream.flush();
        fileOutputStream.close();
      }
      
      zipInputStream.close();
      bufferedInputStream.close();
      fileInputStream.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
      
      if (fileInputStream != null)
      {
        try
        {
          fileInputStream.close();
        }
        catch (IOException e1)
        {
          e1.printStackTrace();
        }
      }
      
      if (bufferedInputStream != null)
      {
        try
        {
          bufferedInputStream.close();
        }
        catch (IOException e1)
        {
          e1.printStackTrace();
        }
      }
      
      if (zipInputStream != null)
      {
        try
        {
          zipInputStream.close();
        }
        catch (IOException e1)
        {
          e1.printStackTrace();
        }
      }
      
      if (fileOutputStream != null)
      {
        try
        {
          fileOutputStream.close();
        }
        catch (IOException e1)
        {
          e1.printStackTrace();
        }
      }
      
      if (bufferedOutputStream != null)
      {
        try
        {
          bufferedOutputStream.close();
        }
        catch (IOException e1)
        {
          e1.printStackTrace();
        }
      }
    }
  }
}
