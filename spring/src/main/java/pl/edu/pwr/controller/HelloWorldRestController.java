package pl.edu.pwr.controller;

import pl.edu.pwr.model.Path;
import pl.edu.pwr.service.PathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class HelloWorldRestController {

    @Autowired
    PathService pathService;  //Service which will do all data retrieval/manipulation work


    //-------------------Retrieve All Paths--------------------------------------------------------

    @RequestMapping(value = "/path/", method = RequestMethod.GET)
    public ResponseEntity<List<Path>> listAllPaths() {
        List<Path> paths = pathService.findAllPaths();
        if (paths.isEmpty()) {
            return new ResponseEntity<List<Path>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<List<Path>>(paths, HttpStatus.OK);
    }


    //-------------------Retrieve Single Path--------------------------------------------------------

    @RequestMapping(value = "/path/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Path> getPath(@PathVariable("id") long id) {
        System.out.println("Fetching Path with id " + id);
        Path path = pathService.findById(id);
        if (path == null) {
            System.out.println("Path with id " + id + " not found");
            return new ResponseEntity<Path>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Path>(path, HttpStatus.OK);
    }


    //-------------------Create a Path--------------------------------------------------------

    @RequestMapping(value = "/path/", method = RequestMethod.POST)
    public ResponseEntity<Void> createPath(@RequestBody Path path, UriComponentsBuilder ucBuilder) {
        System.out.println("Creating Path " + path.getName());

        if (pathService.isPathExist(path)) {
            System.out.println("A Path " + path.getPath() + " already exist");
            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
        }
        if (Files.isDirectory(Paths.get(path.getPath()))) {
            pathService.savePath(path);
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(ucBuilder.path("/path/{id}").buildAndExpand(path.getId()).toUri());
            return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
        }
        return new ResponseEntity<Void>(HttpStatus.CONFLICT);
    }


    //------------------- Update a Path --------------------------------------------------------

    @RequestMapping(value = "/path/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Path> updatePath(@PathVariable("id") long id, @RequestBody Path path) {
        System.out.println("Updating Path " + id);

        Path currentPath = pathService.findById(id);

        if (currentPath == null) {
            System.out.println("Path with id " + id + " not found");
            return new ResponseEntity<Path>(HttpStatus.NOT_FOUND);
        }

        currentPath.setName(path.getName());
        currentPath.setPath(path.getPath());
        currentPath.setThreads(path.getThreads());
        currentPath.setStarted(path.getStarted());
        pathService.updatePath(currentPath);
        return new ResponseEntity<Path>(currentPath, HttpStatus.OK);
    }


    //------------------- Delete a Path --------------------------------------------------------

    @RequestMapping(value = "/path/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Path> deletePath(@PathVariable("id") long id) {
        Path path = pathService.findById(id);
        if (path == null) {
            System.out.println("Unable to delete. Path with id " + id + " not found");
            return new ResponseEntity<Path>(HttpStatus.NOT_FOUND);
        }

        pathService.deletePathById(id);
        return new ResponseEntity<Path>(HttpStatus.NO_CONTENT);
    }

    //------------------- Start a Path --------------------------------------------------------

    @RequestMapping(value = "/start/{id}", method = RequestMethod.POST)
    public ResponseEntity<Path> startPath(@PathVariable("id") long id) {
        Path path = pathService.findById(id);
        if (path == null) {
            System.out.println("Unable to start. Path with id " + id + " not found");
            return new ResponseEntity<Path>(HttpStatus.NOT_FOUND);
        }
        pathService.startPath(path);
        return new ResponseEntity<Path>(HttpStatus.NO_CONTENT);
    }

    //------------------- Stop a Path --------------------------------------------------------

    @RequestMapping(value = "/stop/{id}", method = RequestMethod.POST)
    public ResponseEntity<Path> stopPath(@PathVariable("id") long id) {
        Path path = pathService.findById(id);
        if (path == null) {
            System.out.println("Unable to stop. Path with id " + id + " not found");
            return new ResponseEntity<Path>(HttpStatus.NOT_FOUND);
        }
        pathService.stopPath(path);
        return new ResponseEntity<Path>(HttpStatus.NO_CONTENT);
    }


    //------------------- Delete All Paths --------------------------------------------------------

    @RequestMapping(value = "/path/", method = RequestMethod.DELETE)
    public ResponseEntity<Path> deleteAllPath() {
        System.out.println("Deleting All Paths");

        pathService.deleteAllPaths();
        return new ResponseEntity<Path>(HttpStatus.NO_CONTENT);
    }

}