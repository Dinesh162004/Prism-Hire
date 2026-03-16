package com.prismhire.controller;

import com.prismhire.entity.User;
import com.prismhire.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return userRepository.findById(userId)
                .map(u -> ResponseEntity.ok(toMap(u)))
                .orElse(ResponseEntity.status(404).body(null));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            return ResponseEntity.status(404).body(Map.of("message", "User not found."));

        if (body.containsKey("name") && body.get("name") != null)
            user.setName(body.get("name").toString());
        if (body.containsKey("email") && body.get("email") != null)
            user.setEmail(body.get("email").toString());
        if (body.containsKey("profilePicture")) {
            user.setProfilePicture(body.get("profilePicture") != null ? body.get("profilePicture").toString() : "");
        }
        if (body.containsKey("theme") && body.get("theme") != null) {
            try {
                user.setTheme(User.Theme.valueOf(body.get("theme").toString()));
            } catch (Exception ignored) {
            }
        }

        User updated = userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Profile updated successfully!", "user", toMap(updated)));
    }

    private Map<String, Object> toMap(User u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", u.getId());
        m.put("name", u.getName());
        m.put("email", u.getEmail());
        m.put("profilePicture", u.getProfilePicture());
        m.put("theme", u.getTheme());
        m.put("createdAt", u.getCreatedAt());
        return m;
    }
}
