package server;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

public class ServerDirectory {
	private Set<ServerFile> fileSet;
	
	public ServerDirectory() {
		super();
		fileSet = new HashSet<>();
	}
	public String listDirectoryAsHTML () {
		String listing = "<table class=\"table\">\r\n"
				+ "  <thead>\r\n"
				+ "    <tr>\r\n"
				+ "      <th scope=\"col\">#</th>\r\n"
				+ "      <th scope=\"col\">File Name</th>\r\n"
				+ "      <th scope=\"col\">Last Date Modified</th>\r\n"
				+ "      <th scope=\"col\">Size</th>\r\n"
				+ "    </tr>\r\n"
				+ "  </thead>\r\n"
				+ "  <tbody>";
		for(ServerFile serverFileListItem : fileSet){
			listing += "<tr>\r\n"
					+ "      <th scope=\"row\">1</th>\r\n"
					+ "      <td>" + serverFileListItem.getFileName() + "</td>\r\n"
					+ "      <td>" + serverFileListItem.getLastModifiedDate() + "</td>\r\n"
					+ "      <td>" + serverFileListItem.getSize() + "</td>\r\n"
					+ "    </tr>";
		}
		listing += "</tbody>\r\n"
				+ "</table>";
		return listing;
	}
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
	public Set<ServerFile> getFileSet() {
		return fileSet;
	}
	public void setFileSet(Set<ServerFile> fileSet) {
		this.fileSet = fileSet;
	}
	public String listDirectoryAsHTML(String host) {
		String listing = "<table class=\"table\">\r\n"
				+ "  <thead>\r\n"
				+ "    <tr>\r\n"
				+ "      <th scope=\"col\">#</th>\r\n"
				+ "      <th scope=\"col\">File Name</th>\r\n"
				+ "      <th scope=\"col\">Last Date Modified</th>\r\n"
				+ "      <th scope=\"col\">Size</th>\r\n"
				+ "    </tr>\r\n"
				+ "  </thead>\r\n"
				+ "  <tbody>";
		for(ServerFile serverFileListItem : fileSet){
			listing += "<tr>\r\n"
					+ "      <th scope=\"row\">1</th>\r\n"
					+ "      <td><a href=\""+ serverFileListItem.getFileName() + "\">" + serverFileListItem.getFileName() + "</a></td>\r\n"
					+ "      <td>" + serverFileListItem.getLastModifiedDate() + "</td>\r\n"
					+ "      <td>" + serverFileListItem.getSize() + "</td>\r\n"
					+ "    </tr>";
		}
		listing += "</tbody>\r\n"
				+ "</table>";
		return listing;
	}
}
