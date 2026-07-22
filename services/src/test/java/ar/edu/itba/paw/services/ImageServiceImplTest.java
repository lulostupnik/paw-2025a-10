package ar.edu.itba.paw.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.models.Image;

@RunWith(MockitoJUnitRunner.class)
public class ImageServiceImplTest {

    private static final long IMAGE_ID = 7;
    private static final byte[] IMAGE_DATA = new byte[]{1, 2, 3, 4};
    private static final Image IMAGE = new Image(IMAGE_ID, IMAGE_DATA);

    @InjectMocks
    ImageServiceImpl imageService;

    @Mock
    ImageDao imageDao;

    @Test
    public void testCreateImage(){
        when(
            imageDao.create(eq(IMAGE_DATA))
        ).thenReturn(IMAGE_ID);

        long imageId = imageService.createImage(IMAGE_DATA);

        assertEquals(IMAGE_ID, imageId);
        verify(imageDao).create(eq(IMAGE_DATA));
    }

    @Test
    public void testFindImage(){
        when(
            imageDao.findById(eq(IMAGE_ID))
        ).thenReturn(Optional.of(IMAGE));

        Optional<Image> maybeImage = imageService.findImage(IMAGE_ID);

        assertNotNull(maybeImage);
        assertTrue(maybeImage.isPresent());
        assertEquals(IMAGE, maybeImage.get());
    }

    @Test
    public void testFindImageMissing(){
        when(
            imageDao.findById(eq(IMAGE_ID))
        ).thenReturn(Optional.empty());

        Optional<Image> maybeImage = imageService.findImage(IMAGE_ID);

        assertNotNull(maybeImage);
        assertTrue(maybeImage.isEmpty());
    }

    @Test
    public void testDeleteImage(){
        imageService.deleteImage(IMAGE_ID);

        verify(imageDao).delete(eq(IMAGE_ID));
    }
}
