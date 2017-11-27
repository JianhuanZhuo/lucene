package cn.keepfight;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;

public class AnalyzerUtils {
    public static void displayToken(String str, Analyzer a) throws IOException {
        TokenStream stream = a.tokenStream("content", new StringReader(str));
        // 创建一个属性，这个属性会添加到流中，随着这个 TokenStream 增加而变化
        CharTermAttribute cta = stream.addAttribute(CharTermAttribute.class);
        while (stream.incrementToken()){
            System.out.println("["+cta+"]");
        }
        System.out.println() ;
    }
}
