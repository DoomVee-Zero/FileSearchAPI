package net.doomer.filesearchapi.api;

import lombok.extern.log4j.Log4j2;
import net.doomer.filesearchapi.service.DirectorySearch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
public class FileSearchController {
  @GetMapping("/")
  public String helloWorld() {
    return "Hello World";
  }

  @GetMapping("/search/{folder}")
  public String searchFiles(
      @PathVariable String folder,
      @RequestParam(required = false) String term
  ) {
    if(term != null && !term.isBlank()) {
      log.info("Started search for term: '{}' in folder: '{}'", term, folder);
      DirectorySearch directory = new DirectorySearch(folder);
      String result = "Result: %s".formatted(directory.search(term));
      log.info("Finished search for term: '{}' in folder: '{}'", term, folder);
      return result;
    }
    return "No search term provided";
  }
}
