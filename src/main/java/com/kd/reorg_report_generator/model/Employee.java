package com.kd.reorg_report_generator.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Employee implements Comparable<Employee> {

    @CsvBindByName(column = "Id")
    private Integer id;

    @CsvBindByName(column = "firstName")
    private String firstName;

    @CsvBindByName(column = "lastName")
    private String lastName;

    @CsvBindByName(column = "salary")
    private Double salary;

    @CsvBindByName(column = "managerId")
    private Integer managerId;

    private Integer hierarchyDepth;

    public Employee(Integer id) {
        this.id = id;
    }

    public Employee(Integer id, String firstName, String lastName, Double salary, Integer managerId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.managerId = managerId;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object otherEmp) {
        if (id != null && otherEmp instanceof Employee) {
            return id.equals(((Employee) otherEmp).getId());
        }

        return false;
    }

    @Override
    public int compareTo(Employee otherEmployee) {
        return otherEmployee.getId().compareTo(this.getId());
    }

    @Override
    public String toString() {
        return id + "";
    }
}