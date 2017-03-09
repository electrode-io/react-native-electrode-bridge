package com.walmartlabs.electrode.reactnative.bridge.util;

import android.os.Bundle;

import com.walmartlabs.electrode.reactnative.bridge.BridgeMessage;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;
import com.walmartlabs.electrode.reactnative.sample.model.BirthYear;
import com.walmartlabs.electrode.reactnative.sample.model.Person;
import com.walmartlabs.electrode.reactnative.sample.model.Position;
import com.walmartlabs.electrode.reactnative.sample.model.Status;

import junit.framework.TestCase;

import static com.walmartlabs.electrode.reactnative.bridge.BridgeMessage.Type.REQUEST;

public class BridgeArgumentsTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Logger.overrideLogLevel(Logger.LogLevel.DEBUG);
    }

    public void testFromBundleSuccess() {
        Person person = new Person.Builder("Deepu", 10).build();
        Bundle personBundle = person.toBundle();

        Person personCopy = BridgeArguments.bridgeableFromBundle(personBundle, Person.class);
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

        Person personCopy = BridgeArguments.bridgeableFromBundle(personBundle, Person.class);
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

    public void testFromBundleWithEmptyBundle() {
        Person personCopy = BridgeArguments.bridgeableFromBundle(Bundle.EMPTY, Person.class);
        assertNull(personCopy);
    }

    public void testFromBundleWithNullBundle() {
        Person personCopy = BridgeArguments.bridgeableFromBundle(null, Person.class);
        assertNull(personCopy);
    }

    public void testBundleToPrimitiveAndViceVersaForString() {
        String[] expectedArray = {"ONE", "TWO", "THREE", "FOUR", "FIVE"};
        Bundle bundle = BridgeArguments.getBundleForPrimitive(expectedArray, String[].class);
        assertNotNull(bundle);
        assertNotNull(bundle.get(BridgeMessage.BRIDGE_MSG_DATA));

        String[] actualArray = (String[]) BridgeArguments.getPrimitiveFromBundle(bundle, String[].class);
        assertNotNull(actualArray);
        assertEquals(expectedArray.length, actualArray.length);

    }
}