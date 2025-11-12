package vn.techbox.techbox_store.product.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoLeadingTrailingSpacesValidator implements ConstraintValidator<NoLeadingTrailingSpaces, String> {

    @Override
    public void initialize(NoLeadingTrailingSpaces constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotBlank handle null values
        }

        // Check if the string starts or ends with whitespace
        return !value.startsWith(" ") && !value.endsWith(" ");
    }
}