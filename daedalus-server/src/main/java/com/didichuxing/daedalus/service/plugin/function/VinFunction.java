package com.didichuxing.daedalus.service.plugin.function;

import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 生成车辆vin码
 *
 * @author : jiangxinyu
 * @date : 2020/9/28
 */
@Component
public class VinFunction extends AbstractVariadicFunction {
    @Override
    public AviatorObject variadicCall(Map<String, Object> env, AviatorObject... args) {
        return new AviatorString(getVin());
    }

    @Override
    public String getName() {
        return "vin";
    }


    private String getVin() {
        int length = 6;
        int VIN_LEN = 17;
        Map<String, Integer> valueWeightMap = new HashMap<>();
        valueWeightMap.put("0", 0);
        valueWeightMap.put("1", 1);
        valueWeightMap.put("2", 2);
        valueWeightMap.put("3", 3);
        valueWeightMap.put("4", 4);
        valueWeightMap.put("5", 5);
        valueWeightMap.put("6", 6);
        valueWeightMap.put("7", 7);
        valueWeightMap.put("8", 8);
        valueWeightMap.put("9", 9);
        // 子母
        valueWeightMap.put("A", 1);
        valueWeightMap.put("B", 2);
        valueWeightMap.put("C", 3);
        valueWeightMap.put("D", 4);
        valueWeightMap.put("E", 5);
        valueWeightMap.put("F", 6);
        valueWeightMap.put("G", 7);
        valueWeightMap.put("H", 8);
        valueWeightMap.put("J", 1);
        valueWeightMap.put("K", 2);
        valueWeightMap.put("L", 3);
        valueWeightMap.put("M", 4);
        valueWeightMap.put("N", 5);
        valueWeightMap.put("P", 7);
        valueWeightMap.put("R", 9);
        valueWeightMap.put("S", 2);
        valueWeightMap.put("T", 3);
        valueWeightMap.put("U", 4);
        valueWeightMap.put("V", 5);
        valueWeightMap.put("W", 6);
        valueWeightMap.put("X", 7);
        valueWeightMap.put("Y", 8);
        valueWeightMap.put("Z", 9);

        Map<Integer, Integer> idxWeightMap = new HashMap<>();
        idxWeightMap.put(1, 8);
        idxWeightMap.put(2, 7);
        idxWeightMap.put(3, 6);
        idxWeightMap.put(4, 5);
        idxWeightMap.put(5, 4);
        idxWeightMap.put(6, 3);
        idxWeightMap.put(7, 2);
        idxWeightMap.put(8, 10);
        idxWeightMap.put(9, 0);
        idxWeightMap.put(10, 9);
        idxWeightMap.put(11, 8);
        idxWeightMap.put(12, 7);
        idxWeightMap.put(13, 6);
        idxWeightMap.put(14, 5);
        idxWeightMap.put(15, 4);
        idxWeightMap.put(16, 3);
        idxWeightMap.put(17, 2);

        String str = "ABCDEFGHJKLMNPRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(33);
            sb.append(str.charAt(number));
        }
        String randomStr = sb.toString();

        String vin = "LVVDC21B5HD" + randomStr;
        int total_score = 0;
        for (int i = 0; i < VIN_LEN; i++) {
            try {
                int value_score = valueWeightMap.get(vin.substring(i, i + 1));
                int idx_weight = idxWeightMap.get(i + 1);
                total_score += value_score * idx_weight;
            } catch (Exception e) {
            }
        }

        int check_bit_value = total_score % 11;
        char check_str;
        char[] vinchar = vin.toCharArray();
        if (check_bit_value == 10) {
            check_str = 'X';
            vinchar[8] = check_str;
        } else {
            check_str = (char) (48 + check_bit_value);
            vinchar[8] = check_str;
        }
        vin = Arrays.toString(vinchar).replaceAll("[\\[\\]\\s,]", "");
        return vin;
    }
}
