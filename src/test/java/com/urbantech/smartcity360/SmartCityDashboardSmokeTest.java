package com.urbantech.smartcity360;

import com.urbantech.smartcity360.ui.ExecutionLogBus;
import com.urbantech.smartcity360.ui.PartADecoratorPanel;
import com.urbantech.smartcity360.ui.PartBCompositePanel;
import com.urbantech.smartcity360.ui.PartCChainPanel;
import com.urbantech.smartcity360.ui.PartDFlyweightPanel;
import com.urbantech.smartcity360.ui.PartEInterpreterPanel;
import com.urbantech.smartcity360.ui.SmartCityDashboardFrame;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Phase 4: Java Swing GUI Smoke and Integration Verification")
class SmartCityDashboardSmokeTest {

    @Test
    @DisplayName("Verify ExecutionLogBus message broadcasting to subscribers")
    void testExecutionLogBusBroadcasting() throws Exception {
        ExecutionLogBus bus = ExecutionLogBus.getInstance();
        AtomicBoolean received = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        bus.subscribe(msg -> {
            if (msg.contains("Test Smoke Message")) {
                received.set(true);
                latch.countDown();
            }
        });

        bus.log("TestCategory", "Test Smoke Message");
        boolean completed = latch.await(3, TimeUnit.SECONDS);

        assertTrue(completed && received.get(), "Subscriber should receive the broadcasted message");
    }

    @Test
    @DisplayName("Verify all 5 GoF pattern UI panels instantiate cleanly")
    void testPanelsInstantiation() throws Exception {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Skipping Swing UI layout instantiation in headless mode");
            return;
        }

        SwingUtilities.invokeAndWait(() -> {
            assertDoesNotThrow(() -> new PartADecoratorPanel());
            assertDoesNotThrow(() -> new PartBCompositePanel());
            assertDoesNotThrow(() -> new PartCChainPanel());
            assertDoesNotThrow(() -> new PartDFlyweightPanel());
            assertDoesNotThrow(() -> new PartEInterpreterPanel());
        });
    }

    @Test
    @DisplayName("Verify SmartCityDashboardFrame loads tabs and initializes components without errors")
    void testDashboardFrameCreation() throws Exception {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        SwingUtilities.invokeAndWait(() -> {
            SmartCityDashboardFrame frame = new SmartCityDashboardFrame();
            assertNotNull(frame);
            assertTrue(frame.getTitle().contains("SmartCity360"));
            frame.dispose();
        });
    }
}
