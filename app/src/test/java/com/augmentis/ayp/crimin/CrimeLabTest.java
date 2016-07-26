package com.augmentis.ayp.crimin;


import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;


/**
 * Created by Rawin on 20-Jul-16.
 */
public class CrimeLabTest {

    @Test
    public void test_create_crimeLab_no_error_and_size_100() {
        CrimeLab crimeLab = CrimeLab.getInstance(null);
        for (int i=0; i<2000000; i++) {
            Crime crime = crimeLab.getCrimeById(UUID.randomUUID());
            assertNotNull(crime);
        }

    }

    @Test
    public void testArray() {   //

    }
}
