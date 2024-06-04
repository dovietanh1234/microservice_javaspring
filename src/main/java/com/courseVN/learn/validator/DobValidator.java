package com.courseVN.learn.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class DobValidator implements ConstraintValidator<DobConstraint, LocalDate> {
    // 2 params -> param 1 la annotation ma cai validator nay se chiu trach nhiem cho
    //             param 2 la kieu du lieu ma ta xu ly trong field request

    // ctrl i de implement interface

    private int min;

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        // isValid la cai ham xu ly cai data nay co dung hay ko?
        // moi annotation chi xu ly 1 cai constrain nhat dinh thui:
        if(Objects.isNull(localDate)){
            return true; // neu nguoi dung ko dien
        }

        // di vao business cua chung ta:
       long years = ChronoUnit.YEARS.between(localDate, LocalDate.now()); // co the tinh toan month, day ...

        return years >= min;
    }

    @Override
    public void initialize(DobConstraint constraintAnnotation) {

        // khi cai constraint nay dc khoi tao thi ta co the get duoc nhung cai thong so cua cai annotation
        ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.min(); // 18 ta da khai bao o @Annotation trong field
    }
}
