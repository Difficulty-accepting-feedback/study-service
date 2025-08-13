package com.grow.study_service.common.rsdata;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RsData<T> {

    private String code;
    private String msg;
    private T data;

    public RsData(String code, String msg) {
        this(code, msg, null);
    }
}
