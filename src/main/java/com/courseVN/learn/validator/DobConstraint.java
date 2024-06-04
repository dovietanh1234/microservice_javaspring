package com.courseVN.learn.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
// target annotation se dc apply o dau? -> ta chi apply trong field ko trong method and class -> remove di
@Retention(RetentionPolicy.RUNTIME) // cai annotation nay se dc xu ly luc nao -> luc RUNTIME -> annotation se duoc maping vao
// annotation cua lombok xu ly luc compiler time do!
@Constraint(
        validatedBy = { DobValidator.class } // khai bao class validator ma ta tu custom vao day
) // cai class chiu trach nhiem validate cho cai annotation nay
public @interface DobConstraint {

    String message() default "INVALID DATE OF BIRTH";

    // tao mot property customize: cho phep khi su dung se validate theo so tuoi la bao nhieu:

    int min(); // day ms chi dung lai o khai bao de xu ly ta can co mot class xu ly dua vao trong "validatedBy()"


    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /*
    * day la 3 thuoc tinh co ban cua annotation -> danh cho validation
    * */


}
