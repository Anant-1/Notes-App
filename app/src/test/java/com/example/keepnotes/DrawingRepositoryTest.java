package com.example.keepnotes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.keepnotes.data.repository.DrawingRepository;

import org.junit.Before;
import org.junit.Test;

public class DrawingRepositoryTest {

    private DrawingRepository repository;

    @Before
    public void setUp() {
        repository = new DrawingRepository();
    }

    @Test
    public void testDefaultValues() {
        assertFalse(repository.isEraserMode());
        assertEquals(5f, repository.getPenSize(), 0.001f);
        assertEquals(20f, repository.getEraserSize(), 0.001f);
    }

    @Test
    public void testSetPenSize() {
        repository.setPenSize(15f);
        assertEquals(15f, repository.getPenSize(), 0.001f);
    }

    @Test
    public void testSetEraserMode() {
        repository.setEraserMode(true);
        assertTrue(repository.isEraserMode());
    }

    @Test
    public void testSetSelectedColor() {
        int color = 0xFF536DFE;
        repository.setSelectedColor(color);
        assertEquals(color, repository.getSelectedColor());
    }
}
