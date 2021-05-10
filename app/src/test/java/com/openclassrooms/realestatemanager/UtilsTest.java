package com.openclassrooms.realestatemanager;

import org.junit.Test;

import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void convertDollarToEuro() {
        int dollar = 1000;
        int euro = Utils.convertDollarToEuro(dollar);

        assertEquals(812, euro);
    }

    @Test
    public void convertEuroToDollars() {
        int euro = 1000;
        int dollar = Utils.convertEuroToDollars(euro);

        assertEquals(1188, dollar);
    }

    @Test
    public void getTodayDate() {
        String date = Utils.getTodayDate();

        assertTrue(date.matches("../../...."));
    }
}