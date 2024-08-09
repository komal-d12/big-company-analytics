package com.kd.reorg_report_generator.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static com.kd.reorg_report_generator.utils.CalcUtils.*;

/**
 * Class to hold Salary report for an Employee
 */
@Data
@NoArgsConstructor
@ToString
public class SalaryInsights {

    private Integer employeeId;
    private Double salary;
    private Double directReportsAverageSalary;
    private Boolean isSalaryLess = false;
    private Boolean isSalaryMore = false;
    private Double differenceAmount = 0d;
    private String differencePercentage = "";

    public void evaluate() {
        double idealMinSalary = calculateIdealMinSalary(directReportsAverageSalary);
        double idealMaxSalary = calculateIdealMaxSalary(directReportsAverageSalary);

        if (salary < idealMinSalary) {
            setIsSalaryLess(true);
            double differenceAmount = idealMinSalary - salary;
            setDifferenceAmount(differenceAmount);
            setDifferencePercentage(calculatePercentage(differenceAmount, idealMinSalary));
        } else if (salary > idealMaxSalary) {
            setIsSalaryMore(true);
            double differenceAmount = salary - idealMaxSalary;
            setDifferenceAmount(differenceAmount);
            setDifferencePercentage(calculatePercentage(differenceAmount, idealMaxSalary));
        }
    }

}
