# Register-Login

Lets follow the following step
First step you can visit Spring Initializer (https://start.spring.io/)  and configure the project using following dependencies.

 you have add the following dependencies while you configuring the spring initializr :
 ```markdown
Spring jpa
Spring web
Spring security
Mysql
```
After done the Configuration.

Open the project on the ***pom.xml***

 ```markdown
 <dependencies>
   <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
   </dependency>
   <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
   </dependency>
   <dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
  </dependency>
  <!-- https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-security -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
    <version>2.7.8</version>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
   </dependency>
<dependencies>
  ```

you can see the dependencies what you added.

**Database Configuration**
Configure the database mysql database connection in the ***application.properties***.  this place where connect to Spring boot  to Mysql.

 ```markdown
application.properties
spring.application.name=Registation
server.port=8085
spring.jpa.hibernate.ddl-auto=create
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/dbkms?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root123
#jpa vendor adapter configuration
spring.jpa.database-platform=org.hibernate.dialect.MySQL57Dialect
spring.jpa.generate-ddl=true
spring.jpa.show-sql=true
 ``` 

Create the Entity Employee for represent the employee data in the database.

Employee :
 ```java
package com.tahrioussama.Registation.Entity;
import javax.persistence.*;
@Entity
@Table(name="employee")
public class Employee {
    @Id
    @Column(name="employee_id", length = 45)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int employeeid;
    @Column(name="employee_name", length = 255)
    private String employeename;
    @Column(name="email", length = 255)
    private String email;
    @Column(name="password", length = 255)
    private String password;
    public Employee() {
    }
    public Employee(int employeeid, String employeename, String email, String password) {
        this.employeeid = employeeid;
        this.employeename = employeename;
        this.email = email;
        this.password = password;
    }
//create getters and setters
 ```

After that create the employeeDTOs for passing to data from frontend.in this example create two DTOs .

EmployeeDTO,
LoginDTO

EmployeeDTO :
 ```java
public class EmployeeDTO {
    private int employeeid;
    private String employeename;
    private String email;
    private String password;
    public EmployeeDTO() {
    }
    public EmployeeDTO(int employeeid, String employeename, String email, String password) {
        this.employeeid = employeeid;
        this.employeename = employeename;
        this.email = email;
        this.password = password;
    }
} //create getters and setters
 ```

LoginDTO :
 ```java
public class LoginDTO {
private String email;
private String password;
public LoginDTO() {
}
public LoginDTO(String email, String password) {
this.email = email;
this.password = password;
} //create getters and setters
  ```

Create a EmployeeRepository interface which extends JpaRepository

EmployeeRepo :
```java
package com.tahrioussama.Registation.Repo;
import com.example.Registation.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@EnableJpaRepositories
@Repository
public interface EmployeeRepo extends JpaRepository<Employee,Integer>
{
      Optional<Employee> findOneByEmailAndPassword(String email, String password);
    Employee findByEmail(String email);
}
```

Create the Employee Service

EmployeeService :

```java
package com.tahrioussama.Registation.Service;
import com.example.Registation.Dto.EmployeeDTO;
import com.example.Registation.Dto.LoginDTO;
import com.example.Registation.payload.response.LoginMesage;
public interface EmployeeService {
    String addEmployee(EmployeeDTO employeeDTO);
    LoginMesage loginEmployee(LoginDTO loginDTO);
}
 ```

Create the EmployeeImpl which implements from EmployeeService

EmployeeImpl :
```java
package com.tahrioussama.Registation.Service.impl;
import com.tahrioussama.Registation.Dto.EmployeeDTO;
import com.tahrioussama.Registation.Dto.LoginDTO;
import com.tahrioussama.Registation.Entity.Employee;
import com.tahrioussama.Registation.Repo.EmployeeRepo;
import com.tahrioussama.Registation.Service.EmployeeService;
import com.tahrioussama.Registation.payload.response.LoginMesage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Optional;
@Service
public class EmployeeIMPL implements EmployeeService {
    @Autowired
    private EmployeeRepo employeeRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public String addEmployee(EmployeeDTO employeeDTO) {
        Employee employee = new Employee(
                employeeDTO.getEmployeeid(),
                employeeDTO.getEmployeename(),
                employeeDTO.getEmail(),
               this.passwordEncoder.encode(employeeDTO.getPassword())
        );
        employeeRepo.save(employee);
        return employee.getEmployeename();
    }


    @Override
    public LoginMesage  loginEmployee(LoginDTO loginDTO) {
        String msg = "";
        Employee employee1 = employeeRepo.findByEmail(loginDTO.getEmail());
        if (employee1 != null) {
            String password = loginDTO.getPassword();
            String encodedPassword = employee1.getPassword();
            Boolean isPwdRight = passwordEncoder.matches(password, encodedPassword);
            if (isPwdRight) {
                Optional<Employee> employee = employeeRepo.findOneByEmailAndPassword(loginDTO.getEmail(), encodedPassword);
                if (employee.isPresent()) {
                    return new LoginMessage("Login Success", true);
                } else {
                    return new LoginMessage("Login Failed", false);
                }
            } else {
                return new LoginMessage("password Not Match", false);
            }
        }else {
            return new LoginMessage("Email not exits", false);
        }
    }
}
```

Create a EmployeeController to manage the Restful APIs requests related to employee login and registration.

EmployeeController :
```java
package com.tahrioussama.RegisterLogin.EmployeeController;
import com.tahrioussama.RegisterLogin.Dto.EmployeeDTO;
import com.tahrioussama.RegisterLogin.Dto.LoginDTO;
import com.tahrioussama.RegisterLogin.Service.EmployeeService;
import com.tahrioussama.RegisterLogin.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@CrossOrigin
@RequestMapping("api/v1/employee")
public class EmployeeController {
    @Autowired
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
```

Security measures need to implemented 
Create a file  SecurityConfig for managing the Security password

```java
package com.tahrioussama.Registation.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
 ```

Create LoginMessage Page for displaying Messages while testing

LoginMessage :

```java
public class LoginMessage {
    String message;
    Boolean status;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Boolean getStatus() {
        return status;
    }
    public void setStatus(Boolean status) {
        this.status = status;
    }
    public LoginMessage(String message, Boolean status) {
        this.message = message;
        this.status = status;
    }
}
```

Our backend is over lets start the frontend as React.

**React**
React is a front-end application.let’s install the react  inside the project folder open the command prompt and type the commands.

Installing React
```markdown
npx create-react-app front-end
```
then front-end folder has been created and installed the all dependencies based on React.

After that you need to install the required dependencies for managing the API requests

```markdown
npm install --save axios
npm i react-router-dom
```

BootStrap Styles
After that go to the bootstrap respective website and copy the bootstap css style and paste inside the index.html  inside the head tag.index.html is the single page application.

create the folder inside src which name is components inside the folder create 3 files. ****Home.jsx,Register.jsx,Login.jsx****

Register.jsx :

```javascript
import {  useState } from "react";
import axios from "axios";
function Register() {
  
    const [employeename, setEmployeename] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    async function save(event) {
        event.preventDefault();
        try {
          await axios.post("http://localhost:8085/api/v1/employee/save", {
          employeename: employeename,
          email: email,
          password: password,
          });
          alert("Employee Registation Successfully");
        } catch (err) {
          alert(err);
        }
      }
  
    return (
    <div>
    <div class="container mt-4" >
    <div class="card">
            <h1>Student Registation</h1>
    
    <form>
        <div class="form-group">
          <label>Employee name</label>
          <input type="text"  class="form-control" id="employeename" placeholder="Enter Name"
          
          value={employeename}
          onChange={(event) => {
            setEmployeename(event.target.value);
          }}
          />
        </div>
        <div class="form-group">
          <label>email</label>
          <input type="email"  class="form-control" id="email" placeholder="Enter Email"
          
          value={email}
          onChange={(event) => {
            setEmail(event.target.value);
          }}
          
          />
 
        </div>
        <div class="form-group">
            <label>password</label>
            <input type="password"  class="form-control" id="password" placeholder="Enter password"
            
            value={password}
            onChange={(event) => {
              setPassword(event.target.value);
            }}
            
            />
          </div>
        <button type="submit" class="btn btn-primary mt-4" onClick={save} >Save</button>
       
      </form>
    </div>
    </div>
     </div>
    );
  }
  
  export default Register;
```

Login.jsx :

```javascript
import {  useState } from "react";
import { useNavigate } from 'react-router-dom';
import axios from "axios";
function Login() {
   
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const navigate = useNavigate();
    async function login(event) {
        event.preventDefault();
        try {
          await axios.post("http://localhost:8085/api/v1/employee/login", {
            email: email,
            password: password,
            }).then((res) => 
            {
             console.log(res.data);
             
             if (res.data.message == "Email not exits") 
             {
               alert("Email not exits");
             } 
             else if(res.data.message == "Login Success")
             { 
                
                navigate('/home');
             } 
              else 
             { 
                alert("Incorrect Email and Password not match");
             }
          }, fail => {
           console.error(fail); // Error!
  });
        }
 
         catch (err) {
          alert(err);
        }
      
      }
    return (
       <div>
            <div class="container">
            <div class="row">
                <h2>Login</h2>
             <hr/>
             </div>
             <div class="row">
             <div class="col-sm-6">
 
            <form>
        <div class="form-group">
          <label>Email</label>
          <input type="email"  class="form-control" id="email" placeholder="Enter Name"
          
          value={email}
          onChange={(event) => {
            setEmail(event.target.value);
          }}
          
          />
        </div>
        <div class="form-group">
            <label>password</label>
            <input type="password"  class="form-control" id="password" placeholder="Enter Fee"
            
            value={password}
            onChange={(event) => {
              setPassword(event.target.value);
            }}
            
            />
          </div>
                  <button type="submit" class="btn btn-primary" onClick={login} >Login</button>
              </form>
            </div>
            </div>
            </div>
     </div>
    );
  }
  
  export default Login;
```

Home.jsx :

```javascript
function Home() {
  return (
    <div>
     <h1>Home</h1>
    </div>
  );
}
export default Home;
Routes
Configure the Routes for managing the urls.open the App.js

App.js
import { BrowserRouter,Routes,Route } from "react-router-dom";
import Register from "./compontents/Register";
import Login from "./compontents/Login";
 
import Home from "./compontents/Home";
function App() {
  return (
    <div>
      <BrowserRouter>
            <Routes>
              <Route path="/home" element= { <Home/>} />
              <Route path="/register" element= { <Register/>} />
              <Route path="/" element= { <Login/>} />
            </Routes>
        </BrowserRouter>
      
    </div>
  );
}
export default App;
```
