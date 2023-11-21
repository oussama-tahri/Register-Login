package com.tahrioussama.registerloginbackend.web;

import com.tahrioussama.registerloginbackend.dtos.EmployeeDTO;
import com.tahrioussama.registerloginbackend.dtos.LoginDTO;
import com.tahrioussama.registerloginbackend.payload.response.LoginMessage;
import com.tahrioussama.registerloginbackend.services.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
@RequestMapping("employee")
@AllArgsConstructor
public class EmployeeController {

    private EmployeeService employeeService;


    @PostMapping(path = "/save")
    public String saveEmployee(@RequestBody EmployeeDTO employeeDTO)
    {
        String id = employeeService.addEmployee(employeeDTO);
        return id;
    }
    @PostMapping(path = "/login")
    public ResponseEntity<?> loginEmployee(@RequestBody LoginDTO loginDTO)
    {
        LoginMessage loginResponse = employeeService.loginEmployee(loginDTO);
        return ResponseEntity.ok(loginResponse);
    }
}