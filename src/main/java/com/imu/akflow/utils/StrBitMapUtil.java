package com.imu.akflow.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StrBitMapUtil {

    /**
     * 基于Set<Integer>生成01字符串
     * 规则：长度为最大元素值+1
     * 示例：{0, 2, 4} -> "10101" (长度5)
     */
    public static String toBitmap(Set<Integer> positions) {
        if (CollectionUtils.isEmpty(positions)) {
            return "";
        }

        // 找到最大元素值
        long maxPos = positions.stream()
                .max(Integer::compareTo)
                .orElse(-1);

        if (maxPos < 0) {
            return "";
        }

        // 创建位图字符串
        StringBuilder bitmap = new StringBuilder();
        for (int i = 0; i <= maxPos; i++) {
            bitmap.append(positions.contains(i) ? '1' : '0');
        }

        return bitmap.toString();
    }

    /**
     * 基于01字符串还原为Set<Integer>
     * 规则：字符串中'1'的位置就是集合元素
     * 示例："10101" -> {0, 2, 4}
     */
    public static Set<Integer> bitmapToSet(String bitmap) {
        if (StringUtils.isBlank(bitmap)) {
            return new HashSet<>();
        }

        return IntStream.range(0, bitmap.length())
                .filter(i -> bitmap.charAt(i) == '1')
                .boxed()
                .collect(Collectors.toSet());
    }
}
