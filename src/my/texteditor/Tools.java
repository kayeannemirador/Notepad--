package my.texteditor;

public class Tools {

    static int on = 1;
    static int off = 0;

    public static int wordCount(String str) {
        int status = on;
        int word = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ' ' || str.charAt(i) == '\n' || str.charAt(i) == '\t') {
                status = on;
            } else if (status == on) {
                status = off;
                word++;
            }
        }
        return word;
    }
    
     public static int characterCount(String str) {
        int chr = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ' ' || str.charAt(i) == '\n' || str.charAt(i) == '\t') {
           
            } else {
               
                chr++;
            }
        }
        return chr;
     }
}
        /*if (str.isBlank()) 
         return 0; 
        
        int count = 0;
        String[] words = str.trim().split("");
        
        for (String word : words)
            if (!word.isBlank())
                count++;
        
        return count;
    }*/

