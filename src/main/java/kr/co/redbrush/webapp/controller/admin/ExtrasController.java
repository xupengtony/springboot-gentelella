package kr.co.redbrush.webapp.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
@Slf4j
public class ExtrasController {

    @GetMapping({"/extras/page/{pageType}"})
    public String extraPages(Map<String, Object> model, @PathVariable("pageType") String pageType) {
        model.put("title", "Extra Page " + pageType);

        return "extras_page_" + pageType;
    }
}