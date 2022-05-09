package net.doomer.filesearchapi.service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DirectorySearch {
  File root;
  Map<String, Map<String, Integer>> fileContentRelation;

  public DirectorySearch(String path) {
    this(getFileFromPath(path));
  }

  public DirectorySearch(File root) {
    this.root = root;
    if(this.root == null) {
      throw new IllegalArgumentException("File not found!");
    }
    this.fileContentRelation = readFiles();
  }

  public List<String> search(String word) {
    word = word.toLowerCase();
    Map<String, Integer> fileMap = new LinkedHashMap<>();
    for(Map.Entry<String, Map<String, Integer>> fileEntry : this.fileContentRelation.entrySet()) {
      if(fileEntry.getValue().containsKey(word)) {
        fileMap.put(fileEntry.getKey(), fileEntry.getValue().get(word));
      }
    }
    List<String> files = new ArrayList<>(fileMap.entrySet().stream()
        .sorted(Map.Entry.comparingByValue()).map(Map.Entry::getKey).toList());
    Collections.reverse(files);
    return files;
  }

  public Map<String, Map<String, Integer>> getFileContentRelation() {
    return fileContentRelation;
  }

  private Map<String, Map<String, Integer>> readFiles() {
    Map<String, Map<String, Integer>> fileMap = new HashMap<>();
    File[] files = this.root.listFiles();
    if(files == null) {
      return fileMap;
    }
    for(File file : files) {
      if(file.isDirectory()) {
        DirectorySearch subSearch = new DirectorySearch(file);
        subSearch.getFileContentRelation().forEach((k, v) ->
            fileMap.put("%s/%s".formatted(file.getName(), k), v));
        continue;
      }

      Path path = file.toPath();
      try {
        byte[] contentBytes = Files.readAllBytes(path);
        String contents = new String(contentBytes).strip();
        fileMap.put(file.getName(), splitParagraph(contents));
      } catch(IOException e) {
        System.err.printf("The file '%s' was not found!%n", file.getName());
      }
    }
    return fileMap;
  }

  private static Map<String, Integer> splitParagraph(String paragraph) {
    Map<String, Integer> words = new HashMap<>();
    String[] split = paragraph.split("[ ,.;!?(){}\\[\\]\n]+");
    for(String word : split) {
      String transformed = word.toLowerCase();
      if(words.containsKey(transformed)) {
        words.put(transformed, words.get(transformed) + 1);
      } else {
        words.put(transformed, 1);
      }
    }
    return words;
  }

  public static File getFileFromPath(String path) {
    URL url = DirectorySearch.class.getClassLoader().getResource(path);
    File file = null;
    try {
      if(url != null) {
        file = new File(url.toURI());
      }
    } catch (URISyntaxException e) {
      System.err.println(e.getMessage());
    }
    return file;
  }
}
