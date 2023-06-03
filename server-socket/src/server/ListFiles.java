package server;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

public class ListFiles {
	public static Set<ServerFile> listFilesUsingDirectoryStream(String dir) throws IOException {
	    Set<ServerFile> fileSet = new HashSet<>();
	    try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
	        for (Path path : stream) { // iterates
	            if (!Files.isDirectory(path)) {
	            	BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
	            	ServerFile tempFile = new ServerFile(
	            			path.getFileName().toString(),
	            			attr.lastModifiedTime().toString(),
	            			attr.size());
	                fileSet.add(tempFile); //ambil namanya aja
	            }
	        }
	    }
	    return fileSet;
	}
	public static void main(String[] args) {
		System.out.println(System.getProperty("user.dir"));
		try {
			//System.out.println(listFilesUsingDirectoryStream());
			//System.out.println(listFilesUsingDirectoryStream(System.getProperty("user.dir")));
			Set<ServerFile> fileSet = new HashSet<>();
			fileSet = listFilesUsingDirectoryStream(System.getProperty("user.dir"));
			for(ServerFile serverFile : fileSet) {
				System.out.println(serverFile.getFileName());
				System.out.println(serverFile.getLastModifiedDate());
				System.out.println(serverFile.getSize() + " bytes\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//	public Set<String> listFilesUsingFilesList(String dir) throws IOException {
//		try (Stream<Path> stream = Files.list(Paths.get(dir))) {
//			return stream.filter(file -> !Files.isDirectory(file))
//					.map(Path::getFileName)
//					.map(Path::toString)
//					.collect(Collectors.toSet());
//		}
//	}
}
