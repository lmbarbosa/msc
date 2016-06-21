/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ArticleExtraction;

/**
 *
 * @author leo
 */
public class ClassBiggestFont {
    
     // Document biggest Font Size BaseFont
    static String charBiggestFontSizeBase = null;
    // Document biggest Font Size YDirAjd
    static float charBiggestFontSizeYLimit = 0;
    // Document biggest Font Size
    static float charBiggestFontSize = 0;
     // Document biggest Font Size Ocurrence
    static int charBiggestFontSizeOcurrence = 0;

    public static float getCharBiggestFontSizeYLimit() {
        return charBiggestFontSizeYLimit;
    }

    public static void setCharBiggestFontSizeYLimit(float charBiggestFontSizeYLimit) {
        ClassBiggestFont.charBiggestFontSizeYLimit = charBiggestFontSizeYLimit;
    }

    public static String getCharBiggestFontSizeBase() {
        return charBiggestFontSizeBase;
    }

    public static void setCharBiggestFontSizeBase(String charBiggestFontSizeBase) {
        ClassBiggestFont.charBiggestFontSizeBase = charBiggestFontSizeBase;
    }

    public static float getCharBiggestFontSize() {
        return charBiggestFontSize;
    }

    public static void setCharBiggestFontSize(float charBiggestFontSize) {
        ClassBiggestFont.charBiggestFontSize = charBiggestFontSize;
    }

    public static int getCharBiggestFontSizeOcurrence() {
        return charBiggestFontSizeOcurrence;
    }

    public static void setCharBiggestFontSizeOcurrence(int charBiggestFontSizeOcurrence) {
        ClassBiggestFont.charBiggestFontSizeOcurrence = charBiggestFontSizeOcurrence;
    }
    
   

    
}
