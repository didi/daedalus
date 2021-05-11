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

import com.didichuxing.daedalus.util.curl.property.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 *
 */
public abstract class AbstractTestElement implements TestElement, Serializable {
    private static final long serialVersionUID = 241L;

    private static final Logger log = LoggerFactory.getLogger(AbstractTestElement.class);

    private final Map<String, JMeterProperty> propMap =
            Collections.synchronizedMap(new LinkedHashMap<String, JMeterProperty>());

    /**
     * Holds properties added when isRunningVersion is true
     */
    private transient Set<JMeterProperty> temporaryProperties;

    private transient boolean runningVersion = false;



    @Override
    public Object clone() {
        try {
            TestElement clonedElement = this.getClass().getDeclaredConstructor().newInstance();

            PropertyIterator iter = propertyIterator();
            while (iter.hasNext()) {
                clonedElement.setProperty(iter.next().clone());
            }
            clonedElement.setRunningVersion(runningVersion);
            return clonedElement;
        } catch (IllegalArgumentException | ReflectiveOperationException | SecurityException e) {
            throw new AssertionError(e); // clone should never return null
        }
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public void removeProperty(String key) {
        propMap.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof AbstractTestElement) {
            return ((AbstractTestElement) o).propMap.equals(propMap);
        } else {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }


    @Override
    public void setName(String name) {
        setProperty(TestElement.NAME, name);
    }

    @Override
    public String getName() {
        return getPropertyAsString(TestElement.NAME);
    }


    /**
     * Get the named property. If it doesn't exist, a new NullProperty object is
     * created with the same name and returned.
     */
    @Override
    public JMeterProperty getProperty(String key) {
        JMeterProperty prop = propMap.get(key);
        if (prop == null) {
            prop = new NullProperty(key);
        }
        return prop;
    }

    /**
     * Null property are wrapped in a {@link NullProperty}
     * This method avoids this wrapping
     * for internal use only
     *
     * @since 3.1
     */
    private JMeterProperty getRawProperty(String key) {
        return propMap.get(key);
    }


    @Override
    public int getPropertyAsInt(String key) {
        return getProperty(key).getIntValue();
    }

    @Override
    public int getPropertyAsInt(String key, int defaultValue) {
        JMeterProperty jmp = getRawProperty(key);
        return jmp == null || jmp instanceof NullProperty ? defaultValue : jmp.getIntValue();
    }

    @Override
    public boolean getPropertyAsBoolean(String key) {
        return getProperty(key).getBooleanValue();
    }

    @Override
    public boolean getPropertyAsBoolean(String key, boolean defaultVal) {
        JMeterProperty jmp = getRawProperty(key);
        return jmp == null || jmp instanceof NullProperty ? defaultVal : jmp.getBooleanValue();
    }

    @Override
    public float getPropertyAsFloat(String key) {
        return getProperty(key).getFloatValue();
    }

    @Override
    public long getPropertyAsLong(String key) {
        return getProperty(key).getLongValue();
    }

    @Override
    public long getPropertyAsLong(String key, long defaultValue) {
        JMeterProperty jmp = getRawProperty(key);
        return jmp == null || jmp instanceof NullProperty ? defaultValue : jmp.getLongValue();
    }

    @Override
    public double getPropertyAsDouble(String key) {
        return getProperty(key).getDoubleValue();
    }

    @Override
    public String getPropertyAsString(String key) {
        return getProperty(key).getStringValue();
    }

    @Override
    public String getPropertyAsString(String key, String defaultValue) {
        JMeterProperty jmp = getRawProperty(key);
        return jmp == null || jmp instanceof NullProperty ? defaultValue : jmp.getStringValue();
    }

    /**
     * Add property to test element
     *
     * @param property {@link JMeterProperty} to add to current Test Element
     * @param clone    clone property
     */
    protected void addProperty(JMeterProperty property, boolean clone) {
        JMeterProperty propertyToPut = property;
        if (clone) {
            propertyToPut = property.clone();
        }
        if (isRunningVersion()) {
            setTemporary(propertyToPut);
        } else {
            clearTemporary(property);
        }
        JMeterProperty prop = getProperty(property.getName());

        if (prop instanceof NullProperty || (prop instanceof StringProperty && prop.getStringValue().isEmpty())) {
            propMap.put(property.getName(), propertyToPut);
        } else {
            prop.mergeIn(propertyToPut);
        }
    }

    /**
     * Add property to test element without cloning it
     *
     * @param property {@link JMeterProperty}
     */
    protected void addProperty(JMeterProperty property) {
        addProperty(property, false);
    }

    /**
     * Remove property from temporaryProperties
     *
     * @param property {@link JMeterProperty}
     */
    protected void clearTemporary(JMeterProperty property) {
        if (temporaryProperties != null) {
            temporaryProperties.remove(property);
        }
    }


    @Override
    public void setProperty(JMeterProperty property) {
        if (isRunningVersion()) {
            if (getProperty(property.getName()) instanceof NullProperty) {
                addProperty(property);
            } else {
                getProperty(property.getName()).setObjectValue(property.getObjectValue());
            }
        } else {
            propMap.put(property.getName(), property);
        }
    }

    @Override
    public void setProperty(String name, String value) {
        setProperty(new StringProperty(name, value));
    }

    /**
     * Create a String property - but only if it is not the default.
     * This is intended for use when adding new properties to JMeter
     * so that JMX files are not expanded unnecessarily.
     * <p>
     * N.B. - must agree with the default applied when reading the property.
     *
     * @param name  property name
     * @param value current value
     * @param dflt  default
     */
    @Override
    public void setProperty(String name, String value, String dflt) {
        if (dflt.equals(value)) {
            removeProperty(name);
        } else {
            setProperty(new StringProperty(name, value));
        }
    }

    @Override
    public void setProperty(String name, boolean value) {
        setProperty(new BooleanProperty(name, value));
    }

    /**
     * Create a boolean property - but only if it is not the default.
     * This is intended for use when adding new properties to JMeter
     * so that JMX files are not expanded unnecessarily.
     * <p>
     * N.B. - must agree with the default applied when reading the property.
     *
     * @param name  property name
     * @param value current value
     * @param dflt  default
     */
    @Override
    public void setProperty(String name, boolean value, boolean dflt) {
        if (value == dflt) {
            removeProperty(name);
        } else {
            setProperty(new BooleanProperty(name, value));
        }
    }

    @Override
    public void setProperty(String name, int value) {
        setProperty(new IntegerProperty(name, value));
    }

    /**
     * Create an int property - but only if it is not the default.
     * This is intended for use when adding new properties to JMeter
     * so that JMX files are not expanded unnecessarily.
     * <p>
     * N.B. - must agree with the default applied when reading the property.
     *
     * @param name  property name
     * @param value current value
     * @param dflt  default
     */
    @Override
    public void setProperty(String name, int value, int dflt) {
        if (value == dflt) {
            removeProperty(name);
        } else {
            setProperty(new IntegerProperty(name, value));
        }
    }

    @Override
    public void setProperty(String name, long value) {
        setProperty(new LongProperty(name, value));
    }

    /**
     * Create a long property - but only if it is not the default.
     * This is intended for use when adding new properties to JMeter
     * so that JMX files are not expanded unnecessarily.
     * <p>
     * N.B. - must agree with the default applied when reading the property.
     *
     * @param name  property name
     * @param value current value
     * @param dflt  default
     */
    @Override
    public void setProperty(String name, long value, long dflt) {
        if (value == dflt) {
            removeProperty(name);
        } else {
            setProperty(new LongProperty(name, value));
        }
    }

    @Override
    public PropertyIterator propertyIterator() {
        return new PropertyIteratorImpl(propMap.values());
    }

    /**
     * Add to this the properties of element (by reference)
     *
     * @param element {@link TestElement}
     */
    protected void mergeIn(TestElement element) {
        PropertyIterator iter = element.propertyIterator();
        while (iter.hasNext()) {
            JMeterProperty prop = iter.next();
            addProperty(prop, false);
        }
    }

    /**
     * Returns the runningVersion.
     */
    @Override
    public boolean isRunningVersion() {
        return runningVersion;
    }

    /**
     * Sets the runningVersion.
     *
     * @param runningVersion the runningVersion to set
     */
    @Override
    public void setRunningVersion(boolean runningVersion) {
        this.runningVersion = runningVersion;
        PropertyIterator iter = propertyIterator();
        while (iter.hasNext()) {
            iter.next().setRunningVersion(runningVersion);
        }
    }




    /**
     * Clears temporaryProperties
     */
    protected void emptyTemporary() {
        if (temporaryProperties != null) {
            temporaryProperties.clear();
        }
    }




    /**
     * {@inheritDoc}
     */
    @Override
    public void setTemporary(JMeterProperty property) {
        if (temporaryProperties == null) {
            temporaryProperties = new LinkedHashSet<>();
        }
        temporaryProperties.add(property);


    }


    public AbstractTestElement() {
        super();
    }






}
