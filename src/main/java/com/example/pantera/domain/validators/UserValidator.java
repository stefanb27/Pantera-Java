package com.example.pantera.domain.validators;

import com.example.pantera.domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidateException {
        String ex = "";
        if(!entity.getFirstName().toString().matches("[A-Za-z]+$")) ex = ex + "Invalid first name ";
        if(!entity.getLastName().toString().matches("[A-Za-z]+$")) ex = ex + "Invalid last name ";
        if(!entity.getId().toString().matches("[0-9]+$")) ex = ex + "Invalid Id ";
        if(ex.length() != 0){
            throw new ValidateException(ex);
        }
    }
}
