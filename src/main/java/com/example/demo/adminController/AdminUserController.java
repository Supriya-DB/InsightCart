package com.example.demo.adminController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.adminServices.AdminUserService;
import com.example.demo.entities.User;

@RestController
@RequestMapping("/admin/user")
@CrossOrigin(
    origins = "http://localhost:5173",
    allowCredentials = "true"
)
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(
            AdminUserService adminUserService) {

        this.adminUserService = adminUserService;
    }

    // =========================================================
    // MODIFY USER
    // =========================================================

    @PutMapping("/modify")
    public ResponseEntity<?> modifyUser(
            @RequestBody Map<String, Object> userRequest) {

        try {

            if (userRequest == null ||
                    userRequest.get("userId") == null) {

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("userId is required");
            }

            Integer userId;

            Object userIdObject =
                    userRequest.get("userId");

            if (userIdObject instanceof Integer) {

                userId = (Integer) userIdObject;

            } else {

                userId =
                        Integer.valueOf(
                                userIdObject.toString()
                        );
            }


            String username =
                    userRequest.get("username") != null
                            ? userRequest.get("username").toString()
                            : null;


            String email =
                    userRequest.get("email") != null
                            ? userRequest.get("email").toString()
                            : null;


            String role =
                    userRequest.get("role") != null
                            ? userRequest.get("role").toString()
                            : null;


            User updatedUser =
                    adminUserService.modifyUser(
                            userId,
                            username,
                            email,
                            role
                    );


            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "userId",
                    updatedUser.getUserId()
            );

            response.put(
                    "username",
                    updatedUser.getUsername()
            );

            response.put(
                    "email",
                    updatedUser.getEmail()
            );

            response.put(
                    "role",
                    updatedUser.getRole().name()
            );

            response.put(
                    "createdAt",
                    updatedUser.getCreatedAt()
            );

            response.put(
                    "updatedAt",
                    updatedUser.getUpdatedAt()
            );


            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);


        } catch (IllegalArgumentException e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());


        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            "Something went wrong while modifying user"
                    );
        }
    }


    // =========================================================
    // GET USER BY ID
    // =========================================================

    @GetMapping("/getbyid")
    public ResponseEntity<?> getUserById(
            @RequestParam Integer userId) {

        try {

            if (userId == null) {

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("userId is required");
            }


            User user =
                    adminUserService.getUserById(userId);


            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "userId",
                    user.getUserId()
            );

            response.put(
                    "username",
                    user.getUsername()
            );

            response.put(
                    "email",
                    user.getEmail()
            );

            response.put(
                    "role",
                    user.getRole().name()
            );

            response.put(
                    "createdAt",
                    user.getCreatedAt()
            );

            response.put(
                    "updatedAt",
                    user.getUpdatedAt()
            );


            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);


        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());


        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            "Something went wrong while fetching user"
                    );
        }
    }
}