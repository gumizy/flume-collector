package com.datacloudsec.core.interceptor;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;
import com.datacloudsec.config.conf.BasicConfigurationConstants;
import com.datacloudsec.config.conf.parser.asset.Asset;
import com.datacloudsec.config.event.CommonConstants;
import com.datacloudsec.config.tools.CommonUtils;
import com.datacloudsec.core.conf.CusEmployeeObject;
import com.datacloudsec.core.conf.DcEnrichConfig;
import com.datacloudsec.core.conf.UserInfoConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @Author gumizy
 * @Date 2018/1/24 16:56
 */
public class CusDepartInterceptor extends AbstractInterceptor implements Interceptor {


    private static final Logger logger = LoggerFactory.getLogger(CusDepartInterceptor.class);

    public CusDepartInterceptor(Context context) {
        String enrichFileds = context.getString(BasicConfigurationConstants.CUS_DEPART_ENRICH_FIELDS);
        if (enrichFileds != null && enrichFileds.trim().length() > 0) {
            this.enrichFields = enrichFileds.split(",");
        }

        String enrichConfigField = context.getString(BasicConfigurationConstants.CUS_DEPART_ENRICH_CONFIG_FIELD);
        if (enrichConfigField != null && enrichConfigField.trim().length() > 0) {
            this.enrichConfigField = enrichConfigField;
        }

        if (this.enrichFields == null || this.enrichFields.length == 0 || this.enrichConfigField.isEmpty() || this.enrichConfigField.trim().length() == 0) {
            logger.error("Enrich base filed or enrich fileds is has not configurate property, oraginal log will be persisted, context :{}" + context);
        }
    }

    @Override
    public void initialize() {

    }

    @Override
    public Event intercept(Event event) {
        Object object = null;
        try {
            object = event.getValue().get(this.enrichConfigField);

            if (object != null && object.toString().trim().length() > 0) {
                // 根据ip资产信息找到用户信息
                Asset asset = DcEnrichConfig.getAssetByIp(String.valueOf(object));
                if (asset != null) {
                    String chargeUser = asset.getChargeUser();
                    // 用户信息
                    Map<String, CusEmployeeObject> cusEmployeeObjects = UserInfoConfig.getCusEmployeeObjects();
                    CusEmployeeObject dataBlock = cusEmployeeObjects.get(chargeUser);
                    if (dataBlock == null) {
                        dataBlock = new CusEmployeeObject();
                        Class<? extends CusEmployeeObject> aClass = dataBlock.getClass();
                        Field[] fields = aClass.getDeclaredFields();
                        for (Field field : fields) {
                            field.setAccessible(true);
                            if (field.getType() == String.class) {
                                field.set(dataBlock, CommonConstants.UNKNOWN);
                            }
                        }
                    }
                    for (String fieldName : enrichFields) {
                        inject(fieldName, dataBlock, event);
                    }
                } else {
                    setDefaultUserInfo(event);
                }

            } else {
                setDefaultUserInfo(event);
            }
        } catch (Exception e) {
            logger.debug("GEO information for ip {} can not be located, so event geo information complated can not be done!", object);
        }
        return event;
    }

    private void setDefaultUserInfo(Event event) throws IllegalAccessException {
        CusEmployeeObject dataBlock = new CusEmployeeObject();
        Class<? extends CusEmployeeObject> aClass = dataBlock.getClass();
        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getType() == String.class) {
                field.set(dataBlock, CommonConstants.UNKNOWN);
            }
        }
        for (String fieldName : enrichFields) {
            try {
                String subfix = CommonUtils.format(fieldName);
                String methodName = "get" + subfix.substring(0, 1).toUpperCase() + subfix.substring(1);
                Method declaredMethod = event.getClass().getDeclaredMethod(methodName);
                if (declaredMethod != null) {
                    Object invoke = declaredMethod.invoke(event);
                    if (invoke != null) {
                        continue;
                    }
                }
            } catch (NoSuchMethodException e) {
                logger.error(e.getMessage());
            } catch (InvocationTargetException e) {
                logger.error(e.getMessage());
            }
            inject(fieldName, dataBlock, event);
        }
    }

    @Override
    public List<Event> intercept(List<Event> events) {
        for (Event event : events) {
            intercept(event);
        }
        return events;
    }

    @Override
    public void close() {

    }

    public static class Builder implements Interceptor.Builder {
        private Context context;

        @Override
        public Interceptor build() {
            return new CusDepartInterceptor(context);
        }

        @Override
        public void configure(Context context) {
            this.context = context;
        }
    }

}
