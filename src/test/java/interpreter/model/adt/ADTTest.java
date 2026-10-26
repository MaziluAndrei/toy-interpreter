package interpreter.model.adt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ADTException;
import org.junit.jupiter.api.Test;

class ADTTest {

    @Test
    void stackLifoOrderAndEmptyPopThrows() throws ADTException {
        IStack<Integer> stack = new MyStack<>();
        assertTrue(stack.isEmpty());

        stack.push(10);
        stack.push(20);
        assertFalse(stack.isEmpty());
        assertEquals(20, stack.pop());
        assertEquals(10, stack.pop());
        assertTrue(stack.isEmpty());

        assertThrows(ADTException.class, stack::pop);
    }

    @Test
    void dictionaryPutLookupUpdateAndMissingKeyThrows() throws ADTException {
        IDictionary<String, Integer> dict = new MyDictionary<>();
        assertFalse(dict.isDefined("x"));

        dict.put("x", 5);
        assertTrue(dict.isDefined("x"));
        assertEquals(5, dict.lookup("x"));

        dict.update("x", 42);
        assertEquals(42, dict.lookup("x"));

        assertThrows(ADTException.class, () -> dict.lookup("missing"));
        assertThrows(ADTException.class, () -> dict.update("missing", 1));
    }

    @Test
    void listAddGetSizeAndOutOfBoundsThrows() throws ADTException {
        IList<String> list = new MyList<>();
        assertEquals(0, list.size());

        list.add("first");
        list.add("second");
        assertEquals(2, list.size());
        assertEquals("first", list.get(0));
        assertEquals("second", list.get(1));

        assertThrows(ADTException.class, () -> list.get(-1));
        assertThrows(ADTException.class, () -> list.get(2));
    }
}