package com.example.pantera.domain.validators;

import com.example.pantera.domain.Friendship;

public class FriendshipValidator implements Validator<Friendship>{
    @Override
    public void validate(Friendship entity) throws ValidateException {
        String ex = "";
        if(!entity.getId().getLeft().toString().matches("[0-9]+")) ex = ex + "First Id incorrect ";
        if(!entity.getId().getRight().toString().matches("[0-9]+")) ex = ex + "Second Id incorrect ";
        if(ex.length() != 0){
            throw new ValidateException(ex);
        }
    }
}
