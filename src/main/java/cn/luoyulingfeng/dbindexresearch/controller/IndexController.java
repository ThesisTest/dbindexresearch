package cn.luoyulingfeng.dbindexresearch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/index")
public class IndexController {

    @GetMapping(value = "/research1")
    public String research1(){
        return "research1";
    }

    @GetMapping(value = "/research2")
    public String research2(){
        return "research2";
    }

    @GetMapping(value = "/research3")
    public String research3(){
        return "research3";
    }
}
