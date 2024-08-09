package com.kd.reorg_report_generator.utils;

import com.kd.reorg_report_generator.model.Employee;
import com.kd.reorg_report_generator.service.OrgReportingService;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

public class CsvUtils {

    public static List<Employee> loadEmployeeData(String fileName) throws FileNotFoundException, URISyntaxException {
        URL url = OrgReportingService.class.getClassLoader().getResource(fileName);
        File file = Paths.get(url.toURI()).toFile();
        return new CsvToBeanBuilder<Employee>(new FileReader(file)).withType(Employee.class).build().parse();
    }
}
