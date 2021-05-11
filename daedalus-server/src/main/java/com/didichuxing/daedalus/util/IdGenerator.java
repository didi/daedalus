package com.didichuxing.daedalus.util;

import lombok.experimental.UtilityClass;
import org.bson.types.ObjectId;

/**
 * @author : jiangxinyu
 * @date : 2021/1/20
 */
@UtilityClass
public class IdGenerator {

    public static String id() {
        return new ObjectId().toHexString();
    }
}
