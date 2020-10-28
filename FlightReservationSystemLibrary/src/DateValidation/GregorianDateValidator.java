/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DateValidation;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author sohqi
 */
public class GregorianDateValidator
        implements ConstraintValidator<GregorianDateValidate, GregorianCalendar> {

    public final void initialize(final GregorianDateValidate annotation) {
    }

    public final boolean isValid(final GregorianCalendar gregorianCalendar,
            final ConstraintValidatorContext context) {

        // Only use the date for comparison
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Date today = calendar.getTime();

        // Your date must be after today or today (== not before today)
        return !gregorianCalendar.before(today) || gregorianCalendar.after(today);

    }
}
