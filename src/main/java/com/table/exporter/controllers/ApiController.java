package com.table.exporter.controllers;

import com.table.exporter.model.UserEntity;
import com.table.exporter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @Autowired
    private final UserService userService;

    public ApiController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<UserEntity> saveUser(@RequestBody UserEntity userEntity) {
        log.info("Here is the value of user entity {} ", userEntity);
        UserEntity savedEntity = userService.saveUser(userEntity);
        return ResponseEntity.status(HttpStatus.OK).body(savedEntity);
    }

    @GetMapping("/users/{email}")
    public ResponseEntity<List<UserEntity>> getUsersByEmail(@PathVariable("email") String email) throws SQLException {
        log.info("User email prefix is {} ", email);
        List<UserEntity> savedEntity = userService.getUsersByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(savedEntity);
    }


    @GetMapping("/table/{name}/export")
    public void exportTable(
            @PathVariable("name") String table,
            @RequestParam("col") List<String> columns,
            HttpServletResponse httpServletResponse) throws SQLException, IOException {
        log.info("Table name is {} ", table);
        log.info("Table columns are {} ", columns);

        httpServletResponse.setContentType("text/csv");
        httpServletResponse.addHeader("Content-Disposition", "attachment; filename=\"" + table + ".csv\"");

        userService.exportTable(table, columns, httpServletResponse.getWriter());
    }
}