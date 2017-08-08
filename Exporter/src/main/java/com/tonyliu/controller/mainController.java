package com.tonyliu.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by tao on 8/3/17.
 */

@Controller
public class mainController {

    @RequestMapping("/")
    public String mainPage() {
        return "index";
    }

    // for user to download the file
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadResource(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "filename") String filename) {
        //If user is not authorized - he should be thrown out from here itself
        String[] strings = filename.split(" ");
        String newFilename = "";
        for (int i = 0; i < strings.length; i++) {
            if (i != strings.length - 1) {
                newFilename += strings[i] + "+";
            } else {
                newFilename += strings[i];
            }
        }
        //System.out.println("Filename: " + newFilename + ".csv");
        //Authorized user will download the file
        String dataDirectory = request.getServletContext().getRealPath("CSVFiles/");
        System.out.println("dataDirectory: " + dataDirectory);
        Path file = Paths.get(dataDirectory, newFilename );
        if (Files.exists(file)) {
            System.out.println("File Exist!!!");
            response.setContentType("text/csv");
            response.addHeader("Content-Disposition", "attachment; filename=UniversityX+CS101+2015_T1.csv");
            try {
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Unable to find the file!");
        }

    }
}