package com.lmxdawn.api.admin.bean;

import lombok.Data;

@Data
public class Decode {

    private String encryptedData;
    private String iv;
    private String code;
    @Override
    public String toString() {
        return "DecodeUserInfo{" +
                "encryptedData=" + encryptedData +
                ", iv='" + iv  +
                ", code='" + code +
                '}';
    }
}
