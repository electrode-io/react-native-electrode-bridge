package com.walmartlabs.electrode.reactnative.bridge.util;

import android.os.Bundle;

import com.walmartlabs.electrode.reactnative.bridge.BridgeMessage;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;
import com.walmartlabs.electrode.reactnative.sample.model.BirthYear;
import com.walmartlabs.electrode.reactnative.sample.model.Person;
import com.walmartlabs.electrode.reactnative.sample.model.Position;
import com.walmartlabs.electrode.reactnative.sample.model.Status;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class BridgeArgumentsTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Logger.overrideLogLevel(Logger.LogLevel.DEBUG);
    }

    public void testFromBundleSuccess() {
        Person person = new Person.Builder("Deepu", 10).build();
        Bundle personBundle = person.toBundle();

        Person personCopy = (Person) BridgeArguments.objectFromBundle(personBundle, Person.class);
        assertNotNull(personCopy);
        assertEquals(person.getName(), personCopy.getName());
        assertEquals(person.getMonth(), personCopy.getMonth());
    }

    public void testFromBundleSuccess1() {
        Status status = new Status.Builder(true).log(false).build();
        BirthYear birthYear = new BirthYear.Builder(01, 2000).build();
        Position position = new Position.Builder(20.12).lng(12.20).build();

        Person person = new Person.Builder("Richard", 10)
                .age(18)
                .birthYear(birthYear)
                .status(status)
                .position(position)
                .build();
        Bundle personBundle = person.toBundle();

        Person personCopy = (Person) BridgeArguments.objectFromBundle(personBundle, Person.class);
        assertNotNull(personCopy);
        assertEquals(person.getName(), personCopy.getName());
        assertEquals(person.getMonth(), personCopy.getMonth());
        assertNotNull(person.getStatus());
        assertEquals(person.getStatus().getLog(), personCopy.getStatus().getLog());
        assertEquals(person.getStatus().getMember(), personCopy.getStatus().getMember());
        assertNotNull(person.getBirthYear());
        assertEquals(person.getBirthYear().getMonth(), personCopy.getBirthYear().getMonth());
        assertEquals(person.getBirthYear().getYear(), personCopy.getBirthYear().getYear());
        assertNotNull(person.getPosition());
        assertEquals(person.getPosition().getLat(), personCopy.getPosition().getLat());
        assertEquals(person.getPosition().getLng(), personCopy.getPosition().getLng());
    }

    public void testGenerateBundleForList() {
        final Person person = new Person.Builder("test1", 1).build();
        final Person person1 = new Person.Builder("test2", 2).build();
        final Person person2 = new Person.Builder("test3", 3).build();
        List<Person> personList = new ArrayList<Person>() {{
            add(person);
            add(person1);
            add(person2);
        }};

        Bundle bundle = BridgeArguments.generateDataBundle(personList);
        assertNotNull(bundle);

        List<Person> personListCopy = (List<Person>) BridgeArguments.generateObject(bundle.get(BridgeMessage.BRIDGE_MSG_DATA), Person.class);
        assertNotNull(personListCopy);
        assertEquals(personList.size(), personListCopy.size());
        for (int i = 0; i < personList.size(); i++) {
            Person expected = personList.get(i);
            Person actual = personListCopy.get(i);
            assertNotNull(actual);
            assertEquals(expected.getName(), actual.getName());
            assertEquals(expected.getMonth(), actual.getMonth());
        }
    }


    public void testGenerateObjectForBridgeable() {
        final Person person = new Person.Builder("test1", 1).build();
        Bundle bundle = BridgeArguments.generateDataBundle(person);
        assertNotNull(bundle);

        final Person personCopy = (Person) BridgeArguments.generateObject(bundle.get(BridgeMessage.BRIDGE_MSG_DATA), Person.class);
        assertNotNull(personCopy);
        assertEquals(person.getName(), personCopy.getName());
        assertEquals(person.getMonth(), personCopy.getMonth());
    }

    public void testGenerateObjectForString() {
        final String expected = "test";
        Bundle bundle = BridgeArguments.generateDataBundle(expected);
        assertNotNull(bundle);

        final String actual = (String) BridgeArguments.generateObject(bundle.get(BridgeMessage.BRIDGE_MSG_DATA), String.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    public void testGenerateObjectForStringArray() {
        final String[] expected = {"one", "two"};
        Bundle bundle = BridgeArguments.generateDataBundle(expected);
        assertNotNull(bundle);

        final Object actual = BridgeArguments.generateObject(bundle.get(BridgeMessage.BRIDGE_MSG_DATA), String.class);
        assertTrue(actual instanceof List);
        assertTrue(((List) actual).size() > 0);
        assertTrue(((List) actual).get(0) instanceof String);
        assertEquals(expected.length, ((List) actual).size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], ((List) actual).get(i));
        }
    }

    public void testGenerateObjectForInteger() {
        final Integer expected = 1;
        Bundle bundle = BridgeArguments.generateDataBundle(expected);
        assertNotNull(bundle);

        final Integer actual = (Integer) BridgeArguments.generateObject(bundle.get(BridgeMessage.BRIDGE_MSG_DATA), Integer.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    public void testGenerateObjectForBoolean() {
        final Boolean expected = true;
        Bundle bundle = BridgeArguments.generateDataBundle(expected);
        assertNotNull(bundle);

        final Boolean actual = (Boolean) BridgeArguments.generateObject(bundle.get(BridgeMessage.BRIDGE_MSG_DATA), Boolean.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    public void testToIntArray() {
        List<Integer> inputList = new ArrayList<Integer>() {{
            add(1);
            add(2);
            add(3);
        }};

        int result[] = BridgeArguments.toIntArray(inputList);

        assertEquals(inputList.size(), result.length);
    }

    public void testToIntArrayWithNull() {
        List<Integer> inputList = new ArrayList<Integer>() {{
            add(1);
            add(2);
            add(null);
            add(3);
        }};

        int result[] = BridgeArguments.toIntArray(inputList);

        assertEquals(inputList.size(), result.length);
    }

    public void testToIntArrayWithEmptyList() {
        List<Integer> inputList = new ArrayList<>();

        int result[] = BridgeArguments.toIntArray(inputList);

        assertEquals(inputList.size(), result.length);
    }

}