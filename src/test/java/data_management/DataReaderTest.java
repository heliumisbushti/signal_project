package com.data_management;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit and integration tests for DataReader interface and its integration with DataStorage.
 */
class DataReaderTest {

    static class MockDataReader implements DataReader {
        private boolean shouldThrowOnConnect = false;
        private boolean shouldThrowOnClose = false;
        private boolean simulateFormatError = false;
        private boolean simulateConnectionLoss = false;
        private boolean closed = false;

        public void setShouldThrowOnConnect(boolean value) { this.shouldThrowOnConnect = value; }
        public void setShouldThrowOnClose(boolean value) { this.shouldThrowOnClose = value; }
        public void setSimulateFormatError(boolean value) { this.simulateFormatError = value; }
        public void setSimulateConnectionLoss(boolean value) { this.simulateConnectionLoss = value; }

        @Override
        public void connectAndStream(DataStorage dataStorage) throws IOException {
            if (shouldThrowOnConnect) throw new IOException("Connection failed");
            if (simulateConnectionLoss) throw new IOException("Connection lost during streaming");

            // Simulate normal data
            dataStorage.addPatientData(1, 98.6, "Temperature", 1712345678901L);

            // Simulate data format error
            if (simulateFormatError) {
                // For this mock, we just skip adding a record or could throw an exception
                // Here, we throw an exception to simulate a parsing failure
                throw new IOException("Malformed data received");
            }
        }

        @Override
        public void close() throws IOException {
            if (shouldThrowOnClose) throw new IOException("Close failed");
            closed = true;
        }

        public boolean isClosed() { return closed; }
    }

    private DataStorage storage;
    private MockDataReader reader;

    @BeforeEach
    void setUp() {
        storage = new DataStorage();
        reader = new MockDataReader();
    }

    @Test
    void testNormalStreaming() throws IOException {
        reader.connectAndStream(storage);
        List<Patient> patients = storage.getAllPatients();
        assertEquals(1, patients.size());
        assertEquals(1, patients.get(0).getPatientId());
    }

    @Test
    void testConnectionFailure() {
        reader.setShouldThrowOnConnect(true);
        IOException ex = assertThrows(IOException.class, () -> reader.connectAndStream(storage));
        assertTrue(ex.getMessage().contains("Connection failed"));
        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void testConnectionLossDuringStreaming() {
        reader.setSimulateConnectionLoss(true);
        IOException ex = assertThrows(IOException.class, () -> reader.connectAndStream(storage));
        assertTrue(ex.getMessage().contains("Connection lost"));
        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void testDataFormatError() {
        reader.setSimulateFormatError(true);
        IOException ex = assertThrows(IOException.class, () -> reader.connectAndStream(storage));
        assertTrue(ex.getMessage().contains("Malformed data"));
        // No patient should be added if format error occurs before adding valid data
        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void testCloseSuccess() throws IOException {
        reader.connectAndStream(storage);
        reader.close();
        assertTrue(reader.isClosed());
    }

    @Test
    void testCloseFailure() throws IOException {
        reader.setShouldThrowOnClose(true);
        reader.connectAndStream(storage);
        IOException ex = assertThrows(IOException.class, () -> reader.close());
        assertTrue(ex.getMessage().contains("Close failed"));
    }
}
