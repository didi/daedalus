/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.didichuxing.daedalus.util.curl;


import com.didichuxing.daedalus.util.curl.property.JMeterProperty;
import com.didichuxing.daedalus.util.curl.property.PropertyIterator;

public interface TestElement extends Cloneable {
    String NAME = "TestElement.name"; //$NON-NLS-1$






    void setProperty(String key, String value);

    void setProperty(String key, String value, String dflt);

    void setProperty(String key, boolean value);

    void setProperty(String key, boolean value, boolean dflt);

    void setProperty(String key, int value);

    void setProperty(String key, int value, int dflt);

    void setProperty(String name, long value);

    void setProperty(String name, long value, long dflt);


    /**
     * Returns true or false whether the element is the running version.
     *
     * @return <code>true</code> if the element is the running version
     */
    boolean isRunningVersion();


    /**
     * Indicate that the given property should be only a temporary property in
     * the TestElement
     *
     * @param property void
     */
    void setTemporary(JMeterProperty property);

    /**
     * Return a property as a boolean value.
     *
     * @param key the name of the property to get
     * @return the value of the property
     */
    boolean getPropertyAsBoolean(String key);

    /**
     * Return a property as a boolean value or a default value if no property
     * could be found.
     *
     * @param key          the name of the property to get
     * @param defaultValue the default value to use
     * @return the value of the property, or <code>defaultValue</code> if no
     * property could be found
     */
    boolean getPropertyAsBoolean(String key, boolean defaultValue);

    /**
     * Return a property as a long value.
     *
     * @param key the name of the property to get
     * @return the value of the property
     */
    long getPropertyAsLong(String key);

    /**
     * Return a property as a long value or a default value if no property
     * could be found.
     *
     * @param key          the name of the property to get
     * @param defaultValue the default value to use
     * @return the value of the property, or <code>defaultValue</code> if no
     * property could be found
     */
    long getPropertyAsLong(String key, long defaultValue);

    /**
     * Return a property as an int value.
     *
     * @param key the name of the property to get
     * @return the value of the property
     */
    int getPropertyAsInt(String key);

    /**
     * Return a property as an int value or a default value if no property
     * could be found.
     *
     * @param key          the name of the property to get
     * @param defaultValue the default value to use
     * @return the value of the property, or <code>defaultValue</code> if no
     * property could be found
     */
    int getPropertyAsInt(String key, int defaultValue);

    /**
     * Return a property as a float value.
     *
     * @param key the name of the property to get
     * @return the value of the property
     */
    float getPropertyAsFloat(String key);

    /**
     * Return a property as a double value.
     *
     * @param key the name of the property to get
     * @return the value of the property
     */
    double getPropertyAsDouble(String key);

    /**
     * Make the test element the running version, or make it no longer the
     * running version. This tells the test element that it's current state must
     * be retrievable by a call to recoverRunningVersion(). It is kind of like
     * making the TestElement Read- Only, but not as strict. Changes can be made
     * and the element can be modified, but the state of the element at the time
     * of the call to setRunningVersion() must be recoverable.
     *
     * @param run flag whether this element should be the running version
     */
    void setRunningVersion(boolean run);


    /**
     * Return a property as a string value.
     *
     * @param key the name of the property to get
     * @return the value of the property
     */
    String getPropertyAsString(String key);

    /**
     * Return a property as an string value or a default value if no property
     * could be found.
     *
     * @param key          the name of the property to get
     * @param defaultValue the default value to use
     * @return the value of the property, or <code>defaultValue</code> if no
     * property could be found
     */
    String getPropertyAsString(String key, String defaultValue);

    /**
     * Sets and overwrites a property in the TestElement. This call will be
     * ignored if the TestElement is currently a "running version".
     *
     * @param property the property to be set
     */
    void setProperty(JMeterProperty property);


    JMeterProperty getProperty(String propName);

    /**
     * Get a Property Iterator for the TestElements properties.
     *
     * @return PropertyIterator
     */
    PropertyIterator propertyIterator();

    /**
     * Remove property stored under the <code>key</code>
     *
     * @param key name of the property to be removed
     */
    void removeProperty(String key);

    // lifecycle methods

    Object clone();


    /**
     * Get the name of this test element
     *
     * @return name of this element
     */
    String getName();

    /**
     * @param name of this element
     */
    void setName(String name);


}
