package de.csdev.ebus.command.datatypes;

import java.util.Map;

import org.apache.commons.lang.ObjectUtils;

@Deprecated
public abstract class EBusTypeGenericVariant<T> extends EBusTypeGeneric<T> {

    public static String DEFAULT = "std"; // BTI - 3

    protected String variant = DEFAULT;

    @Override
    public EBusTypeGenericVariant<T> getInstance(Map<String, Object> properties) {

        if (properties == null || !properties.containsKey(IEBusType.TYPE)
                || ObjectUtils.equals(properties.get(IEBusType.TYPE), DEFAULT)) {
            return (EBusTypeGenericVariant<T>) super.getInstance(properties);
        }

        String key = (String) properties.get(IEBusType.TYPE) + ":" + isReverseByteOrderSet(properties);
        EBusTypeGenericVariant<T> type = (EBusTypeGenericVariant<T>) otherInstances.get(key);
        if (type == null) {
            type = (EBusTypeGenericVariant<T>) createNewInstance();
            type.variant = (String) properties.get(IEBusType.TYPE);
            applyNewInstanceProperties(type, properties);

            otherInstances.put(key, type);
        }

        return type;
    }

    @Override
    public String toString() {
        return "EBusTypeGenericVariant [variant=" + variant + "]";
    }

}
