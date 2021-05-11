package com.didichuxing.daedalus.service.plugin.function;

import com.didichuxing.daedalus.pojo.ExecuteException;
import com.google.common.base.Splitter;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/9/28
 */
@Slf4j
@Component
public class NowDateFunction extends AbstractVariadicFunction {
    @Override
    public AviatorObject variadicCall(Map<String, Object> env, AviatorObject... args) {
        String nowDate = getDate(env, args);
        return new AviatorString(nowDate);
    }

    private String getDate(Map<String, Object> env, AviatorObject[] args) {
        try {
            String nowDate;
            if (ArrayUtils.isEmpty(args)) {
                nowDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } else if (args.length == 1) {
                String format = String.valueOf(args[0].getValue(env));
                nowDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));
            } else if (args.length == 2) {
                LocalDateTime now = LocalDateTime.now();
                String format = String.valueOf(args[0].getValue(env));
                String opt = String.valueOf(args[1].getValue(env));
                List<String> opts = Splitter.onPattern("(?=\\+)|(?=-)").omitEmptyStrings().trimResults().splitToList(opt);
                for (String o : opts) {
                    TemporalUnit unit = Unit.getUnit(o.substring(o.length() - 1));
                    switch (o.substring(0, 1)) {
                        case "+":
                            now = now.plus(Long.parseLong(o.substring(1, o.length() - 1)), unit);
                            break;
                        case "-":
                            now = now.minus(Long.parseLong(o.substring(1, o.length() - 1)), unit);
                            break;
                        default:
                            throw new ExecuteException("仅支持+、-操作符");
                    }

                }
                nowDate = now.format(DateTimeFormatter.ofPattern(format));
            } else {
                throw new ExecuteException(getName() + "函数参数个数错误！");
            }
            return nowDate;
        } catch (ExecuteException e) {
            throw e;
        } catch (Exception e) {
            log.error("nowDate 函数异常！", e);
            throw new ExecuteException("nowDate函数执行异常，请检查格式！");

        }
    }

    @Override
    public String getName() {
        return "nowDate";
    }

    public static void main(String[] args) {
        AviatorEvaluator.addFunction(new NowDateFunction());
        System.out.println(AviatorEvaluator.execute("nowDate()"));
        System.out.println(AviatorEvaluator.execute("nowDate('yyyy-MM-dd hh:mm:ss')"));
        System.out.println(AviatorEvaluator.execute("nowDate('yyyy-MM-dd hh:mm:ss','+10d-10s')"));
    }


    @AllArgsConstructor
    enum Unit {
        YEAR("y", ChronoUnit.YEARS),
        MONTH("M", ChronoUnit.MONTHS),
        DAY("d", ChronoUnit.DAYS),
        HOUR("h", ChronoUnit.HOURS),
        MINUTE("m", ChronoUnit.MINUTES),
        SECOND("s", ChronoUnit.SECONDS);

        String code;
        TemporalUnit unit;

        static TemporalUnit getUnit(String code) {
            for (Unit unit : Unit.values()) {
                if (unit.code.equals(code)) {
                    return unit.unit;
                }
            }
            return null;
        }
    }
}
