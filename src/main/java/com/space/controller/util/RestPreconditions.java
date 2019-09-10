package com.space.controller.util;

public class RestPreconditions {
    public static <T> T checkFound(T resource) {
        if (resource == null)
            throw new ResourceNotFoundException();

        return resource;
    }

    public static <T> T checkValid(T resource) {
        if (resource == null)
            throw new BadRequestException();

        return resource;
    }

    public static void checkValidID(String id) {
        if ((id == null) || (id.equals("")))
            throw new BadRequestException();

        try {
            Long value = Long.valueOf(id);

            if (value <= 0)
                throw new BadRequestException("ID должен быть больше 0!");
        } catch (NumberFormatException e){
            throw new BadRequestException("ID not valid!");
        }
    }
}