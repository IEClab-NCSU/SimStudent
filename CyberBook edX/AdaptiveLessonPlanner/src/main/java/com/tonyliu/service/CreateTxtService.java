package com.tonyliu.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * Created by tao on 8/18/17.
 */
@Service
public class CreateTxtService {

    // This method is using to create txt file in the server
    public boolean createFile(String filename) {
        boolean success = false;
        File f = new File(filename);
        if (f.exists()) {
            System.out.println("File already exists");
        } else {
            System.out.println("No such file exists, creating now");
            try {
                success = f.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (success) {
                System.out.printf("Successfully created new file: %s%n", f);
            } else {
                System.out.printf("Failed to create new file: %s%n", f);
            }
        }
        return success;

    }


}
