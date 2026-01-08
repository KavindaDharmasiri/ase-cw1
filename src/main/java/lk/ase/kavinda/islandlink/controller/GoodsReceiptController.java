package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.entity.GoodsReceiptNote;
import lk.ase.kavinda.islandlink.service.GoodsReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/goods-receipt")
@CrossOrigin(origins = "http://localhost:4200")
public class GoodsReceiptController {

    @Autowired
    private GoodsReceiptService goodsReceiptService;

    @GetMapping
    public List<GoodsReceiptNote> getAllGoodsReceiptNotes() {
        return goodsReceiptService.getAllGoodsReceiptNotes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoodsReceiptNote> getGoodsReceiptNoteById(@PathVariable Long id) {
        return goodsReceiptService.getGoodsReceiptNoteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public GoodsReceiptNote createGoodsReceiptNote(@RequestBody GoodsReceiptNote grn) {
        return goodsReceiptService.createGoodsReceiptNote(grn);
    }

    @GetMapping("/rdc/{rdcId}")
    public List<GoodsReceiptNote> getGrnsByRdc(@PathVariable Long rdcId) {
        return goodsReceiptService.getGrnsByRdc(rdcId);
    }

    @GetMapping("/purchase-order/{poId}")
    public List<GoodsReceiptNote> getGrnsByPurchaseOrder(@PathVariable Long poId) {
        return goodsReceiptService.getGrnsByPurchaseOrder(poId);
    }
}