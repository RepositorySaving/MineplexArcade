package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.StringMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



















public final class ObjectTypeAdapter
  extends TypeAdapter<Object>
{
  public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory()
  {
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
      if (type.getRawType() == Object.class) {
        return new ObjectTypeAdapter(gson, null);
      }
      return null;
    }
  };
  private final Gson gson;
  
  private ObjectTypeAdapter(Gson gson)
  {
    this.gson = gson;
  }
  
  public Object read(JsonReader in) throws IOException {
    JsonToken token = in.peek();
    switch (2.$SwitchMap$com$google$gson$stream$JsonToken[token.ordinal()]) {
    case 1: 
      List<Object> list = new ArrayList();
      in.beginArray();
      while (in.hasNext()) {
        list.add(read(in));
      }
      in.endArray();
      return list;
    
    case 2: 
      Map<String, Object> map = new StringMap();
      in.beginObject();
      while (in.hasNext()) {
        map.put(in.nextName(), read(in));
      }
      in.endObject();
      return map;
    
    case 3: 
      return in.nextString();
    
    case 4: 
      return Double.valueOf(in.nextDouble());
    
    case 5: 
      return Boolean.valueOf(in.nextBoolean());
    
    case 6: 
      in.nextNull();
      return null;
    }
    
    throw new IllegalStateException();
  }
  
  public void write(JsonWriter out, Object value) throws IOException
  {
    if (value == null) {
      out.nullValue();
      return;
    }
    
    TypeAdapter<Object> typeAdapter = this.gson.getAdapter(value.getClass());
    if ((typeAdapter instanceof ObjectTypeAdapter)) {
      out.beginObject();
      out.endObject();
      return;
    }
    
    typeAdapter.write(out, value);
  }
}
