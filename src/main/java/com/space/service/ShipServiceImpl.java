package com.space.service;

import com.space.controller.ShipOrder;
import com.space.controller.util.DateUtils;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ShipServiceImpl implements ShipService {

    @Override
    public Ship get(long id) {
        return shipRepository.findById(id).orElse(null);
    }

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    private ShipRepository shipRepository;

    @Autowired
    public void setShipRepository(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public Ship add(Ship ship) {
        // Если в запросе на создание корабля нет параметра “isUsed”, то считаем, что пришло значение “false”.
        ship.setUsed(ship.getUsed()!= null ? ship.getUsed() : false);
        Double rating = calcRating(ship.getUsed(), ship.getProdDate(), ship.getSpeed());
        ship.setRating(rating);

        Ship savedShip = shipRepository.saveAndFlush(ship);
        return savedShip;
    }

    // Расчет рейтинга корабля
    private Double calcRating(Boolean isUsed, Date prodDate, Double speed) {

        Double k = isUsed ? 0.5 : 1;
        Calendar cal = Calendar.getInstance();
        cal.setTime(prodDate);
        BigDecimal rating = new BigDecimal((80 * speed * k) / (DateUtils.getCurrentYear() - cal.get(Calendar.YEAR) + 1));
        rating = rating.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        return rating.doubleValue();
    }

    @Override
    public void deleteById(long id) {
        shipRepository.deleteById(id);
    }

    @Override
    public Ship update(Ship ship) {
        Double rating = calcRating(ship.getUsed(), ship.getProdDate(), ship.getSpeed());
        ship.setRating(rating);
        return shipRepository.saveAndFlush(ship);
    }


    @Override
    public List<Ship> getAll() {
        return shipRepository.findAll();
    }

    @Override
    public Ship getByName(String name) {
        return shipRepository.findByName(name);
    }

    @Override
    public long getCount() {
        return shipRepository.count();
    }

    public Predicate[] getFilters(CriteriaBuilder cb, Root<Ship> root,
                                  String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed,
                                  Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize,
                                  Double minRating, Double maxRating) {
        final List<Predicate> predicates = new ArrayList<>();

        if (name != null)
            predicates.add(cb.like(root.get("name"), "%" + name + "%"));

        if (planet != null)
            predicates.add(cb.like(root.get("planet"), "%" + planet + "%"));

        if (shipType != null)
            predicates.add(cb.equal(root.get("shipType"), shipType));

        if (isUsed != null)
            predicates.add(cb.equal(root.get("isUsed"), isUsed));

        // Дата выпуска
        if (after != null)
            predicates.add(cb.greaterThanOrEqualTo(root.get("prodDate").as(Date.class), new Date(after)));

        if (before != null)
            predicates.add(cb.lessThanOrEqualTo(root.get("prodDate").as(Date.class), new Date(before)));

        // Максимальная скорость
        if (minSpeed != null)
            predicates.add(cb.ge(root.get("speed"), minSpeed));

        if (maxSpeed != null)
            predicates.add(cb.le(root.get("speed"), maxSpeed));

        // Количество членов экипажа ок
        if (minCrewSize != null)
            predicates.add(cb.ge(root.get("crewSize"), minCrewSize));

        if (maxCrewSize != null)
            predicates.add(cb.le(root.get("crewSize"), maxCrewSize));

        //Рейтинг корабля
        if (minRating != null)
            predicates.add(cb.ge(root.get("rating"), minRating));

        if (maxRating != null)
            predicates.add(cb.le(root.get("rating"), maxRating));

        return predicates.toArray(new Predicate[predicates.size()]);
    }

    @Override
    public long getCount(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed,
                         Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize,
                         Double minRating, Double maxRating) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Ship> root = query.from(Ship.class);

        Predicate[] predicates = getFilters(cb, root, name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

        query.select(cb.count(root));
        query.where(predicates);

        return entityManager.createQuery(query).getSingleResult();
    }

    @Override
    public List<Ship> getAll(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed,
                             Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize,
                             Double minRating, Double maxRating, ShipOrder order, Integer pageNumber, Integer pageSize) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Ship> query = cb.createQuery(Ship.class);
        Root<Ship> root = query.from(Ship.class);

        Predicate[] predicates = getFilters(cb, root, name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

        query.where(predicates);

        // Порядок сортировки
        if (order != null)
            query.orderBy(cb.asc(root.get(order.getFieldName())));

        CriteriaQuery<Ship> select = query.select(root);
        TypedQuery<Ship> typedQuery = entityManager.createQuery(select);
        typedQuery.setFirstResult(pageNumber * pageSize);
        typedQuery.setMaxResults(pageSize);

        return typedQuery.getResultList();
    }
}
