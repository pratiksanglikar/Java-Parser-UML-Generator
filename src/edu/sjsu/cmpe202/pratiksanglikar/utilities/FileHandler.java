package edu.sjsu.cmpe202.pratiksanglikar.utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

	public static List<File> getAllFoldersInDirectory(String pathToParentDirectory) {
		File folder = new File(pathToParentDirectory);
		List<File> folders = new ArrayList<File>(0);
		if (!folder.isDirectory()) {
			return null;
		}
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if (file.isDirectory()) {
				if (!file.getName().startsWith(".")) {
					folders.add(file);
				}
			}
		}
		return folders;
	}

	public static List<File> getAllFilesInDirectory(File folder) {
		if (!folder.isDirectory()) {
			return null;
		}
		List<File> files = new ArrayList<File>(0);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if(file.isFile()){
				if(file.getName().endsWith("java")){
					files.add(file);
				}
			}
		}
		return files;
	}
}
