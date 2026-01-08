package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.entity.*;
import lk.ase.kavinda.islandlink.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class WarehouseService {

    @Autowired
    private PickListRepository pickListRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private DriverRepository driverRepository;

    public List<PickList> getAllPickLists() {
        return pickListRepository.findAll();
    }

    public Optional<PickList> getPickListById(Long id) {
        return pickListRepository.findById(id);
    }

    @Transactional
    public PickList createPickList(PickList pickList) {
        // Generate pick list number
        String pickListNumber = generatePickListNumber();
        pickList.setPickListNumber(pickListNumber);
        
        return pickListRepository.save(pickList);
    }

    @Transactional
    public PickList updatePickListStatus(Long id, PickList.PickListStatus status) {
        PickList pickList = pickListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pick List not found"));
        
        pickList.setStatus(status);
        
        switch (status) {
            case PICKED:
                pickList.setPickedDate(LocalDateTime.now());
                break;
            case LOADED:
                pickList.setLoadedDate(LocalDateTime.now());
                break;
            case DISPATCHED:
                pickList.setDispatchedDate(LocalDateTime.now());
                // Update order statuses to OUT_FOR_DELIVERY
                updateOrderStatuses(pickList, Order.OrderStatus.OUT_FOR_DELIVERY);
                break;
        }
        
        return pickListRepository.save(pickList);
    }

    public List<PickList> getPickListsByRdc(Long rdcId) {
        RDC rdc = new RDC();
        rdc.setId(rdcId);
        return pickListRepository.findByRdc(rdc);
    }

    public List<PickList> getPickListsByStatus(PickList.PickListStatus status) {
        return pickListRepository.findByStatus(status);
    }

    @Transactional
    public PickList assignVehicleAndDriver(Long pickListId, Long vehicleId, Long driverId) {
        PickList pickList = pickListRepository.findById(pickListId)
                .orElseThrow(() -> new RuntimeException("Pick List not found"));
        
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        
        pickList.setVehicle(vehicle);
        pickList.setDriver(driver);
        
        return pickListRepository.save(pickList);
    }

    private String generatePickListNumber() {
        String prefix = "PL" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String maxPickListNumber = pickListRepository.findMaxPickListNumberByPrefix(prefix);
        
        if (maxPickListNumber == null) {
            return prefix + "001";
        }
        
        int nextNumber = Integer.parseInt(maxPickListNumber.substring(prefix.length())) + 1;
        return prefix + String.format("%03d", nextNumber);
    }

    private void updateOrderStatuses(PickList pickList, Order.OrderStatus status) {
        for (PickListItem item : pickList.getItems()) {
            Order order = item.getOrder();
            order.setStatus(status);
            orderRepository.save(order);
        }
    }
}