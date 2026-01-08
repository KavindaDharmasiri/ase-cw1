package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.entity.PickList;
import lk.ase.kavinda.islandlink.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
@CrossOrigin(origins = "http://localhost:4200")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @GetMapping("/pick-lists")
    public List<PickList> getAllPickLists() {
        return warehouseService.getAllPickLists();
    }

    @GetMapping("/pick-lists/{id}")
    public ResponseEntity<PickList> getPickListById(@PathVariable Long id) {
        return warehouseService.getPickListById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/pick-lists")
    public PickList createPickList(@RequestBody PickList pickList) {
        return warehouseService.createPickList(pickList);
    }

    @PutMapping("/pick-lists/{id}/status")
    public ResponseEntity<PickList> updatePickListStatus(
            @PathVariable Long id, 
            @RequestParam PickList.PickListStatus status) {
        try {
            PickList updatedPickList = warehouseService.updatePickListStatus(id, status);
            return ResponseEntity.ok(updatedPickList);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/pick-lists/{id}/assign")
    public ResponseEntity<PickList> assignVehicleAndDriver(
            @PathVariable Long id,
            @RequestParam Long vehicleId,
            @RequestParam Long driverId) {
        try {
            PickList updatedPickList = warehouseService.assignVehicleAndDriver(id, vehicleId, driverId);
            return ResponseEntity.ok(updatedPickList);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/pick-lists/rdc/{rdcId}")
    public List<PickList> getPickListsByRdc(@PathVariable Long rdcId) {
        return warehouseService.getPickListsByRdc(rdcId);
    }

    @GetMapping("/pick-lists/status/{status}")
    public List<PickList> getPickListsByStatus(@PathVariable PickList.PickListStatus status) {
        return warehouseService.getPickListsByStatus(status);
    }
}