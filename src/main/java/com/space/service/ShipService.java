package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;

import java.util.Date;
import java.util.List;

public interface ShipService {

    Ship add (Ship ship);
    Ship get(long id);

//    Ship add(String name, String planet, ShipType shipType, Date prodDate, Boolean isUsed, Double speed, Integer crewSize);

    void deleteById(long id);

    Ship update(Ship ship);

    List<Ship> getAll();

    long getCount();

    Ship getByName(String name);

    long getCount(String name,
                  String planet,
                  ShipType shipType,
                  Long after,
                  Long before,
                  Boolean isUsed,
                  Double minSpeed,
                  Double maxSpeed,
                  Integer minCrewSize,
                  Integer maxCrewSize,
                  Double minRating,
                  Double maxRating);

    List<Ship> getAll(String name,
                      String planet,
                      ShipType shipType,
                      Long after,
                      Long before,
                      Boolean isUsed,
                      Double minSpeed,
                      Double maxSpeed,
                      Integer minCrewSize,
                      Integer maxCrewSize,
                      Double minRating,
                      Double maxRating,
                      ShipOrder order,
                      Integer pageNumber,
                      Integer pageSize);
}
