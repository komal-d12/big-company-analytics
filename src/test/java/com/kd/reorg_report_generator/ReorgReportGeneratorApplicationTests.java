package com.kd.reorg_report_generator;

import com.kd.reorg_report_generator.model.Employee;
import com.kd.reorg_report_generator.model.OrgAnalytics;
import com.kd.reorg_report_generator.model.SalaryInsights;
import com.kd.reorg_report_generator.service.OrgReportingService;
import com.kd.reorg_report_generator.utils.CsvUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.kd.reorg_report_generator.utils.CalcUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReorgReportGeneratorApplicationTests {

    private List<Employee> employeeData = null;

    @BeforeAll
    public void setUp() {
        employeeData = new ArrayList<>(List.of(
                new Employee(123, "Joe", "Doe", 60000d, null),
                new Employee(124, "Martin", "Chekov", 45000d, 123),
                new Employee(125, "Bob", "Ronstad", 47000d, 123),
                new Employee(300, "Alice", "Hasacat", 50000d, 124),
                new Employee(305, "Brett", "Hardleaf", 56000d, 300),
                new Employee(307, "Angela", "Smith", 34000d, 305),
                new Employee(308, "Chris", "John", 38000d, 307),
                new Employee(401, "Snow", "White", 35000d, 308)
        ));
    }

    @Test
    void testGetDirectReportees() {
        Integer employeeId1 = 123;
        List<Employee> reportees = OrgReportingService.getDirectReportees(employeeId1, employeeData);
        assertTrue(reportees != null && !reportees.isEmpty());
        reportees.forEach(reportee -> {
            assertEquals(employeeId1, reportee.getManagerId());
        });

        Integer employeeId2 = 300;
        reportees = OrgReportingService.getDirectReportees(employeeId2, employeeData);
        assertTrue(reportees != null && !reportees.isEmpty());
        reportees.forEach(reportee -> {
            assertEquals(employeeId2, reportee.getManagerId());
        });

        Integer employeeId3 = 125;
        reportees = OrgReportingService.getDirectReportees(employeeId3, employeeData);
        assertTrue(reportees.isEmpty());
    }

    @Test
    void testGetHierarchyDepth() {
        // Top-level employee
        Employee employee = employeeData.get(employeeData.indexOf(new Employee(123)));
        Integer depth = OrgReportingService.calculateHierarchyDepth(employee, employeeData);
        assertEquals(0, depth);

        // Employee with 5 managers between them and the CEO
        employee = employeeData.get(employeeData.indexOf(new Employee(401)));
        depth = OrgReportingService.calculateHierarchyDepth(employee, employeeData);
        assertEquals(5, depth);

        // Employee with 5 managers between them and the CEO
        employee = employeeData.get(employeeData.indexOf(new Employee(300)));
        depth = OrgReportingService.calculateHierarchyDepth(employee, employeeData);
        assertEquals(1, depth);

        // Non-existent employee
        employee = new Employee();
        employee.setId(408);
        employee.setManagerId(222);
        depth = OrgReportingService.calculateHierarchyDepth(employee, employeeData);
        assertEquals(-1, depth);

        // Non-existent manager
        employee = new Employee();
        employee.setId(401);
        employee.setManagerId(222);
        depth = OrgReportingService.calculateHierarchyDepth(employee, employeeData);
        assertEquals(-1, depth);
    }

    @Test
    void testGenerateSalaryInsights() {
        // Manager's salary is in ideal range
        Employee employee = employeeData.get(employeeData.indexOf(new Employee(123)));
        List<Employee> directReports = OrgReportingService.getDirectReportees(employee.getId(), employeeData);
        SalaryInsights salaryInsights = OrgReportingService.generateSalaryInsights(employee.getId(), employee.getSalary(), directReports);
        assertEquals(employee.getSalary(), salaryInsights.getSalary());
        assertFalse(salaryInsights.getIsSalaryLess());
        assertFalse(salaryInsights.getIsSalaryMore());
        assertEquals(0d, salaryInsights.getDifferenceAmount());
        assertEquals("", salaryInsights.getDifferencePercentage());

        // Manager's salary is less than ideal minimum
        employee = employeeData.get(employeeData.indexOf(new Employee(124)));
        directReports = OrgReportingService.getDirectReportees(employee.getId(), employeeData);
        salaryInsights = OrgReportingService.generateSalaryInsights(employee.getId(), employee.getSalary(), directReports);
        assertEquals(employee.getSalary(), salaryInsights.getSalary());
        assertTrue(salaryInsights.getIsSalaryLess());
        assertFalse(salaryInsights.getIsSalaryMore());

        double averageSalary = directReports.stream().collect(Collectors.averagingDouble(Employee::getSalary));
        double idealMinSalary = calculateIdealMinSalary(averageSalary);
        double difference = idealMinSalary - employee.getSalary();
        assertEquals(difference, salaryInsights.getDifferenceAmount());
        assertEquals(calculatePercentage(difference, idealMinSalary), salaryInsights.getDifferencePercentage());

        // Manager's salary is more than ideal maximum
        employee = employeeData.get(employeeData.indexOf(new Employee(305)));
        directReports = OrgReportingService.getDirectReportees(employee.getId(), employeeData);
        salaryInsights = OrgReportingService.generateSalaryInsights(employee.getId(), employee.getSalary(), directReports);
        assertEquals(employee.getSalary(), salaryInsights.getSalary());
        assertFalse(salaryInsights.getIsSalaryLess());
        assertTrue(salaryInsights.getIsSalaryMore());

        averageSalary = directReports.stream().collect(Collectors.averagingDouble(Employee::getSalary));
        double idealMaxSalary = calculateIdealMaxSalary(averageSalary);
        difference = employee.getSalary() - idealMaxSalary;
        assertEquals(difference, salaryInsights.getDifferenceAmount());
        assertEquals(calculatePercentage(difference, idealMaxSalary), salaryInsights.getDifferencePercentage());
    }

    @Test
    void testGenerateOrgAnalytics() {
        OrgAnalytics report = OrgReportingService.generateOrgAnalytics(employeeData);
        assertNotNull(report);
        report.printReport();
    }

    @Test
    void testloadEmployeeData() throws FileNotFoundException, URISyntaxException {
        List<Employee> employeeData = CsvUtils.loadEmployeeData("big-company-test.csv");
        assertTrue(employeeData != null && !employeeData.isEmpty());
        employeeData.forEach(employee -> assertNotNull(employee.getId()));
    }

}
