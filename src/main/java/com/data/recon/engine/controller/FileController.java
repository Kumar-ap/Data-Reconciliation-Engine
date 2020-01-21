package com.data.recon.engine.controller;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.data.recon.engine.service.FileService;

@RestController
public class FileController {
	
	@Autowired
	private FileService fileService;
	
	
	
//	@RequestMapping(value = "/getDataReconresults", method = RequestMethod.GET)
	@GetMapping(value = "/getDataReconresults")
	 public Map<String, List<String>> fileUploadData()
	 {
		Map<String, List<String>> mapData=fileService.matchFile(new File("X.txt"), new File("Y.txt"));
		return mapData;
		
		 
	 }

	

}
