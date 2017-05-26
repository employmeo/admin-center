package com.talytica.admin.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.employmeo.data.model.SurveySection;
import com.talytica.common.service.StorageService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/upload")
public class FileUploadController {
	// save uploaded file to this folder
    private static String UPLOADED_FOLDER = "C://users//sri//workspace//files//";
	private static final String FRAGMENT_ROOT = "model/";
	private static final String MODEL = "upload";
	private static final String MODEL_DISPLAY = "Uploads";
	private static final String LIST_VIEW = FRAGMENT_ROOT + MODEL + "/list";
	private static final String STATUS_VIEW = FRAGMENT_ROOT + MODEL + "/index";

	@Autowired
	StorageService storageService;
	
    @PostMapping("/media")
    public String fileUpload(Model model,
    		@RequestParam("file") MultipartFile file, 
    		@RequestParam("folder") String folder, 
    		RedirectAttributes redirectAttributes
    		) throws IOException {
    	
        if (null == file || file.isEmpty()) {
        	log.debug("file is null");
        	return "redirect:/admin/upload/status";
        }

        String url = storageService.uploadCoreMediaFile(convertMultipart(file), folder);
        model.addAttribute("result", url);
        model.addAttribute("folder", folder);
        
        return STATUS_VIEW;

    }

    @RequestMapping(value = {"","index"}, method = RequestMethod.GET)
    public String uploadStatus(Model model) {
    	return STATUS_VIEW;
    }
    
	private File convertMultipart(MultipartFile media) throws IOException {
		File convFile = new File(media.getOriginalFilename());
	    convFile.createNewFile(); 
	    FileOutputStream fos = new FileOutputStream(convFile); 
	    fos.write(media.getBytes());
	    fos.close();
	    return convFile;
	}
	
    @ModelAttribute("allFolders")
    public Iterable<S3ObjectSummary> getSurveySections() {
    	Iterable<S3ObjectSummary> summaries = storageService.getDirectories("media/");
        return summaries;
    }
}
