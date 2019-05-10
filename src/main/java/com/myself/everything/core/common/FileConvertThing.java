package com.myself.everything.core.common;

import com.myself.everything.core.model.FileType;
import com.myself.everything.core.model.Thing;

import java.io.File;


//辅助工具类：将File对象转换成Thing对象（不希望被实例化）
public final class FileConvertThing {
    private FileConvertThing() {
    }

    public static Thing convert(File file) {//转换函数：File对象 -> Thing对象
        Thing thing = new Thing();
        thing.setName(file.getName());
        thing.setPath(file.getAbsolutePath());
        thing.setDepth(computerFileDepth(file));
        thing.setFileType(computerFileType(file));
        return thing;
    }

    private static int computerFileDepth(File file) {//计算文件深度函数
        String[] segments = file.getAbsolutePath().split("\\\\");//获取文件的绝对路径并分隔
        return segments.length-1;
    }

    private static FileType computerFileType(File file) {//利用获取到的扩展名计算文件类型
        if (file.isDirectory()) {//目录不存在文件类型
            return FileType.OTHER;
        }
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");//获取扩展名
        if (index != -1 && index < fileName.length() - 1) { // 防止此类情况出现 -> abc.，最后一个.后面没有扩展名
            String extend = fileName.substring(index + 1);
            return FileType.lookup(extend);
        } else {
            return FileType.OTHER;
        }
    }
}
