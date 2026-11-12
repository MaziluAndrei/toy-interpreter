package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import interpreter.model.type.StringType;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ADTTest {

    @Test
    void stackPushPopAndToListTopFirst() throws ADTException {
        IStack<Integer> stack = new MyStack<>();
        assertTrue(stack.isEmpty());

        stack.push(10);
        stack.push(20);
        assertEquals(List.of(20, 10), stack.toList());
        assertEquals(20, stack.pop());
        assertEquals(10, stack.pop());
        assertTrue(stack.isEmpty());

        assertThrows(ADTException.class, stack::pop);
    }

    @Test
    void dictionaryPutLookupUpdateRemoveAndToMap() throws ADTException {
        IDictionary<String, Integer> dict = new MyDictionary<>();
        assertFalse(dict.isDefined("x"));

        dict.put("x", 5);
        assertTrue(dict.isDefined("x"));
        assertEquals(5, dict.lookup("x"));

        dict.update("x", 42);
        assertEquals(42, dict.lookup("x"));
        assertEquals(42, dict.toMap().get("x"));

        dict.remove("x");
        assertFalse(dict.isDefined("x"));
        assertThrows(ADTException.class, () -> dict.lookup("a"));
        assertThrows(ADTException.class, () -> dict.remove("a"));
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

        assertEquals(List.of("first", "second"), list.toList());
        assertThrows(ADTException.class, () -> list.get(2));
    }

    @Test
    void stringTypeDefaultAndStringValueEquality() {
        assertEquals(new StringValue(""), new StringType().defaultValue());
        StringValue s1 = new StringValue("test.in");
        StringValue s2 = new StringValue("test.in");
        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }
}