/*
 * Copyright 2017 WalmartLabs

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.walmartlabs.electrode.reactnative.bridge.util;

import android.os.Bundle;
import android.os.Parcel;

import com.walmartlabs.electrode.reactnative.bridge.BridgeMessage;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;
import com.walmartlabs.electrode.reactnative.sample.model.Address;
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

    public void testGenerateDataBundleForList() {
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

    public void testUpdateBundleForListWithParcelable() {
        final Person person = new Person.Builder("test1", 1).build();
        final Person person1 = new Person.Builder("test2", 2).build();
        final Person person2 = new Person.Builder("test3", 3).build();
        List<Person> personList = new ArrayList<Person>() {{
            add(person);
            add(person1);
            add(person2);
        }};


        Bundle bundle = new Bundle();
        BridgeArguments.updateBundleWithList(personList, bundle, "testKey");

        List<Person> personListCopy = BridgeArguments.getList(bundle.getParcelableArray("testKey"), Person.class);
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

    public void testUpdateBundleForListWithInteger() {
        List<Integer> personList = new ArrayList<Integer>() {{
            add(1);
            add(2);
            add(3);
        }};


        Bundle bundle = new Bundle();
        BridgeArguments.updateBundleWithList(personList, bundle, "testKey");

        List<Integer> personListCopy = BridgeArguments.getList(bundle.getIntArray("testKey"), Integer.class);
        assertNotNull(personListCopy);
        assertEquals(personList.size(), personListCopy.size());
        for (int i = 0; i < personList.size(); i++) {
            Integer expected = personList.get(i);
            Integer actual = personListCopy.get(i);
            assertNotNull(actual);
            assertEquals(expected, actual);
            assertEquals(expected, actual);
        }
    }

    public void testUpdateBundleForListWithDouble() {
        List<Double> personList = new ArrayList<Double>() {{
            add(1.1);
            add(2.2);
            add(3.3);
        }};


        Bundle bundle = new Bundle();
        BridgeArguments.updateBundleWithList(personList, bundle, "testKey");

        List<Double> personListCopy = BridgeArguments.getList(bundle.getDoubleArray("testKey"), Double.class);
        assertNotNull(personListCopy);
        assertEquals(personList.size(), personListCopy.size());
        for (int i = 0; i < personList.size(); i++) {
            Double expected = personList.get(i);
            Double actual = personListCopy.get(i);
            assertNotNull(actual);
            assertEquals(expected, actual);
            assertEquals(expected, actual);
        }
    }

    public void testUpdateBundleForListWithBoolean() {
        List<Boolean> personList = new ArrayList<Boolean>() {{
            add(true);
            add(false);
            add(true);
        }};


        Bundle bundle = new Bundle();
        BridgeArguments.updateBundleWithList(personList, bundle, "testKey");

        List<Boolean> personListCopy = BridgeArguments.getList(bundle.getBooleanArray("testKey"), Boolean.class);
        assertNotNull(personListCopy);
        assertEquals(personList.size(), personListCopy.size());
        for (int i = 0; i < personList.size(); i++) {
            Boolean expected = personList.get(i);
            Boolean actual = personListCopy.get(i);
            assertNotNull(actual);
            assertEquals(expected, actual);
            assertEquals(expected, actual);
        }
    }

    public void testUpdateBundleForListWithFloat() {
        List<Float> personList = new ArrayList<Float>() {{
            add(1.4f);
            add(2.5f);
            add(3.6f);
        }};


        Bundle bundle = new Bundle();
        BridgeArguments.updateBundleWithList(personList, bundle, "testKey");

        List<Float> personListCopy = BridgeArguments.getList(bundle.getFloatArray("testKey"), Float.class);
        assertNotNull(personListCopy);
        assertEquals(personList.size(), personListCopy.size());
        for (int i = 0; i < personList.size(); i++) {
            Float expected = personList.get(i);
            Float actual = personListCopy.get(i);
            assertNotNull(actual);
            assertEquals(expected, actual);
            assertEquals(expected, actual);
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


    public void testModelParcelable() {
        List<Address> addressList = new ArrayList<Address>() {{
            add(new Address.Builder("1235 Walmart Ave", "94085").build());
            add(new Address.Builder("1233 SanBruno Ave", "94075").build());
        }};

        List<String> namesList = new ArrayList<String>() {{
            add("Name1");
            add("Name2");
        }};
        List<Integer> agesList = new ArrayList<Integer>() {{
            add(30);
            add(40);
        }};
        final Person inputPerson = new Person.Builder("testName", 10).addresses(addressList).siblingsNames(namesList).siblingsAges(agesList).build();
        Parcel personParcel = Parcel.obtain();
        inputPerson.writeToParcel(personParcel, inputPerson.describeContents());
        personParcel.setDataPosition(0);

        Person outPerson = Person.CREATOR.createFromParcel(personParcel);
        assertNotNull(outPerson);
        assertEquals(inputPerson.getAddressList().size(), outPerson.getAddressList().size());
        assertEquals(inputPerson.getSiblingsAges().size(), outPerson.getSiblingsAges().size());
        assertEquals(inputPerson.getSiblingsNames().size(), outPerson.getSiblingsNames().size());
        assertEquals(inputPerson.getName(), outPerson.getName());
    }


    public void testNullList() {
        final Person inputPerson = new Person.Builder("testName", 10).addresses(null).siblingsNames(null).siblingsAges(null).build();
        Parcel personParcel = Parcel.obtain();
        inputPerson.writeToParcel(personParcel, inputPerson.describeContents());
        personParcel.setDataPosition(0);

        Person outPerson = Person.CREATOR.createFromParcel(personParcel);
        assertNotNull(outPerson);
        assertEquals(0, outPerson.getAddressList().size());
        assertEquals(0, outPerson.getSiblingsAges().size());
        assertEquals(0, outPerson.getSiblingsNames().size());
        assertEquals(inputPerson.getName(), outPerson.getName());
    }

    public void testNullListItem() {
        Bundle bundle = new Bundle();
        bundle.putString("name", "John");
        bundle.putInt("month", 1);
        bundle.putString("addressList", null);

        Person person = new Person(bundle);
        assertNotNull(person);
    }

}