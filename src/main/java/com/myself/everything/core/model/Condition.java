package com.myself.everything.core.model;

import lombok.Data;

@Data
public class Condition {
    private String name;
    private String fileType;
    private Integer limit;

    //检索结果文件信息按depth排序，默认升序
    //1.默认是true -> 升序 asc
    //2.false -> 降序 desc
    private Boolean orderByAsc;
}
