package com.myself.everything.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

//配置类（哪些路径需要扫描 哪些路径不需要扫描）-> 配置需要扫描遍历的文件和不需要扫描遍历的文件 单例

@Getter   //不能修改引用，所以只提供getter方法!!!!
@ToString
public class EverythingConfig {
    private static volatile EverythingConfig config;

    //建立索引文件的路径（需要遍历扫描的路径）
    private Set<String> includePath = new HashSet<>();//路径不可能重复

    //排除索引文件的路径（不需要遍历扫描的路径）
    private Set<String> excludePath = new HashSet<>();

    //检索的最大的返回值数量
    @Setter
    private Integer maxReturnThingsRecord = 30;

    //深度排序的规则，默认是升序
    //order by dept asc limit 30 offset 0
    @Setter
    private Boolean deptOrderAsc = true;

    //H2数据库文件路径
    private String h2IndexPath = System.getProperty("user.dir") + File.separator + "everything";

    private EverythingConfig() {

    }

    //初始化默认路径配置
    private void initDefaultPathsConfig() {
        //1.获取文件系统      C:\    D:\    E:\
        //遍历的目录
        FileSystem fileSystem = FileSystems.getDefault();//获取电脑的文件系统 sun.nio.fs.WindowsFileSystem@4b67cf4d

        Iterable<Path> iterable = fileSystem.getRootDirectories();// [C:\, D:\, E:\]
        iterable.forEach(path -> config.getIncludePath().add(path.toString()));//将C:\  D:\  E:\加到要遍历的目录中
        //排除的目录
        //windows: C:\Windows  C:\Program Files (x86)   C:\Program Files   C:\ProgramData
        //linux:  /tmp   /etc
        //unix
        String osName = System.getProperty("os.name");//获取操作系统名
        if (osName.startsWith("Windows")) {//windows操作系统     //ctrl+d 复制行
            config.getExcludePath().add("C:\\Windows");
            config.getExcludePath().add("C:\\Program Files (x86)");
            config.getExcludePath().add("C:\\Program Files");
            config.getExcludePath().add("C:\\ProgramData");
        } else {//Linux操作系统
            config.getExcludePath().add("/tmp");
            config.getExcludePath().add("/etc");
            config.getExcludePath().add("/root");
        }
    }

    public static EverythingConfig getInstance() {//单例模式
        if (config == null) {
            synchronized (EverythingConfig.class) {
                if (config == null) {
                    config = new EverythingConfig();
                    config.initDefaultPathsConfig();
                }
            }
        }
        return config;


    }

}
