package com.kd.reorg_report_generator.service;

import com.kd.reorg_report_generator.model.Employee;
import com.kd.reorg_report_generator.model.OrgAnalytics;
import com.kd.reorg_report_generator.model.SalaryInsights;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static com.kd.reorg_report_generator.utils.CsvUtils.loadEmployeeData;

public class OrgReportingService {

    public static OrgAnalytics generateOrgAnalytics(List<Employee> employeeData) {
        OrgAnalytics orgAnalytics = new OrgAnalytics();

        employeeData.forEach(employee -> {
            employee.setHierarchyDepth(calculateHierarchyDepth(employee, employeeData));

            List<Employee> reportees = getDirectReportees(employee.getId(), employeeData);
            SalaryInsights salaryInsights = generateSalaryInsights(employee.getId(), employee.getSalary(), reportees);
            orgAnalytics.add(employee, salaryInsights);
        });

        return orgAnalytics;
    }

    public static Integer calculateHierarchyDepth(Employee employee, List<Employee> employeeData) {
        Integer managerId = employee.getManagerId();
        if (managerId == null) {
            // Top-level employee (does not have a manager)
            return 0;
        }

        int depth = -1;
        int managerIndex = employeeData.indexOf(new Employee(managerId));
        while (managerId != null && managerIndex >= 0) {
            depth++;
            managerId = employeeData.get(managerIndex).getManagerId();
            managerIndex = employeeData.indexOf(new Employee(managerId));
        }

        return depth;
    }

    public static List<Employee> getDirectReportees(Integer employeeId, List<Employee> employeeData) {
        return employeeData.stream().filter(reportee -> employeeId.equals(reportee.getManagerId())).toList();
    }

    public static SalaryInsights generateSalaryInsights(Integer employeeId, Double salary, List<Employee> directReports) {
        SalaryInsights salaryInsights = new SalaryInsights();
        salaryInsights.setEmployeeId(employeeId);
        salaryInsights.setSalary(salary);

        if (directReports != null && !directReports.isEmpty()) {
            double averageSalary = directReports.stream().collect(Collectors.averagingDouble(Employee::getSalary));
            salaryInsights.setDirectReportsAverageSalary(averageSalary);
            salaryInsights.evaluate();
        }

        return salaryInsights;
    }

    public static void main(String[] args) throws FileNotFoundException, URISyntaxException {
        List<Employee> employeeData = loadEmployeeData("big-company.csv");
        OrgAnalytics orgReport = OrgReportingService.generateOrgAnalytics(employeeData);
        orgReport.printReport();
    }

}

