package com.tonyliu.controller;

import com.tonyliu.entity.Course;
import com.tonyliu.entity.Xblock;
import com.tonyliu.service.XblockService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by tao on 8/3/17.
 */

@RestController
public class TesterController {

    @Autowired
    private XblockService service;

    @RequestMapping("/hello")
    public String hello() {
        return "hello";
    }

    @RequestMapping("/getXblocks")
    public List<Xblock> getXblocks() {

        return service.getXblocks();
    }

    @RequestMapping(value = "/getCourseId", method = RequestMethod.GET)
    public List<Course> getAllCourseId() {
        return service.getCourses();
    }

    @RequestMapping(value="/searchByCourseId", method = RequestMethod.POST)
    public List<Xblock> getXblocksByCourseId (@RequestParam(value = "course_id") String course_id) {
        String[] strings = course_id.split(" ");
        String course_new_id = "";
        for(int  i = 0; i < strings.length; i++) {
            if(i != strings.length - 1) {
                course_new_id += strings[i] + "+";
            } else {
                course_new_id += strings[i];
            }
        }
        System.out.println("couse_id: " + course_new_id);
        return service.getXblocksByCourseId(course_new_id);
    }



}
