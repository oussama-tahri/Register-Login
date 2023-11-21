package com.tahrioussama.registerloginbackend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmployeeDTO {
    private int employeeid;
    private String employeename;
    private String email;
    private String password;
}