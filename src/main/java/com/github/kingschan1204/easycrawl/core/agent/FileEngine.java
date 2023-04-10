package com.github.kingschan1204.easycrawl.core.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FileEngine extends HttpEngine{
    //下载文件上的时候才有作用
    private String folder;
    private String fileName;
    public final String CONTENT_DISPOSITION ="Content-disposition";
}
