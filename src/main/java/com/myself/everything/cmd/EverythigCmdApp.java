package com.myself.everything.cmd;

import com.myself.everything.config.EverythingConfig;
import com.myself.everything.core.EverythingManager;
import com.myself.everything.core.model.Condition;
import com.myself.everything.core.model.Thing;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class EverythigCmdApp {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        //解析用户参数
        parseParams(args);
//        System.out.println(EverythingConfig.getInstance());


        //欢迎
        welcome();

        //统一调度器
        EverythingManager manager = EverythingManager.getInstance();

        //启动后台清理线程
        manager.startBackgroundClearThread();

        //交互式
        interactive(manager);
    }

    private static void parseParams(String[] args) {
        EverythingConfig config = EverythingConfig.getInstance();
        for (String param : args) {
            String maxReturnParam = "--maxReturnThingsRecord=";

            if (param.startsWith(maxReturnParam)) {
                //处理参数：如果用户输入的参数格式不对，使用默认值即可
                //--maxReturnThingsRecord=value
                int index = param.indexOf("=");

                String maxReturnStr = param.substring(index + 1);
                try {
                    int maxReturn = Integer.parseInt(maxReturnStr);
                    config.setMaxReturnThingsRecord(maxReturn);
                } catch (NumberFormatException e) {
                    //如果用户输入的参数格式不对，使用默认值即可
                }

            }

            String deptOrderAscParam = "--deptOrderAsc=";
            if (param.startsWith(deptOrderAscParam)) {
                //--deptOrderAsc=value
                int index = param.indexOf("=");
                String deptOrderAsc = param.substring(index + 1);
                config.setDeptOrderAsc(Boolean.parseBoolean(deptOrderAsc));
            }

            String includePathParam = "--includePath=";
            if (param.startsWith(includePathParam)) {
                //--includePath=value
                int index = param.indexOf("=");
                String includePath = param.substring(index + 1);

                String[] includePaths = includePath.split(";");
                if (includePaths.length > 0) {
                    config.getIncludePath().clear();
                }
                for (String p : includePaths) {
                    config.getIncludePath().add(p);
                }
            }

            String excludePathParam = "--excludePath=";
            if (param.startsWith(excludePathParam)) {
                //--excludePath=value
                int index = param.indexOf("=");
                String excludePath = param.substring(index + 1);
                    String[] excludePaths = excludePath.split(";");
                    config.getExcludePath().clear();
                    for (String p : excludePaths) {
                        config.getExcludePath().add(p);
                    }
            }
        }
    }

    private static void interactive(EverythingManager manager) {
        while (true) {
            System.out.print("everything >>");
            String input = scanner.nextLine();
            //优先处理search
            if (input.startsWith("search")) {
                //search name [file_type]
                String[] values = input.split(" ");
                if (values.length >= 2) {
                    if (!values[0].equals("search")) {
                        help();
                        continue;
                    }
                    Condition condition = new Condition();
                    String name = values[1];
                    condition.setName(name);
                    if (values.length >= 3) {
                        String fileType = values[2];
                        condition.setFileType(fileType.toUpperCase());
                    }
                    search(manager, condition);
                    continue;
                } else {
                    help();
                    continue;
                }
            }
            switch (input) {//JDK1.6中String不能作为switch的参数
                case "help":
                    help();
                    break;
                case "quit":
                    quit();
                    break;
                case "index":
                    index(manager);
                    break;
                default:
                    help();
            }
        }
    }


    private static void search(EverythingManager manager, Condition condition) {
//        System.out.println("检索功能");
        //统一调度器中的search
        //name fileType limit orderByAsc
        condition.setLimit(EverythingConfig.getInstance().getMaxReturnThingsRecord());
        condition.setOrderByAsc(EverythingConfig.getInstance().getDeptOrderAsc());
        List<Thing> thingList = manager.search(condition);
        for (Thing thing : thingList) {//输出到控制台
            System.out.println(thing.getPath());
        }
    }

    private static void index(EverythingManager manager) {
        //统一调度器中的index
//        new Thread(() -> manager.buildIndex()).start();
        new Thread(manager::buildIndex).start();
    }

    private static void quit() {
        System.out.println("再见");
        System.exit(0);
    }

    private static void welcome() {
        System.out.println("欢迎使用Everything");
    }

    private static void help() {
        System.out.println("命令列表：");
        System.out.println("退出：quit");
        System.out.println("帮助：help");
        System.out.println("索引：index");
        System.out.println("搜索：search <name> [<file-Type> img | doc | bin | archive | other]");
    }
}
