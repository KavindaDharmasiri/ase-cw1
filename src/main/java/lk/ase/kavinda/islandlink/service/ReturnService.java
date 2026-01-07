package lk.ase.kavinda.islandlink.service;

import lk.ase.kavinda.islandlink.entity.Return;
import lk.ase.kavinda.islandlink.entity.Complaint;
import lk.ase.kavinda.islandlink.repository.ReturnRepository;
import lk.ase.kavinda.islandlink.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ReturnService {

    @Autowired
    private ReturnRepository returnRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    public Return createReturnRequest(Map<String, Object> returnRequest) {
        Return returnEntity = new Return();
        returnEntity.setOrderId(Long.valueOf(returnRequest.get("orderId").toString()));
        returnEntity.setProductId(Long.valueOf(returnRequest.get("productId").toString()));
        returnEntity.setReason(returnRequest.get("reason").toString());
        returnEntity.setDescription(returnRequest.get("description").toString());
        
        return returnRepository.save(returnEntity);
    }

    public List<Return> getUserReturns(Long userId) {
        return returnRepository.findByUserId(userId);
    }

    public Complaint createComplaint(Map<String, Object> complaintRequest) {
        Complaint complaint = new Complaint();
        complaint.setUserId(Long.valueOf(complaintRequest.get("userId").toString()));
        complaint.setSubject(complaintRequest.get("subject").toString());
        complaint.setCategory(complaintRequest.get("category").toString());
        complaint.setDescription(complaintRequest.get("description").toString());
        
        return complaintRepository.save(complaint);
    }

    public List<Complaint> getUserComplaints(Long userId) {
        return complaintRepository.findByUserId(userId);
    }

    public void cancelReturn(Long returnId) {
        Return returnEntity = returnRepository.findById(returnId).orElse(null);
        if (returnEntity != null && returnEntity.getStatus() == Return.ReturnStatus.PENDING) {
            returnRepository.delete(returnEntity);
        }
    }
}