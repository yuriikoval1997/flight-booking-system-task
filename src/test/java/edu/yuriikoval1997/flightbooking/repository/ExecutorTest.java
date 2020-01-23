package edu.yuriikoval1997.flightbooking.repository;

import edu.yuriikoval1997.flightbooking.entities.Aircraft;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ExecutorTest {

    @Autowired
    private Executor executor;

    @Test
    void executeTrans() {
        var res = executor.executeTrans(session -> {
            Aircraft aircraft1 = new Aircraft("1", (short) 210);
            Aircraft aircraft2 = new Aircraft("2", (short) 210);
            Aircraft aircraft3 = new Aircraft("3", (short) 210);
            session.save(aircraft1);
            session.save(aircraft2);
            session.save(aircraft3);

            var q = session.createQuery("DELETE FROM Aircraft WHERE model = '1' OR model = '2' OR model = '3'");
            return q.executeUpdate();
        });
        System.out.println(res);
        assertEquals(3, res);
    }

    @Test
    void execute() {
        var res = executor.execute(session -> session.get(Aircraft.class, -1L));

        assertNull(res);
    }
}