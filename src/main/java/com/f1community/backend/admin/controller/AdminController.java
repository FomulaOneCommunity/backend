package com.f1community.backend.admin.controller;

import com.f1community.backend.admin.service.AdminService;
import com.f1community.backend.admin.dto.RoleUpdateDto;
import com.f1community.backend.user.dto.request.UserStatusUpdateDto;
import com.f1community.backend.user.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admins", description = "Admins-related API")
@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdminController {
    private final AdminService adminService;

    @Operation(
            summary = "Get all users",
            description = "Retrieves information of all users. Requires admin privileges."
    )
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @Operation(
            summary = "Get a specific user",
            description = "Retrieves information of a specific user. Requires admin privileges."
    )
    @GetMapping("/user/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @Operation(
            summary = "Update user role",
            description = "Updates the role of a specific user. Requires admin privileges."
    )
    @PutMapping("/user/{id}/role")
    public ResponseEntity<Void> updateUserRole(@PathVariable Long id, @RequestBody RoleUpdateDto dto) {
        adminService.updateUserRole(id, dto.getRole());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Update user status",
            description = "Updates the status of a specific user. Requires admin privileges."
    )
    @PutMapping("/user/{id}/status")
    public ResponseEntity<Void> updateUserStatus(@PathVariable Long id, @RequestBody UserStatusUpdateDto dto) {
        adminService.updateUserStatus(id, dto.getStatus());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes a specific user. Requires admin privileges."
    )
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUserByAdmin(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

}