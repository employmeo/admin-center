package com.talytica.admin.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/upload")
public class FileUploadController {
	// save uploaded file to this folder
    private static String UPLOADED_FOLDER = "C://users//sri//workspace//files//";
	private static final String FRAGMENT_ROOT = "upload/";
	private static final String STATUS_VIEW = FRAGMENT_ROOT + "status";

    @PostMapping("/video")
    public String fileUpload(
    		@RequestParam("video") MultipartFile file, 
    		@RequestParam("name") String name,
    		RedirectAttributes redirectAttributes
    		) {
    	
        if (null == file || file.isEmpty()) {
        	log.debug("file is null, name is {}", name);
        	return "redirect:/admin/upload/status";
        }

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.createFile(path);
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/admin/upload/status";

    }

    @RequestMapping(value = {"status"}, method = RequestMethod.GET)
    public String uploadStatus() {
    	return STATUS_VIEW;
    }

}
