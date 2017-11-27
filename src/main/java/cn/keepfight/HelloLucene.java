package cn.keepfight;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class HelloLucene {
    private static Directory directory;
    private static IndexReader reader;

    static {
        try {
            directory =  FSDirectory.open(new File("F:\\project\\index"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 单例模式下的获得搜索器
     */
    public static IndexSearcher getSearcher() throws IOException {
        if (reader == null) {
            reader = IndexReader.open(directory);
        } else {
            IndexReader newReader = IndexReader.openIfChanged(reader);
            // 原先的 reader 未关闭，需先关闭原先的 reader
            reader.close();
            reader = newReader;
        }
        return new IndexSearcher(reader);
    }

    /**
     * 建立索引
     */
    public void index() throws IOException {
        // 1. 创建Directory
//        Directory directory = new RAMDirectory();
        Directory directory = FSDirectory.open(new File("F:\\project\\index"));

        // 2. 创建 IndexWriter
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35));
        try (IndexWriter writer = new IndexWriter(directory, config)) {
            File folder = new File("F:\\project\\lucene");
            for (File f : folder.listFiles()) {
                // 3. 创建 Document 对象，也就是每个文档对应一个 Document
                Document doc = new Document();
                doc.add(new Field("content", new FileReader(f)));
                doc.add(new Field("filename", f.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
                doc.add(new Field("path", f.getAbsolutePath(), Field.Store.YES, Field.Index.NOT_ANALYZED));
                // 使用 setBoost 设置权重
                // doc.setBoost(2.0f);

                // 使用 NumericField 添加数值型域
                // doc.add(new NumericField("id").setIntValue(1));
                writer.addDocument(doc);
            }
        }
    }

    public void delete() throws Exception {
        // 1. 创建Directory
        Directory directory = FSDirectory.open(new File("F:\\project\\index"));
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35));
        try (IndexWriter writer = new IndexWriter(directory, config)) {
//            writer.deleteAll();
            // 删除 ID 为 1 索引得到的文档
            writer.deleteDocuments(new Term("id", "1"));

            // 提交修改操作，这样不需要等到关闭 writer 才发生更改
            writer.commit();

            // 清空回收站
            // writer.forceMergeDeletes();
        }
    }


    public void deleteByReader()throws Exception{
        reader.deleteDocuments(new Term("id", "1"));
        reader.close();

    }

    public void undelete() throws Exception {
        //1. 创建 Directory
        Directory directory = FSDirectory.open(new File("F:\\project\\index"));
        //2. 创建 IndexReader
        // 这里需要设置 ReadOnly 标志位为 true，才可以修改文件
        try (IndexReader reader = IndexReader.open(directory, false)) {
            // Reader 可以读取一些信息
            System.out.println("最大文档数：" + reader.maxDoc());
            System.out.println("当前文档数：" + reader.numDocs());
            System.out.println("删除文档数：" + reader.numDeletedDocs());

            // 恢复出来
            reader.undeleteAll();
        }
    }

    public void searcher() throws Exception {
        //1. 创建 Directory
        Directory directory = FSDirectory.open(new File("F:\\project\\index"));
        //2. 创建 IndexReader
        try (IndexReader reader = IndexReader.open(directory)) {
            // Reader 可以读取一些信息
            System.out.println("当前文档数：" + reader.maxDoc());
            System.out.println("当前文档数：" + reader.numDocs());
            //3. 根据 IndexReader 创建 IndexSearch
            IndexSearcher searcher = new IndexSearcher(reader);
            //4. 创建搜索的Query
            QueryParser parser = new QueryParser(Version.LUCENE_35, "content", new StandardAnalyzer(Version.LUCENE_35));
            // 表示搜索 content 域中 包含 java  的文档
            Query query = parser.parse("卖房");
            //5. 根据 searcher 搜索并返回 TopDocs 对象
            TopDocs tds = searcher.search(query, 10);
            //6. 根据 TopDocs 获取 ScoreDoc 对象
            ScoreDoc[] sds = tds.scoreDocs;
            for (ScoreDoc sd : sds) {
                //7. 根据 seacher 和 ScordDoc 对象获取具体的 Docment 对象
                Document d = searcher.doc(sd.doc);
                // 根据 Document 对象获取需要的值
                System.out.println(d.get("filename") + "-[" + d.get("path") + "]");
            }
        }

    }
}
