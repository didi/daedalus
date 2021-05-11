package com.didichuxing.daedalus.common.enums.http;

import lombok.AllArgsConstructor;
import lombok.Getter;
import okhttp3.MediaType;

/**
 * @author : jiangxinyu
 * @date : 2020/4/26
 */
@Getter
@AllArgsConstructor
public enum BodyType {
    FORM_URLENCODED("application/x-www-form-urlencoded"),
    JSON("application/json"),
    TEXT("text/plain"),
    FORM_DATA("multipart/form-data");

    private String contentType;


    public static BodyType get(String contentType) {
        if (contentType == null) {
            return null;
        }
        MediaType mediaType = MediaType.parse(contentType);
        if (mediaType == null) {
            return null;
        }
        for (BodyType bodyType : values()) {
            String type = mediaType.type() + "/" + mediaType.subtype();
            if (type.equalsIgnoreCase(bodyType.contentType)) {
                return bodyType;
            }
        }
        return null;
    }
}
