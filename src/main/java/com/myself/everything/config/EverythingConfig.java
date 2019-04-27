package com.myself.everything.config;

import lombok.Getter;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Getter   //不能修改引用，所以只提供getter方法
public class EverythingConfig {
    private static volatile EverythingConfig config;


    //建立索引的路径
    private Set<String> includePath = new HashSet<>();//路径不可能重复

    //排除索引文件的路径
    private Set<String> excludePath = new HashSet<>();

    private EverythingConfig() {
    }

    public static EverythingConfig getInstance() {
        if (config == null) {
            synchronized (EverythingConfig.class) {
                if (config == null) {
                    config = new EverythingConfig();
                    //1.获取文件系统      C:\    D:\    E:\
                    //遍历的目录
                    FileSystem fileSystem = FileSystems.getDefault();
                    Iterable<Path> iterable = fileSystem.getRootDirectories();
                    iterable.forEach(path -> config.getIncludePath().add(path.toString()));
                    //排除的目录
                    //windows: C:\Windows  C:\Program Files (x86)   C:\Program Files   C:\ProgramData
                    //linux:  /tmp /etc
                    String osName=System.getProperty("os.name");
                    if(osName.startsWith("Windows")){     //ctrl+d 复制行
                        config.getExcludePath().add("C:\\Windows");
                        config.getExcludePath().add("C:\\Program Files (x86)");
                        config.getExcludePath().add("C:\\Program Files");
                        config.getExcludePath().add("C:\\ProgramData");
                    }else {
                        config.getExcludePath().add("/tmp");
                        config.getExcludePath().add("/etc");
                        config.getExcludePath().add("/root");

                    }
                }
            }
        }
        return config;
    }

//    public static void main(String[] args) {
//        FileSystem fileSystem = FileSystems.getDefault();
//        Iterable<Path> iterable = fileSystem.getRootDirectories();
//        iterable.forEach(new Consumer<Path>() {
//            @Override
//            public void accept(Path path) {
//                System.out.println(path);
//            }
//            /*
//            * C:\
//              D:\
//              E:\
//
//            * */
//        });

        //System.out.println();


//        EverythingConfig config=EverythingConfig.getInstance();
//        System.out.println(config.includePath);
//        System.out.println(config.excludePath);
//    }
}
