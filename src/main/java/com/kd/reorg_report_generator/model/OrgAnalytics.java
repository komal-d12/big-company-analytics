package com.kd.reorg_report_generator.model;

import java.util.Map;
import java.util.TreeMap;

/**
 * Class to hold the Org Reporting Data
 */
public class OrgAnalytics {

    private final Map<Employee, SalaryInsights> orgReport = new TreeMap<>();

    public void add(Employee employee, SalaryInsights salaryData) {
        orgReport.put(employee, salaryData);
    }

    public void printReport() {
        StringBuffer employeesEarningLess = new StringBuffer();
        StringBuffer employeesEarningMore = new StringBuffer();
        StringBuffer longReportingLine = new StringBuffer();

        orgReport.forEach((employee, salaryInsights) -> {
            if (salaryInsights.getIsSalaryLess()) {
                employeesEarningLess
                        .append("Employee ").append(employee.getId()).append(" - ")
                        .append(employee.getFirstName()).append(" ").append(employee.getLastName())
                        .append(" earns ~").append(salaryInsights.getDifferencePercentage()).append("% less than ideal minimum salary")
                        .append("\n");
            } else if (salaryInsights.getIsSalaryMore()) {
                employeesEarningMore
                        .append("Employee ").append(employee.getId()).append(" - ")
                        .append(employee.getFirstName()).append(" ").append(employee.getLastName())
                        .append(" earns ~").append(salaryInsights.getDifferencePercentage()).append("% more ideal maximum salary")
                        .append("\n");
            }

            if (employee.getHierarchyDepth() > 4) {
                longReportingLine
                        .append("Employee ").append(employee.getId()).append(" - ")
                        .append(employee.getFirstName()).append(" ").append(employee.getLastName())
                        .append(" has ").append(employee.getHierarchyDepth()).append(" managers between them and the CEO")
                        .append("\n");
            }
        });

        System.out.println("------------------------------------------------------------------------------------\n");

        if (!employeesEarningLess.isEmpty()) {
            System.out.println("Employees earning less than 20% of the average salary of their direct reportees: \n" + employeesEarningLess);
        }

        System.out.println("------------------------------------------------------------------------------------\n");

        if (!employeesEarningMore.isEmpty()) {
            System.out.println("Employees earning more than 50% of the average salary of their direct reportees: \n" + employeesEarningMore);
        }

        System.out.println("------------------------------------------------------------------------------------\n");

        if (!longReportingLine.isEmpty()) {
            System.out.println("Employees having a reporting line longer than 4: \n" + longReportingLine);
        }

        System.out.println("------------------------------------------------------------------------------------\n");
    }

}
