package com.google.gson;

public abstract interface ExclusionStrategy
{
  public abstract boolean shouldSkipField(FieldAttributes paramFieldAttributes);
  
  public abstract boolean shouldSkipClass(Class<?> paramClass);
}
