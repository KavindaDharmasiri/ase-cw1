package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.entity.Return;
import lk.ase.kavinda.islandlink.entity.Complaint;
import lk.ase.kavinda.islandlink.service.ReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/returns")
@CrossOrigin(origins = "http://localhost:4200")
public class ReturnController {

    @Autowired
    private ReturnService returnService;

    @PostMapping("/request")
    public ResponseEntity<Return> createReturnRequest(@RequestBody Map<String, Object> returnRequest) {
        try {
            Return returnEntity = returnService.createReturnRequest(returnRequest);
            return ResponseEntity.ok(returnEntity);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Return>> getUserReturns(@PathVariable Long userId) {
        List<Return> returns = returnService.getUserReturns(userId);
        return ResponseEntity.ok(returns);
    }

    @PostMapping("/complaints")
    public ResponseEntity<Complaint> createComplaint(@RequestBody Map<String, Object> complaintRequest) {
        try {
            Complaint complaint = returnService.createComplaint(complaintRequest);
            return ResponseEntity.ok(complaint);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/complaints/user/{userId}")
    public ResponseEntity<List<Complaint>> getUserComplaints(@PathVariable Long userId) {
        List<Complaint> complaints = returnService.getUserComplaints(userId);
        return ResponseEntity.ok(complaints);
    }

    @PutMapping("/{returnId}/cancel")
    public ResponseEntity<Void> cancelReturn(@PathVariable Long returnId) {
        try {
            returnService.cancelReturn(returnId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}