package com.data_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataStorageTest {

    private DataStorage dataStorage;

    @BeforeEach
    public void setUp() {
        // Reset the singleton for test isolation (not ideal in production, but useful for tests)
        dataStorage = DataStorage.getInstance();
    }

    @Test
    public void testAddAndRetrievePatientData() {
        int patientId = 101;
        long timestamp = System.currentTimeMillis();

        dataStorage.addPatientData(patientId, 98.6, "Temperature", timestamp);

        List<PatientRecord> records = dataStorage.getRecords(patientId, timestamp - 1000, timestamp + 1000);

        assertEquals(1, records.size(), "Expected one record to be added and retrieved.");
        PatientRecord record = records.get(0);
        assertEquals(patientId, record.getPatientId());
        assertEquals("Temperature", record.getRecordType());
        assertEquals(98.6, record.getMeasurementValue(), 0.001);
        assertEquals(timestamp, record.getTimestamp());
    }

    @Test
    public void testGetRecords_NoPatientFound_ReturnsEmptyList() {
        List<PatientRecord> records = dataStorage.getRecords(999, 0, System.currentTimeMillis());
        assertTrue(records.isEmpty(), "Expected empty list for unknown patient ID.");
    }

    @Test
    public void testGetAllPatients() {
        int id1 = 201;
        int id2 = 202;
        long now = System.currentTimeMillis();

        dataStorage.addPatientData(id1, 70, "HeartRate", now);
        dataStorage.addPatientData(id2, 120, "BloodPressure", now);

        List<Patient> allPatients = dataStorage.getAllPatients();

        assertTrue(allPatients.stream().anyMatch(p -> p.getPatientId() == id1));
        assertTrue(allPatients.stream().anyMatch(p -> p.getPatientId() == id2));
    }
}

