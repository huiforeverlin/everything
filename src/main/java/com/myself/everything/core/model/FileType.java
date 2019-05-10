package com.myself.everything.core.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

//文件类型
public enum FileType {

    IMG("png", "jpeg", "jpg", "gif"),
    DOC("ppt", "pptx", "doc", "docx", "pdf", "txt"),
    BIN("exe", "sh", "jar", "msi"),
    ARCHIVE("zip", "rar"),
    OTHER("*");

    //对应的文件类型的扩展名集合
    private Set<String> extend = new HashSet<>();

    FileType(String... extend) {
        this.extend.addAll(Arrays.asList(extend));
    }

    //根据文件扩展名获取文件类型   pdf -> DOC
    public static FileType lookup(String extend) {
        for (FileType fileType : FileType.values()) {
            if (fileType.extend.contains(extend)) {
                return fileType;
            }
        }
        return FileType.OTHER;
    }

    //根据文件类型名（String类型的文件类型名）获取文件类型对象  DOC(String) -> DOC (FileType)
    //数据库的file_index表中定义file_type为String类型，而我们需要的是枚举对象，所以需要转化一下
    public static FileType lookupByName(String name) {
        for (FileType fileType : FileType.values()) {
            if (fileType.name().equals(name)) {
                return fileType;
            }
        }
        return FileType.OTHER;
    }
}
