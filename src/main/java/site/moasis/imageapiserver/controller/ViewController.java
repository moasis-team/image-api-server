package site.moasis.imageapiserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index(){
        return "index.html";
    }

    @GetMapping("/files")
    public String allFiles(){
        return "files.html";
    }
}
