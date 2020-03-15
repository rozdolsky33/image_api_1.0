package com.arwest.developer.image_api.data;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface ImageRepository extends PagingAndSortingRepository<Image, Long> {

    Image findByName(String name);

}
