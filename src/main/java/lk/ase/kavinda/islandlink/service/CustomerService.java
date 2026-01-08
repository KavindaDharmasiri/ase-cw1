package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.entity.User;
import lk.ase.kavinda.islandlink.entity.RDC;
import lk.ase.kavinda.islandlink.entity.Role;
import lk.ase.kavinda.islandlink.repository.UserRepository;
import lk.ase.kavinda.islandlink.repository.RDCRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RDCRepository rdcRepository;

    public List<User> getCustomersByRdc(Long rdcId) {
        return userRepository.findByServicingRdcIdAndRoleName(rdcId, Role.RoleName.RETAILER);
    }

    public List<User> getAllCustomers() {
        return userRepository.findByRoleName(Role.RoleName.RETAILER);
    }

    public List<RDC> getAllRDCs() {
        return rdcRepository.findAll();
    }

    public void assignRdcToCustomer(Long customerId, Long rdcId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        RDC rdc = rdcRepository.findById(rdcId)
                .orElseThrow(() -> new RuntimeException("RDC not found"));
        
        customer.setServicingRdc(rdc);
        userRepository.save(customer);
    }
}