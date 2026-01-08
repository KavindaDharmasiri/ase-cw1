package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.entity.User;
import lk.ase.kavinda.islandlink.entity.RDC;
import lk.ase.kavinda.islandlink.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "http://localhost:4200")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<User>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/rdc/{rdcId}")
    public ResponseEntity<List<User>> getCustomersByRdc(@PathVariable Long rdcId) {
        return ResponseEntity.ok(customerService.getCustomersByRdc(rdcId));
    }

    @GetMapping("/rdcs")
    public ResponseEntity<List<RDC>> getAllRDCs() {
        return ResponseEntity.ok(customerService.getAllRDCs());
    }

    @PutMapping("/{customerId}/assign-rdc/{rdcId}")
    public ResponseEntity<String> assignRdcToCustomer(@PathVariable Long customerId, @PathVariable Long rdcId) {
        customerService.assignRdcToCustomer(customerId, rdcId);
        return ResponseEntity.ok("RDC assigned successfully");
    }
}