package com.myself.everything.core.common;

import com.myself.everything.core.model.FileType;
import com.myself.everything.core.model.Thing;

import java.io.File;


//辅助工具类：将File对象转换成Thing对象
public final class FileConvertThing {
    private FileConvertThing() {
    }

    public static Thing convert(File file) {
        Thing thing = new Thing();
        thing.setName(file.getName());
        thing.setPath(file.getAbsolutePath());
        thing.setDepth(computerFileDepth(file));
        thing.setFileType(computerFileType(file));
        return thing;
    }

    private static int computerFileDepth(File file) {
        int depth = 0;
        String[] segments = file.getAbsolutePath().split("\\\\");
        depth = segments.length-1;
        return depth;
    }

    private static FileType computerFileType(File file) {
        if (file.isDirectory()) {
            return FileType.OTHER;
        }
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");
        if (index != -1 && index < fileName.length() - 1) {
            //abc.
            String extend = fileName.substring(index + 1);
            return FileType.lookup(extend);
        } else {
            return FileType.OTHER;
        }
    }
}
