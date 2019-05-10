package com.myself.everything.cmd;

import com.myself.everything.config.EverythingConfig;
import com.myself.everything.core.EverythingManager;
import com.myself.everything.core.model.Condition;
import com.myself.everything.core.model.Thing;

import java.util.List;
import java.util.Scanner;

public class EverythigCmdApp {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        //欢迎
        welcome();

        //统一调度器
        EverythingManager manager = EverythingManager.getInstance();

        //启动后台清理线程
        manager.startBackgroundClearThread();

        //交互式
        interactive(manager);
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
