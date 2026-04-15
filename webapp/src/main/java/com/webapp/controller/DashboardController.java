package com.webapp.controller;

import com.webapp.model.DataRecord;
import com.webapp.model.User;
import com.webapp.service.S3Service;
import com.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class DashboardController {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private UserService userService;

    // ─── Dashboard ──────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        String username = auth.getName();
        User user = userService.findByUsername(username);

        List<DataRecord> userRecords = s3Service.getRecordsByUser(username);
        long totalRecords = s3Service.getRecordCount();

        model.addAttribute("user", user);
        model.addAttribute("userRecords", userRecords);
        model.addAttribute("totalRecords", totalRecords);
        model.addAttribute("myRecordCount", userRecords.size());
        model.addAttribute("totalUsers", userService.getUserCount());

        return "dashboard";
    }

    // ─── View All Records ───────────────────────────────────────────
    @GetMapping("/records")
    public String viewRecords(Authentication auth, Model model) {
        String username = auth.getName();
        User user = userService.findByUsername(username);
        List<DataRecord> records = s3Service.getAllRecords();

        model.addAttribute("user", user);
        model.addAttribute("records", records);
        return "records";
    }

    // ─── Create Record Page ─────────────────────────────────────────
    @GetMapping("/records/new")
    public String createRecordPage(Authentication auth, Model model) {
        String username = auth.getName();
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "create-record";
    }

    // ─── Submit New Record ──────────────────────────────────────────
    @PostMapping("/records/create")
    public String createRecord(@RequestParam String title,
                               @RequestParam String category,
                               @RequestParam String description,
                               @RequestParam String status,
                               Authentication auth,
                               RedirectAttributes redirectAttributes) {
        try {
            String id = UUID.randomUUID().toString();
            DataRecord record = new DataRecord(id, title, category, description,
                    status, auth.getName());
            s3Service.saveRecord(record);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Record '" + title + "' saved to AWS S3 successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to save record: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

    // ─── Delete Record ──────────────────────────────────────────────
    @PostMapping("/records/delete/{id}")
    public String deleteRecord(@PathVariable String id,
                               RedirectAttributes redirectAttributes) {
        try {
            s3Service.deleteRecord(id);
            redirectAttributes.addFlashAttribute("successMessage", "Record deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to delete: " + e.getMessage());
        }
        return "redirect:/records";
    }

    // ─── REST API: Get All Records (JSON) ───────────────────────────
    @GetMapping("/api/records")
    @ResponseBody
    public ResponseEntity<List<DataRecord>> apiGetRecords() {
        return ResponseEntity.ok(s3Service.getAllRecords());
    }

    // ─── REST API: Save Record (JSON) ───────────────────────────────
    @PostMapping("/api/records")
    @ResponseBody
    public ResponseEntity<DataRecord> apiCreateRecord(@RequestBody DataRecord record,
                                                       Authentication auth) {
        record.setId(UUID.randomUUID().toString());
        record.setCreatedBy(auth.getName());
        DataRecord saved = s3Service.saveRecord(record);
        return ResponseEntity.ok(saved);
    }

    // ─── Health Check ───────────────────────────────────────────────
    @GetMapping("/api/health")
    @ResponseBody
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "app", "DataRecordApp"));
    }
}
