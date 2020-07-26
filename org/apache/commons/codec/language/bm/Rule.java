package org.apache.commons.codec.language.bm;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;






























































public class Rule
{
  public static final class Phoneme
    implements Rule.PhonemeExpr
  {
    public static final Comparator<Phoneme> COMPARATOR = new Comparator() {
      public int compare(Rule.Phoneme o1, Rule.Phoneme o2) {
        for (int i = 0; i < o1.phonemeText.length(); i++) {
          if (i >= o2.phonemeText.length()) {
            return 1;
          }
          int c = o1.phonemeText.charAt(i) - o2.phonemeText.charAt(i);
          if (c != 0) {
            return c;
          }
        }
        
        if (o1.phonemeText.length() < o2.phonemeText.length()) {
          return -1;
        }
        
        return 0;
      }
    };
    private final CharSequence phonemeText;
    private final Languages.LanguageSet languages;
    
    public Phoneme(CharSequence phonemeText, Languages.LanguageSet languages)
    {
      this.phonemeText = phonemeText;
      this.languages = languages;
    }
    
    public Phoneme append(CharSequence str) {
      return new Phoneme(this.phonemeText.toString() + str.toString(), this.languages);
    }
    
    public Languages.LanguageSet getLanguages() {
      return this.languages;
    }
    
    public Iterable<Phoneme> getPhonemes() {
      return Collections.singleton(this);
    }
    
    public CharSequence getPhonemeText() {
      return this.phonemeText;
    }
    
    public Phoneme join(Phoneme right) {
      return new Phoneme(this.phonemeText.toString() + right.phonemeText.toString(), this.languages.restrictTo(right.languages));
    }
  }
  
  public static abstract interface PhonemeExpr {
    public abstract Iterable<Rule.Phoneme> getPhonemes();
  }
  
  public static final class PhonemeList implements Rule.PhonemeExpr {
    private final List<Rule.Phoneme> phonemes;
    
    public PhonemeList(List<Rule.Phoneme> phonemes) {
      this.phonemes = phonemes;
    }
    
    public List<Rule.Phoneme> getPhonemes() {
      return this.phonemes;
    }
  }
  







  public static final RPattern ALL_STRINGS_RMATCHER = new RPattern() {
    public boolean isMatch(CharSequence input) {
      return true;
    }
  };
  
  public static final String ALL = "ALL";
  
  private static final String DOUBLE_QUOTE = "\"";
  
  private static final String HASH_INCLUDE = "#include";
  
  private static final Map<NameType, Map<RuleType, Map<String, List<Rule>>>> RULES = new EnumMap(NameType.class);
  private final RPattern lContext;
  private final String pattern;
  
  static { for (NameType s : NameType.values()) {
      Map<RuleType, Map<String, List<Rule>>> rts = new EnumMap(RuleType.class);
      
      for (RuleType rt : RuleType.values()) {
        Map<String, List<Rule>> rs = new HashMap();
        
        Languages ls = Languages.getInstance(s);
        for (String l : ls.getLanguages()) {
          try {
            rs.put(l, parseRules(createScanner(s, rt, l), createResourceName(s, rt, l)));
          } catch (IllegalStateException e) {
            throw new IllegalStateException("Problem processing " + createResourceName(s, rt, l), e);
          }
        }
        if (!rt.equals(RuleType.RULES)) {
          rs.put("common", parseRules(createScanner(s, rt, "common"), createResourceName(s, rt, "common")));
        }
        
        rts.put(rt, Collections.unmodifiableMap(rs));
      }
      
      RULES.put(s, Collections.unmodifiableMap(rts));
    }
  }
  
  private static boolean contains(CharSequence chars, char input) {
    for (int i = 0; i < chars.length(); i++) {
      if (chars.charAt(i) == input) {
        return true;
      }
    }
    return false;
  }
  
  private static String createResourceName(NameType nameType, RuleType rt, String lang) {
    return String.format("org/apache/commons/codec/language/bm/%s_%s_%s.txt", new Object[] { nameType.getName(), rt.getName(), lang });
  }
  
  private static Scanner createScanner(NameType nameType, RuleType rt, String lang) {
    String resName = createResourceName(nameType, rt, lang);
    InputStream rulesIS = Languages.class.getClassLoader().getResourceAsStream(resName);
    
    if (rulesIS == null) {
      throw new IllegalArgumentException("Unable to load resource: " + resName);
    }
    
    return new Scanner(rulesIS, "UTF-8");
  }
  
  private static Scanner createScanner(String lang) {
    String resName = String.format("org/apache/commons/codec/language/bm/%s.txt", new Object[] { lang });
    InputStream rulesIS = Languages.class.getClassLoader().getResourceAsStream(resName);
    
    if (rulesIS == null) {
      throw new IllegalArgumentException("Unable to load resource: " + resName);
    }
    
    return new Scanner(rulesIS, "UTF-8");
  }
  
  private static boolean endsWith(CharSequence input, CharSequence suffix) {
    if (suffix.length() > input.length()) {
      return false;
    }
    int i = input.length() - 1; for (int j = suffix.length() - 1; j >= 0; j--) {
      if (input.charAt(i) != suffix.charAt(j)) {
        return false;
      }
      i--;
    }
    


    return true;
  }
  










  public static List<Rule> getInstance(NameType nameType, RuleType rt, Languages.LanguageSet langs)
  {
    return langs.isSingleton() ? getInstance(nameType, rt, langs.getAny()) : getInstance(nameType, rt, "any");
  }
  










  public static List<Rule> getInstance(NameType nameType, RuleType rt, String lang)
  {
    List<Rule> rules = (List)((Map)((Map)RULES.get(nameType)).get(rt)).get(lang);
    
    if (rules == null) {
      throw new IllegalArgumentException(String.format("No rules found for %s, %s, %s.", new Object[] { nameType.getName(), rt.getName(), lang }));
    }
    
    return rules;
  }
  
  private static Phoneme parsePhoneme(String ph) {
    int open = ph.indexOf("[");
    if (open >= 0) {
      if (!ph.endsWith("]")) {
        throw new IllegalArgumentException("Phoneme expression contains a '[' but does not end in ']'");
      }
      String before = ph.substring(0, open);
      String in = ph.substring(open + 1, ph.length() - 1);
      Set<String> langs = new HashSet(Arrays.asList(in.split("[+]")));
      
      return new Phoneme(before, Languages.LanguageSet.from(langs));
    }
    return new Phoneme(ph, Languages.ANY_LANGUAGE);
  }
  
  private static PhonemeExpr parsePhonemeExpr(String ph)
  {
    if (ph.startsWith("(")) {
      if (!ph.endsWith(")")) {
        throw new IllegalArgumentException("Phoneme starts with '(' so must end with ')'");
      }
      
      List<Phoneme> phs = new ArrayList();
      String body = ph.substring(1, ph.length() - 1);
      for (String part : body.split("[|]")) {
        phs.add(parsePhoneme(part));
      }
      if ((body.startsWith("|")) || (body.endsWith("|"))) {
        phs.add(new Phoneme("", Languages.ANY_LANGUAGE));
      }
      
      return new PhonemeList(phs);
    }
    return parsePhoneme(ph);
  }
  
  private static List<Rule> parseRules(Scanner scanner, final String location)
  {
    List<Rule> lines = new ArrayList();
    int currentLine = 0;
    
    boolean inMultilineComment = false;
    while (scanner.hasNextLine()) {
      currentLine++;
      String rawLine = scanner.nextLine();
      String line = rawLine;
      
      if (inMultilineComment) {
        if (line.endsWith("*/")) {
          inMultilineComment = false;
        }
        

      }
      else if (line.startsWith("/*")) {
        inMultilineComment = true;
      }
      else {
        int cmtI = line.indexOf("//");
        if (cmtI >= 0) {
          line = line.substring(0, cmtI);
        }
        

        line = line.trim();
        
        if (line.length() != 0)
        {


          if (line.startsWith("#include"))
          {
            String incl = line.substring("#include".length()).trim();
            if (incl.contains(" ")) {
              System.err.println("Warining: malformed import statement: " + rawLine);
            } else {
              lines.addAll(parseRules(createScanner(incl), location + "->" + incl));
            }
          }
          else {
            String[] parts = line.split("\\s+");
            if (parts.length != 4) {
              System.err.println("Warning: malformed rule statement split into " + parts.length + " parts: " + rawLine);
            } else {
              try {
                String pat = stripQuotes(parts[0]);
                String lCon = stripQuotes(parts[1]);
                String rCon = stripQuotes(parts[2]);
                PhonemeExpr ph = parsePhonemeExpr(stripQuotes(parts[3]));
                final int cLine = currentLine;
                Rule r = new Rule(pat, lCon, rCon, ph) {
                  private final int myLine = cLine;
                  private final String loc = location;
                  
                  public String toString()
                  {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Rule");
                    sb.append("{line=").append(this.myLine);
                    sb.append(", loc='").append(this.loc).append('\'');
                    sb.append('}');
                    return sb.toString();
                  }
                };
                lines.add(r);
              } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Problem parsing line " + currentLine, e);
              }
            }
          }
        }
      }
    }
    
    return lines;
  }
  






  private static RPattern pattern(String regex)
  {
    boolean startsWith = regex.startsWith("^");
    boolean endsWith = regex.endsWith("$");
    String content = regex.substring(startsWith ? 1 : 0, endsWith ? regex.length() - 1 : regex.length());
    boolean boxes = content.contains("[");
    
    if (!boxes) {
      if ((startsWith) && (endsWith))
      {
        if (content.length() == 0)
        {
          new RPattern() {
            public boolean isMatch(CharSequence input) {
              return input.length() == 0;
            }
          };
        }
        new RPattern() {
          public boolean isMatch(CharSequence input) {
            return input.equals(this.val$content);
          }
        };
      }
      if (((startsWith) || (endsWith)) && (content.length() == 0))
      {
        return ALL_STRINGS_RMATCHER; }
      if (startsWith)
      {
        new RPattern() {
          public boolean isMatch(CharSequence input) {
            return Rule.startsWith(input, this.val$content);
          }
        }; }
      if (endsWith)
      {
        new RPattern() {
          public boolean isMatch(CharSequence input) {
            return Rule.endsWith(input, this.val$content);
          }
        };
      }
    } else {
      boolean startsWithBox = content.startsWith("[");
      boolean endsWithBox = content.endsWith("]");
      
      if ((startsWithBox) && (endsWithBox)) {
        String boxContent = content.substring(1, content.length() - 1);
        if (!boxContent.contains("["))
        {
          boolean negate = boxContent.startsWith("^");
          if (negate) {
            boxContent = boxContent.substring(1);
          }
          String bContent = boxContent;
          final boolean shouldMatch = !negate;
          
          if ((startsWith) && (endsWith))
          {
            new RPattern() {
              public boolean isMatch(CharSequence input) {
                return (input.length() == 1) && (Rule.contains(this.val$bContent, input.charAt(0)) == shouldMatch);
              }
            }; }
          if (startsWith)
          {
            new RPattern() {
              public boolean isMatch(CharSequence input) {
                return (input.length() > 0) && (Rule.contains(this.val$bContent, input.charAt(0)) == shouldMatch);
              }
            }; }
          if (endsWith)
          {
            new RPattern() {
              public boolean isMatch(CharSequence input) {
                return (input.length() > 0) && (Rule.contains(this.val$bContent, input.charAt(input.length() - 1)) == shouldMatch);
              }
            };
          }
        }
      }
    }
    

    new RPattern() {
      Pattern pattern = Pattern.compile(this.val$regex);
      
      public boolean isMatch(CharSequence input) {
        Matcher matcher = this.pattern.matcher(input);
        return matcher.find();
      }
    };
  }
  
  private static boolean startsWith(CharSequence input, CharSequence prefix) {
    if (prefix.length() > input.length()) {
      return false;
    }
    for (int i = 0; i < prefix.length(); i++) {
      if (input.charAt(i) != prefix.charAt(i)) {
        return false;
      }
    }
    return true;
  }
  
  private static String stripQuotes(String str) {
    if (str.startsWith("\"")) {
      str = str.substring(1);
    }
    
    if (str.endsWith("\"")) {
      str = str.substring(0, str.length() - 1);
    }
    
    return str;
  }
  






  private final PhonemeExpr phoneme;
  





  private final RPattern rContext;
  




  public Rule(String pattern, String lContext, String rContext, PhonemeExpr phoneme)
  {
    this.pattern = pattern;
    this.lContext = pattern(lContext + "$");
    this.rContext = pattern("^" + rContext);
    this.phoneme = phoneme;
  }
  




  public RPattern getLContext()
  {
    return this.lContext;
  }
  




  public String getPattern()
  {
    return this.pattern;
  }
  




  public PhonemeExpr getPhoneme()
  {
    return this.phoneme;
  }
  




  public RPattern getRContext()
  {
    return this.rContext;
  }
  










  public boolean patternAndContextMatches(CharSequence input, int i)
  {
    if (i < 0) {
      throw new IndexOutOfBoundsException("Can not match pattern at negative indexes");
    }
    
    int patternLength = this.pattern.length();
    int ipl = i + patternLength;
    
    if (ipl > input.length())
    {
      return false;
    }
    



    boolean patternMatches = input.subSequence(i, ipl).equals(this.pattern);
    boolean rContextMatches = this.rContext.isMatch(input.subSequence(ipl, input.length()));
    boolean lContextMatches = this.lContext.isMatch(input.subSequence(0, i));
    
    return (patternMatches) && (rContextMatches) && (lContextMatches);
  }
  
  public static abstract interface RPattern
  {
    public abstract boolean isMatch(CharSequence paramCharSequence);
  }
}
