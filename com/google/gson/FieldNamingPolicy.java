package com.google.gson;

import java.lang.reflect.Field;





























public enum FieldNamingPolicy
  implements FieldNamingStrategy
{
  IDENTITY, 
  














  UPPER_CAMEL_CASE, 
  

















  UPPER_CAMEL_CASE_WITH_SPACES, 
  
















  LOWER_CASE_WITH_UNDERSCORES, 
  





















  LOWER_CASE_WITH_DASHES;
  



  private FieldNamingPolicy() {}
  


  private static String separateCamelCase(String name, String separator)
  {
    StringBuilder translation = new StringBuilder();
    for (int i = 0; i < name.length(); i++) {
      char character = name.charAt(i);
      if ((Character.isUpperCase(character)) && (translation.length() != 0)) {
        translation.append(separator);
      }
      translation.append(character);
    }
    return translation.toString();
  }
  


  private static String upperCaseFirstLetter(String name)
  {
    StringBuilder fieldNameBuilder = new StringBuilder();
    int index = 0;
    char firstCharacter = name.charAt(index);
    
    while ((index < name.length() - 1) && 
      (!Character.isLetter(firstCharacter)))
    {


      fieldNameBuilder.append(firstCharacter);
      firstCharacter = name.charAt(++index);
    }
    
    if (index == name.length()) {
      return fieldNameBuilder.toString();
    }
    
    if (!Character.isUpperCase(firstCharacter)) {
      String modifiedTarget = modifyString(Character.toUpperCase(firstCharacter), name, ++index);
      return modifiedTarget;
    }
    return name;
  }
  
  private static String modifyString(char firstCharacter, String srcString, int indexOfSubstring)
  {
    return indexOfSubstring < srcString.length() ? firstCharacter + srcString.substring(indexOfSubstring) : String.valueOf(firstCharacter);
  }
}
