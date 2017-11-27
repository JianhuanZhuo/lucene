package cn.keepfight;

import org.apache.lucene.analysis.*;
import org.apache.lucene.util.Version;

import java.io.Reader;
import java.util.Set;

public class MyStopAnalyzer extends Analyzer {
    private Set<?> stops;

    public MyStopAnalyzer(String[] sws) {
        stops = StopFilter.makeStopSet(Version.LUCENE_35, sws, true);
    }
    public MyStopAnalyzer() {
        // 可以通过 ENGLISH_STOP_WORDS_SET 获取原本的 StopSet
        stops = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
    }

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        return new StopFilter(Version.LUCENE_35,
                new LowerCaseFilter(Version.LUCENE_35,
                        new LetterTokenizer(Version.LUCENE_35, reader))
                , stops);
    }
}
