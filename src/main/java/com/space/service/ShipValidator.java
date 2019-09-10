package com.space.service;

import com.space.controller.util.BadRequestException;
import com.space.controller.util.DateUtils;
import com.space.controller.util.RestPreconditions;
import com.space.model.Ship;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Calendar;

@Service
public class ShipValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Ship.class);
    }

    public void validateParamValues(Object target, Errors errors) {
        Ship ship = (Ship) target;

        //TODO: use hibernate-validator annotation

        if ((ship.getName() != null) && ((ship.getName().length() < 1) || (ship.getName().length() > 50)))
            throw new BadRequestException("Длина значения параметра name должна быть в пределах 1..50 символов");

        if ((ship.getPlanet() != null) && ((ship.getPlanet().length() < 1) || (ship.getPlanet().length() > 50)))
            throw new BadRequestException("Длина значения параметра planet должна быть в пределах 1..50 символов");

        if ((ship.getCrewSize() != null) && ((ship.getCrewSize() < 1) || (ship.getCrewSize() > 9999)))
            throw new BadRequestException("Параметр crewSize должен быть быть в пределах 1..9999");

        if ((ship.getSpeed() != null) && ((ship.getSpeed() < 0.01D) || (ship.getSpeed() > 0.99D)))
            throw new BadRequestException("Параметр speed должен быть быть в пределах 0,01..0,99");

        if (ship.getProdDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(ship.getProdDate());
            if ((calendar.get(Calendar.YEAR) < 2800) || (calendar.get(Calendar.YEAR) > DateUtils.getCurrentYear()))
                throw new BadRequestException("Дата выпуска должна быть больше в пределах 2800.." + DateUtils.getCurrentYear());
        }
    }

    @Override
    public void validate(Object target, Errors errors) {
        Ship ship = (Ship) target;

        RestPreconditions.checkValid(ship.getName());
        RestPreconditions.checkValid(ship.getPlanet());
        RestPreconditions.checkValid(ship.getShipType());
        RestPreconditions.checkValid(ship.getProdDate());
        RestPreconditions.checkValid(ship.getSpeed());
        RestPreconditions.checkValid(ship.getCrewSize());

        validateParamValues(target, errors);

    }
}
