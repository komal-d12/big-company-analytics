package com.kd.reorg_report_generator.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public final class CalcUtils {

    public static Double calculateIdealMinSalary(Double averageSalary) {
        return averageSalary + (averageSalary * 0.2);
    }

    public static Double calculateIdealMaxSalary(Double averageSalary) {
        return averageSalary + (averageSalary * 0.5);
    }

    public static String calculatePercentage(Double value1, Double value2) {
        return format((value1 / value2) * 100.0);
    }

    public static String format(Double value) {
        DecimalFormat decimalFormat = new DecimalFormat("###.00");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        return decimalFormat.format(value);
    }

}
