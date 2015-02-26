package flexjson.factories;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

import java.lang.reflect.Type;
import java.math.BigDecimal;

public class BigDecimalFactory implements ObjectFactory {

    @Override
    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
        if( value instanceof Number ) {
            return new BigDecimal( value.toString() );
        } else {
            return new BigDecimal( value.toString() );
        }
    }
}
