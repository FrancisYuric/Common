package com.app.annotation.custom;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CustomDateView/CustomVerticalDateView edit result
 * Created by wangshizhan on 2018/10/26
 * Email:wangshizhan@supcom.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OnDateChange {

    String param();
}
