package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.dto.SupplierDTO;
import lk.ase.kavinda.islandlink.entity.Supplier;
import lk.ase.kavinda.islandlink.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "http://localhost:4200")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping
    public List<SupplierDTO> getAllSuppliers() {
        return supplierService.getAllSuppliers().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/active")
    public List<SupplierDTO> getActiveSuppliers() {
        return supplierService.getActiveSuppliers().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable Long id) {
        return supplierService.getSupplierById(id)
                .map(supplier -> ResponseEntity.ok(convertToDTO(supplier)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public SupplierDTO createSupplier(@RequestBody Supplier supplier) {
        return convertToDTO(supplierService.createSupplier(supplier));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplier) {
        try {
            Supplier updatedSupplier = supplierService.updateSupplier(id, supplier);
            return ResponseEntity.ok(convertToDTO(updatedSupplier));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSupplier(@PathVariable Long id) {
        try {
            supplierService.deleteSupplier(id);
            return ResponseEntity.ok("Supplier deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Cannot delete supplier");
        }
    }

    @GetMapping("/search")
    public List<SupplierDTO> searchSuppliers(@RequestParam String name) {
        return supplierService.searchSuppliers(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private SupplierDTO convertToDTO(Supplier supplier) {
        return new SupplierDTO(
                supplier.getId(),
                supplier.getName(),
                supplier.getContactPerson(),
                supplier.getEmail(),
                supplier.getPhone(),
                supplier.getAddress(),
                supplier.getActive()
        );
    }
}