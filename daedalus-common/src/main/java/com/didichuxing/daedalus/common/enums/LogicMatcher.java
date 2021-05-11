package com.didichuxing.daedalus.common.enums;

import com.didichuxing.daedalus.common.utils.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hamcrest.Matcher;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.hamcrest.core.StringContains;
import org.hamcrest.number.OrderingComparison;

import java.util.function.Function;

/**
 * @author : jiangxinyu
 * @date : 2020/11/12
 */
@AllArgsConstructor
@Getter
@SuppressWarnings("all")
public enum LogicMatcher {

    IS("是", true, IsEqual::new),
    IS_NOT("不是", true, val -> IsNot.not(new IsEqual<>(val))),
    EQ("相等(数字)", true, IsEqual::new),
    NE("不相等(数字)", true, val -> IsNot.not(new IsEqual<>(val))),
    GT("大于(数字)", true, OrderingComparison::greaterThan),
    GE("大于等于(数字)", true, OrderingComparison::greaterThanOrEqualTo),
    LT("小于(数字)", true, OrderingComparison::lessThan),
    LE("小于等于(数字)", true, OrderingComparison::lessThanOrEqualTo),
    EXIST("存在", false, value -> new IsNot(new IsNull())),
    CONTAIN("包含", true, val -> StringContains.containsString(JsonUtils.toJsonString(val))),
    ARRAY_SIZE_EQ("数组长度等于", true, size -> IsCollectionWithSize.hasSize(Integer.parseInt(String.valueOf(size)))),
    ARRAY_SIZE_GT("数组长度大于", true, size -> IsCollectionWithSize.hasSize(OrderingComparison.greaterThan(Integer.parseInt(String.valueOf(size))))),
    ARRAY_SIZE_LT("数组长度小于", true, size -> IsCollectionWithSize.hasSize(OrderingComparison.lessThan(Integer.parseInt(String.valueOf(size))))),
    ;

    private final String desc;
    private final boolean needExpectValue;
    //    private final boolean needPath;
    private final Function<Comparable, Matcher> matcher;


}
