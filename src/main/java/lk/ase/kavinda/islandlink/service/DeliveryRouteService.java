package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.entity.Delivery;
import lk.ase.kavinda.islandlink.entity.DeliveryRoute;
import lk.ase.kavinda.islandlink.repository.DeliveryRepository;
import lk.ase.kavinda.islandlink.repository.DeliveryRouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryRouteService {

    @Autowired
    private DeliveryRouteRepository deliveryRouteRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    public List<DeliveryRoute> getAllRoutes() {
        return deliveryRouteRepository.findAll();
    }

    public Optional<DeliveryRoute> getRouteById(Long id) {
        return deliveryRouteRepository.findById(id);
    }

    public List<DeliveryRoute> getRoutesByStatus(DeliveryRoute.RouteStatus status) {
        return deliveryRouteRepository.findByStatus(status);
    }

    public List<DeliveryRoute> getRoutesByRdc(String rdcLocation) {
        return deliveryRouteRepository.findByRdcLocation(rdcLocation);
    }

    public List<DeliveryRoute> getRoutesByDriver(String driverName) {
        return deliveryRouteRepository.findByDriverName(driverName);
    }

    public List<DeliveryRoute> getActiveRoutes() {
        return deliveryRouteRepository.findByStatusIn(List.of(
            DeliveryRoute.RouteStatus.PLANNED,
            DeliveryRoute.RouteStatus.IN_PROGRESS
        ));
    }

    @Transactional
    public DeliveryRoute createRoute(DeliveryRoute route) {
        route.setStatus(DeliveryRoute.RouteStatus.PLANNED);
        return deliveryRouteRepository.save(route);
    }

    @Transactional
    public DeliveryRoute updateRoute(Long id, DeliveryRoute routeDetails) {
        DeliveryRoute route = deliveryRouteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        route.setRouteName(routeDetails.getRouteName());
        route.setDriverName(routeDetails.getDriverName());
        route.setVehicleNumber(routeDetails.getVehicleNumber());
        route.setRdcLocation(routeDetails.getRdcLocation());
        route.setScheduledDate(routeDetails.getScheduledDate());
        route.setNotes(routeDetails.getNotes());

        return deliveryRouteRepository.save(route);
    }

    @Transactional
    public DeliveryRoute updateRouteStatus(Long id, DeliveryRoute.RouteStatus status) {
        DeliveryRoute route = deliveryRouteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        route.setStatus(status);

        // If route is completed, update all associated deliveries
        if (status == DeliveryRoute.RouteStatus.COMPLETED) {
            List<Delivery> deliveries = deliveryRepository.findByDeliveryRouteId(id);
            deliveries.forEach(delivery -> {
                if (delivery.getStatus() != Delivery.DeliveryStatus.DELIVERED) {
                    delivery.setStatus(Delivery.DeliveryStatus.DELIVERED);
                    delivery.setActualDeliveryDate(LocalDateTime.now());
                }
            });
            deliveryRepository.saveAll(deliveries);
        }

        return deliveryRouteRepository.save(route);
    }

    @Transactional
    public DeliveryRoute assignDriver(Long routeId, String driverName, String vehicleNumber) {
        DeliveryRoute route = deliveryRouteRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        route.setDriverName(driverName);
        route.setVehicleNumber(vehicleNumber);

        return deliveryRouteRepository.save(route);
    }

    @Transactional
    public DeliveryRoute dispatchRoute(Long routeId) {
        DeliveryRoute route = deliveryRouteRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        if (route.getDriverName() == null || route.getVehicleNumber() == null) {
            throw new RuntimeException("Cannot dispatch route without driver and vehicle assignment");
        }

        route.setStatus(DeliveryRoute.RouteStatus.IN_PROGRESS);

        // Update all associated deliveries to IN_TRANSIT
        List<Delivery> deliveries = deliveryRepository.findByDeliveryRouteId(routeId);
        deliveries.forEach(delivery -> delivery.setStatus(Delivery.DeliveryStatus.IN_TRANSIT));
        deliveryRepository.saveAll(deliveries);

        return deliveryRouteRepository.save(route);
    }

    @Transactional
    public void deleteRoute(Long id) {
        DeliveryRoute route = deliveryRouteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        if (route.getStatus() == DeliveryRoute.RouteStatus.IN_PROGRESS) {
            throw new RuntimeException("Cannot delete route that is in progress");
        }

        deliveryRouteRepository.delete(route);
    }

    public List<DeliveryRoute> getRoutesForOptimization(String rdcLocation, LocalDateTime date) {
        return deliveryRouteRepository.findActiveRoutesFromDate(date);
    }

    public long countRoutesByStatus(DeliveryRoute.RouteStatus status) {
        return deliveryRouteRepository.countByStatus(status);
    }
}