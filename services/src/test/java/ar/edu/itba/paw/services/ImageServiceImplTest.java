package ar.edu.itba.paw.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.models.Image;

@RunWith(MockitoJUnitRunner.class)
public class ImageServiceImplTest {

    private static final byte[] IMAGE_DATA = new byte[0];
    private static final long IMAGE_ID = 0;
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
    public void testStoreImageNoCache(){
        Mockito.when(
            imageDao.create(Mockito.eq(IMAGE_DATA))
        ).thenReturn(IMAGE_ID);

        long id = imageService.storeImage(IMAGE_DATA);

        assertEquals(IMAGE_ID, id);
    }
    @Test
    public void testStoreImageCache(){
        Mockito.when(
            imageDao.create(Mockito.eq(IMAGE_DATA))
        ).thenReturn(IMAGE_ID);
        Mockito.when(
            cacheManager.getCache(IMAGE_CACHE)
        ).thenReturn(cache);

        long id = imageService.storeImage(IMAGE_DATA);

        assertEquals(IMAGE_ID, id);
    }

}
