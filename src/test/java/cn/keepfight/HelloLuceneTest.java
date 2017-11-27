package cn.keepfight;

import junit.framework.TestCase;

public class HelloLuceneTest extends TestCase {
    public void testIndex() throws Exception {
        new HelloLucene().index();
    }

    public void testSearch() throws Exception{
        new HelloLucene().searcher();
    }
}