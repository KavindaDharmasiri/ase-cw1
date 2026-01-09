package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.service.PickListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/picklist")
@CrossOrigin(origins = "http://localhost:4200")
public class PickListController {

    @Autowired
    private PickListService pickListService;

    @PostMapping("/order/{orderId}")
    @PreAuthorize("hasRole('RDC_STAFF') or hasRole('HEAD_OFFICE_MANAGER')")
    public ResponseEntity<PickListService.PickListDTO> generatePickListForOrder(@PathVariable Long orderId) {
        PickListService.PickListDTO pickList = pickListService.generatePickListForOrder(orderId);
        return ResponseEntity.ok(pickList);
    }
}