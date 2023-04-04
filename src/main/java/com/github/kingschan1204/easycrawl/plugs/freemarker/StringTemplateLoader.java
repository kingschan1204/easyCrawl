package com.github.kingschan1204.easycrawl.plugs.freemarker;

import freemarker.cache.TemplateLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 *
 * @author kings.chan
 * @create 2020-02-13 12:15
 **/
public class StringTemplateLoader implements TemplateLoader {
    private String template;

    public StringTemplateLoader(String template) {
        this.template = template;
        if (null == template) {
            this.template = "";
        }
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
        ((StringReader) templateSource).close();
    }

    @Override
    public Object findTemplateSource(String name) throws IOException {
        return new StringReader(template);
    }

    @Override
    public long getLastModified(Object templateSource) {
        return 0;
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        return (Reader) templateSource;
    }


}
