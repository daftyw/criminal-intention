package com.augmentis.ayp.crimin.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Rawin on 18-Jul-16.
 */
public class CrimeLab {
    List<Crime> crimeList;

    private static CrimeLab instance;

    public static CrimeLab getInstance(Context context) {
        if( instance == null ) {
            instance = new CrimeLab();
        }
        return instance;
    }

    private CrimeLab() {
        crimeList = new ArrayList<>();
    }

    public Crime getCrimeById(UUID uuid) {
        for (Crime crime : crimeList) {
            if(crime.getId().equals(uuid)) {
                return crime;
            }
        }

        return null;
    }


    public int getCrimePositionById(UUID uuid) {
        int size = crimeList.size();
        for (int i = 0; i < size; i++) {
            if(crimeList.get(i).getId().equals(uuid)) {
                return i;
            }
        }

        return -1;
    }

    public List<Crime> getCrimes() {
        return this.crimeList;
    }

    public void addCrime(Crime crime) {
        crimeList.add(crime);
    }
}
