package com.tahrioussama.registerloginbackend.services;


import com.tahrioussama.registerloginbackend.dtos.EmployeeDTO;
import com.tahrioussama.registerloginbackend.dtos.LoginDTO;
import com.tahrioussama.registerloginbackend.payload.response.LoginMessage;

public interface EmployeeService {
    String addEmployee(EmployeeDTO employeeDTO);
    LoginMessage loginEmployee(LoginDTO loginDTO);
}
