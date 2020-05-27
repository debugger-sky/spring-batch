package com.sky.springbatch.util;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class CommonUtil<T> {

    /*
     * Class의 getDeclaredFields 의 name들로 이루어진 String[]을 반환한다.
     * 가져온 DeclaredFields의 갯수가 0이라면, null을 반환한다.
     */
    public String[] declaredFieldsName(Class<T> clazz) {
        Field[] fields = clazz.getDeclaredFields();

        if(fields.length > 0) {
            String[] result = new String[fields.length];
            for(int i=0; i<fields.length; i++) {
                result[i] = fields[i].getName();
            }

            return result;
        }

        return null;
    }
}
