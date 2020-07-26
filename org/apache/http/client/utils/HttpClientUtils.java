package org.apache.http.client.utils;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.util.EntityUtils;
























































public class HttpClientUtils
{
  public static void closeQuietly(HttpResponse response)
  {
    if (response != null) {
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        try {
          EntityUtils.consume(entity);
        }
        catch (IOException ex) {}
      }
    }
  }
  




















  public static void closeQuietly(HttpClient httpClient)
  {
    if (httpClient != null) {
      httpClient.getConnectionManager().shutdown();
    }
  }
}
