package org.maples.serinus.controller.restful;

import lombok.extern.slf4j.Slf4j;
import org.maples.serinus.model.SerinusResources;
import org.maples.serinus.utility.ResultBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/resources")
public class ResourcesController {

    @PostMapping("/add")
    public String add(@RequestBody SerinusResources resources) {

        return "redirect:/system/dashboard";
    }

    @PostMapping("/modify")
    public ResultBean<String> modify(@RequestBody SerinusResources resources) {

        return new ResultBean<>(200, "success", "");
    }

    @PostMapping("/delete")
    public ResultBean<String> delete(long resourcesID) {

        return new ResultBean<>(200, "success", "");
    }

    @PostMapping("/list")
    public ResultBean<String> list() {

        return new ResultBean<>(200, "success", "");
    }
}
