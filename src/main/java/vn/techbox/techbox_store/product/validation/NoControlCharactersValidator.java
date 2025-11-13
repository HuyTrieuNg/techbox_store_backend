package vn.techbox.techbox_store.product.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoControlCharactersValidator implements ConstraintValidator<NoControlCharacters, String> {

    @Override
    public void initialize(NoControlCharacters constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotBlank handle null values
        }

        // Check for control characters (0-31 and 127-159)
        for (char c : value.toCharArray()) {
            if (c < 32 || (c >= 127 && c < 160)) {
                return false;
            }
        }

        return true;
    }
}