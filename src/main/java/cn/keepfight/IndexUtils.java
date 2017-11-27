package cn.keepfight;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class IndexUtils {



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
}
