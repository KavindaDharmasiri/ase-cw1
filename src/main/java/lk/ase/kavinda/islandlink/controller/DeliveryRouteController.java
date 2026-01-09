package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.entity.DeliveryRoute;
import lk.ase.kavinda.islandlink.entity.Order;
import lk.ase.kavinda.islandlink.service.DeliveryRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/delivery-routes")
@CrossOrigin(origins = "http://localhost:4200")
public class DeliveryRouteController {

    @Autowired
    private DeliveryRouteService deliveryRouteService;

    @GetMapping
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('HEAD_OFFICE_MANAGER')")
    public List<DeliveryRoute> getAllRoutes() {
        return deliveryRouteService.getAllRoutes();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<DeliveryRoute> getRouteById(@PathVariable Long id) {
        return deliveryRouteService.getRouteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('HEAD_OFFICE_MANAGER')")
    public List<DeliveryRoute> getRoutesByStatus(@PathVariable DeliveryRoute.RouteStatus status) {
        return deliveryRouteService.getRoutesByStatus(status);
    }

    @GetMapping("/rdc/{rdcLocation}")
    @PreAuthorize("hasRole('RDC_STAFF') or hasRole('LOGISTICS') or hasRole('HEAD_OFFICE_MANAGER')")
    public List<DeliveryRoute> getRoutesByRdc(@PathVariable String rdcLocation) {
        return deliveryRouteService.getRoutesByRdc(rdcLocation);
    }

    @GetMapping("/driver/{driverName}")
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('HEAD_OFFICE_MANAGER')")
    public List<DeliveryRoute> getRoutesByDriver(@PathVariable String driverName) {
        return deliveryRouteService.getRoutesByDriver(driverName);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('HEAD_OFFICE_MANAGER')")
    public List<DeliveryRoute> getActiveRoutes() {
        return deliveryRouteService.getActiveRoutes();
    }

    @PostMapping
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<DeliveryRoute> createRoute(@RequestBody CreateRouteRequest request) {
        try {
            DeliveryRoute route = new DeliveryRoute();
            route.setRouteName(request.getRouteName());
            route.setRdcLocation(request.getRdcLocation());
            route.setScheduledDate(request.getScheduledDate());
            route.setNotes(request.getNotes());

            DeliveryRoute createdRoute = deliveryRouteService.createRoute(route);
            return ResponseEntity.ok(createdRoute);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<DeliveryRoute> updateRoute(@PathVariable Long id, @RequestBody DeliveryRoute routeDetails) {
        try {
            DeliveryRoute updatedRoute = deliveryRouteService.updateRoute(id, routeDetails);
            return ResponseEntity.ok(updatedRoute);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<DeliveryRoute> updateRouteStatus(@PathVariable Long id, @RequestBody UpdateRouteStatusRequest request) {
        try {
            DeliveryRoute updatedRoute = deliveryRouteService.updateRouteStatus(id, request.getStatus());
            return ResponseEntity.ok(updatedRoute);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<DeliveryRoute> assignDriver(@PathVariable Long id, @RequestBody AssignDriverRequest request) {
        try {
            DeliveryRoute updatedRoute = deliveryRouteService.assignDriver(id, request.getDriverName(), request.getVehicleNumber());
            return ResponseEntity.ok(updatedRoute);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/dispatch")
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<DeliveryRoute> dispatchRoute(@PathVariable Long id) {
        try {
            DeliveryRoute dispatchedRoute = deliveryRouteService.dispatchRoute(id);
            return ResponseEntity.ok(dispatchedRoute);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{routeId}/assign-orders")
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<?> assignOrdersToRoute(@PathVariable Long routeId, @RequestBody AssignOrdersRequest request) {
        try {
            deliveryRouteService.assignOrdersToRoute(routeId, request.getOrderIds());
            return ResponseEntity.ok(Map.of("success", true, "message", "Orders assigned to route"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/{routeId}/orders")
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('RDC_STAFF') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<List<Map<String, Object>>> getRouteOrders(@PathVariable Long routeId) {
        try {
            List<Order> orders = deliveryRouteService.getRouteOrders(routeId);
            List<Map<String, Object>> orderDTOs = orders.stream().map(order -> {
                Map<String, Object> dto = new HashMap<>();
                dto.put("id", order.getId());
                dto.put("orderCode", order.getOrderCode());
                dto.put("status", order.getStatus().toString());
                dto.put("totalAmount", order.getTotalAmount());
                dto.put("deliveryAddress", order.getDeliveryAddress());
                dto.put("customerPhone", order.getCustomerPhone());
                dto.put("storeName", order.getStoreName());
                dto.put("orderDate", order.getOrderDate());
                return dto;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(orderDTOs);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        try {
            deliveryRouteService.deleteRoute(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/stats/count/{status}")
    @PreAuthorize("hasRole('LOGISTICS') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<Long> countRoutesByStatus(@PathVariable DeliveryRoute.RouteStatus status) {
        long count = deliveryRouteService.countRoutesByStatus(status);
        return ResponseEntity.ok(count);
    }

    // DTO classes
    public static class CreateRouteRequest {
        private String routeName;
        private String rdcLocation;
        private LocalDateTime scheduledDate;
        private String notes;

        // Getters and setters
        public String getRouteName() { return routeName; }
        public void setRouteName(String routeName) { this.routeName = routeName; }
        public String getRdcLocation() { return rdcLocation; }
        public void setRdcLocation(String rdcLocation) { this.rdcLocation = rdcLocation; }
        public LocalDateTime getScheduledDate() { return scheduledDate; }
        public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class UpdateRouteStatusRequest {
        private DeliveryRoute.RouteStatus status;

        public DeliveryRoute.RouteStatus getStatus() { return status; }
        public void setStatus(DeliveryRoute.RouteStatus status) { this.status = status; }
    }

    public static class AssignDriverRequest {
        private String driverName;
        private String vehicleNumber;

        public String getDriverName() { return driverName; }
        public void setDriverName(String driverName) { this.driverName = driverName; }
        public String getVehicleNumber() { return vehicleNumber; }
        public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    }

    public static class AssignOrdersRequest {
        private List<Long> orderIds;

        public List<Long> getOrderIds() { return orderIds; }
        public void setOrderIds(List<Long> orderIds) { this.orderIds = orderIds; }
    }
}