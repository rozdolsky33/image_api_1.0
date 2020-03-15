package com.arwest.developer.image_api.service;

import com.arwest.developer.image_api.data.Image;
import com.arwest.developer.image_api.data.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ImageService {

    public static String UPLOAD_ROOT = "upload-dir-images"; //local patch dir. to the system where app is running

    private final ImageRepository repository;
    private final ResourceLoader resourceLoader;

    @Autowired
    public ImageService(ImageRepository repository, ResourceLoader resourceLoader){
        this.repository = repository;
        this.resourceLoader = resourceLoader;
    }
//Resource types expand a lot of different types. Types: file based, systems classpath - in this case local file based system- using file prefix.
    public Resource findOneImage(String filename){
        return resourceLoader.getResource("file:" + UPLOAD_ROOT + "/" +filename);
    }
    public void createImage(MultipartFile file) throws IOException{
        if(!file.isEmpty()){
            Files.copy(file.getInputStream(), Paths.get(UPLOAD_ROOT, file.getOriginalFilename()));
            repository.save(new Image (file.getOriginalFilename()));
            //step to create new entry in db under the same heading (image metadata)
        }
    }
    public void deleteImage(String filename) throws IOException {

        final Image byName = repository.findByName(filename);
        repository.delete(byName);
        Files.deleteIfExists(Paths.get(UPLOAD_ROOT, filename));
    }

    @Bean
   // @Profile("dev")
    CommandLineRunner setUp(ImageRepository repository) throws IOException {

        return(args) -> {
            FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));
            Files.createDirectory(Paths.get(UPLOAD_ROOT));
            FileCopyUtils.copy("Test file", new FileWriter(UPLOAD_ROOT + "/test"));
            repository.save(new Image("test"));
            FileCopyUtils.copy("Test file2", new FileWriter(UPLOAD_ROOT + "/test2"));
            repository.save(new Image("test2"));
            FileCopyUtils.copy("Test file2", new FileWriter(UPLOAD_ROOT + "/test3"));
            repository.save(new Image("test3"));
        };
    }

}
