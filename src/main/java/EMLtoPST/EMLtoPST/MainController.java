package EMLtoPST.EMLtoPST;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


public class MainController {
    @GetMapping("/")
    public String index(){
        return "index";
    }
}
