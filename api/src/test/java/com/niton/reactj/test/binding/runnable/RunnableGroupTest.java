package com.niton.reactj.test.binding.runnable;

import com.niton.reactj.api.binding.runnable.RunnableGroup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RunnableGroupTest {
    private       int      count     = 0;
    private final Runnable increment = () -> count++;
    private       String   yoink     = null;
    private final Runnable initYoink = () -> yoink = "yoink";

    @BeforeEach
    void prep() {
        count = 0;
        yoink = null;
    }

    @Test
    void emptyGroup() {
        RunnableGroup group = new RunnableGroup();
        Assertions.assertDoesNotThrow(group::run, "Empty groups are executable");
    }

    @Test
    void constructior() {
        var grp = new RunnableGroup(increment, initYoink);
        grp.run();
        assertEquals(1, count, "The runnables from the ctor should be executed");
        assertEquals("yoink", yoink, "The runnables from the ctor should be executed");
    }

    @Test
    void adding() {
        var grp = new RunnableGroup();
        assertEquals(0, grp.size(), "The group initially should be empty");
        grp.add(increment);
        grp.run();
        assertEquals(1, count, "The added runnable should be executed");
        grp.add(initYoink);
        grp.run();
        assertEquals(2, count, "The added runnable should be executed");
        assertEquals("yoink", yoink, "The added runnable should be executed");
        assertEquals(2, grp.size(), "The group should contain the added runnables");
    }

    @Test
    void toArray() {
        var grp = new RunnableGroup(increment, initYoink);
        assertArrayEquals(
                new Runnable[]{increment, initYoink},
                grp.toArray(),
                "The group should contain the added runnables"
        );
    }


    @Test
    void removing() {
        var grp = new RunnableGroup(increment, initYoink);
        assertEquals(2, grp.size(), "The group should contain the added runnables");
        grp.remove(increment);
        assertEquals(1, grp.size(), "The group should contain the added runnables");
        grp.run();
        assertEquals(0, count, "The removed runnable should not be executed");
        assertEquals("yoink", yoink, "The not removed runnable should be executed");
    }
}