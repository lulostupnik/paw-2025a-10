package ar.edu.itba.paw.services;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.models.Image;

@RunWith(MockitoJUnitRunner.class)
public class ImageServiceImplTest {

    private static final byte[] IMAGE_DATA = new byte[0];
    private static final long IMAGE_ID = 0;
    private static final Image IMAGE = new Image(IMAGE_ID, IMAGE_DATA);
    private static final String IMAGE_CACHE = "images";

    @InjectMocks
    ImageServiceImpl imageService;

    @Mock
    ImageDao imageDao;
    @Mock
    CacheManager cacheManager;
    @Mock
    Cache cache;

    @Test
    public void testCreateImageNoCache(){
        when(
            imageDao.create(eq(IMAGE_DATA))
        ).thenReturn(IMAGE_ID);

        long id = imageService.createImage(IMAGE_DATA);

        assertEquals(IMAGE_ID, id);
    }
    @Test
    public void testCreateImageCache(){
        when(
            imageDao.create(eq(IMAGE_DATA))
        ).thenReturn(IMAGE_ID);
        when(
            cacheManager.getCache(IMAGE_CACHE)
        ).thenReturn(cache);

        long id = imageService.createImage(IMAGE_DATA);

        assertEquals(IMAGE_ID, id);
    }

    @Test
    public void testFindImage(){
        when(
            imageDao.findById(eq(IMAGE_ID))
        ).thenReturn(Optional.of(IMAGE));

        Optional<Image> maybeImage = imageService.findImage(IMAGE_ID);

        assertNotNull(maybeImage);
        assertEquals(IMAGE, maybeImage.get());
    }

    @Test
    public void testDeleteImage(){
        imageService.deleteImage(IMAGE_ID);
    }

}
