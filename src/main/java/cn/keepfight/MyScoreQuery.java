package cn.keepfight;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.function.CustomScoreProvider;
import org.apache.lucene.search.function.CustomScoreQuery;
import org.apache.lucene.search.function.FieldScoreQuery;
import org.apache.lucene.search.function.ValueSourceQuery;

import java.io.IOException;

public class MyScoreQuery {

    public void searchByScoreQuery() throws IOException {
        try (IndexSearcher searcher = SearchUtils.getSearcher()) {
            Query q = new TermQuery(new Term("content", "xx"));
            //创建一个评分域
            FieldScoreQuery fd = new FieldScoreQuery("score", FieldScoreQuery.Type.INT);
            MyCustomScoreQuery query = new MyCustomScoreQuery(q, fd);


        }
    }

    private class MyCustomScoreQuery extends CustomScoreQuery {

        public MyCustomScoreQuery(Query subQuery, ValueSourceQuery valSrcQuery) {
            super(subQuery, valSrcQuery);
        }



        @Override
        protected CustomScoreProvider getCustomScoreProvider(IndexReader reader) throws IOException {

            /**
             *
             */

            // 默认是直接 乘法，原有的评分 * 传入的评分
//            return super.getCustomScoreProvider(reader);

            // 使用自定义的评分
            return new MyCustomScoreProvider(reader);

        }

        private class MyCustomScoreProvider extends CustomScoreProvider{
            /**
             * Creates a new instance of the provider class for the given {@link IndexReader}.
             *
             * @param reader
             */
            public MyCustomScoreProvider(IndexReader reader) {
                super(reader);
            }

            /**
             * @param doc
             * @param subQueryScore 默认文档的评分
             * @param valSrcScore 评分域的评分
             */
            @Override
            public float customScore(int doc, float subQueryScore, float valSrcScore) throws IOException {
                return super.customScore(doc, subQueryScore, valSrcScore);
            }
        }
    }


    private class FilenameScoreProvider extends CustomScoreProvider{
        String[] filenames = null;
        public FilenameScoreProvider(IndexReader reader) throws IOException {
            super(reader);
            // 通过域缓存获取数据
            filenames = FieldCache.DEFAULT.getStrings(reader, "filename");
        }

        @Override
        public float customScore(int doc, float subQueryScore, float[] valSrcScores) throws IOException {
           String currentFileName = filenames[doc];
           if (currentFileName.endsWith(".text")){
               return subQueryScore*1.5f;
           }
           return subQueryScore;
        }
    }
}
