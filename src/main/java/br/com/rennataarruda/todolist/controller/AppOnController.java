package br.com.rennataarruda.todolist.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("public/on")
public class AppOnController {


    @GetMapping
    public String retornoAppOn(){
        return "Aplicação está On";
    }
}
