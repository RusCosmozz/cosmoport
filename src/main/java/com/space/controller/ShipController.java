package com.space.controller;

import com.space.controller.util.RestPreconditions;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import com.space.service.ShipValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/ships")
public class ShipController {

    @Autowired
    private ShipService shipService;

    @Autowired
    private ShipValidator validator;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    protected List<Ship> onShowAllShips(@RequestParam(value = "name", required = false) String name,
                                        @RequestParam(value = "planet", required = false) String planet,
                                        @RequestParam(value = "shipType", required = false) ShipType shipType,
                                        @RequestParam(value = "after", required = false) Long after,
                                        @RequestParam(value = "before", required = false) Long before,
                                        @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                        @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                        @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                        @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                        @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                        @RequestParam(value = "minRating", required = false) Double minRating,
                                        @RequestParam(value = "maxRating", required = false) Double maxRating,
                                        @RequestParam(value = "order", required = false) ShipOrder order,
                                        @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                        @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) throws Exception {

        return shipService.getAll(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize,
                minRating, maxRating, order, pageNumber, pageSize);
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    protected long onGetShipsCount(@RequestParam(value = "name", required = false) String name,
                                   @RequestParam(value = "planet", required = false) String planet,
                                   @RequestParam(value = "shipType", required = false) ShipType shipType,
                                   @RequestParam(value = "after", required = false) Long after,
                                   @RequestParam(value = "before", required = false) Long before,
                                   @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                   @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                   @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                   @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                   @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                   @RequestParam(value = "minRating", required = false) Double minRating,
                                   @RequestParam(value = "maxRating", required = false) Double maxRating) throws Exception {
        return shipService.getCount(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize,
                minRating, maxRating);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    protected Ship onCreateShip(@RequestBody Ship ship, BindingResult errors) {

        validator.validate(ship, errors);


        Ship savedShip = shipService.add(ship);
        return savedShip;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    protected Ship onGetShip(@PathVariable("id") String id) {

        // Если значение id не валидное, необходимо ответить ошибкой с кодом 400.
        RestPreconditions.checkValidID(id);
        //  Если корабль не найден в БД, необходимо ответить ошибкой с кодом 404
        Ship ship = shipService.get(Long.valueOf(id));
        return RestPreconditions.checkFound(ship);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    protected Ship onUpdateShip(@PathVariable("id") String id, @RequestBody Ship resource, BindingResult errors) {

        // Если значение id не валидное, необходимо ответить ошибкой с кодом 400.
        RestPreconditions.checkValidID(id);

        RestPreconditions.checkValid(resource);

        validator.validateParamValues(resource, errors);

        // Если корабль не найден в БД, необходимо ответить ошибкой с кодом 404
        Ship ship = shipService.get(Long.valueOf(id));
        RestPreconditions.checkFound(ship);
        resource.setId(Long.valueOf(id));

        // Обновлять нужно только те поля, которые не null. (возможно, лучше через reflection)
        ship.setName(resource.getName() != null ? resource.getName() : ship.getName());
        ship.setUsed(resource.getUsed() != null ? resource.getUsed() : ship.getUsed());
        ship.setPlanet(resource.getPlanet() != null ? resource.getPlanet() : ship.getPlanet());
        ship.setShipType(resource.getShipType() != null ? resource.getShipType() : ship.getShipType());
        ship.setProdDate(resource.getProdDate() != null ? resource.getProdDate() : ship.getProdDate());
        ship.setSpeed(resource.getSpeed() != null ? resource.getSpeed() : ship.getSpeed());
        ship.setCrewSize(resource.getCrewSize() != null ? resource.getCrewSize() : ship.getCrewSize());

        return shipService.update(ship);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    protected void onDeleteShip(@PathVariable("id") String id) {

        // Если значение id не валидное, необходимо ответить ошибкой с кодом 400.
        RestPreconditions.checkValidID(id);

        // Если корабль не найден в БД, необходимо ответить ошибкой с кодом 404
        Ship ship = shipService.get(Long.valueOf(id));
        RestPreconditions.checkFound(ship);

        shipService.deleteById(Long.valueOf(id));
    }

}
