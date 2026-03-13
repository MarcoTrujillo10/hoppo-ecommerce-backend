package com.example.HPPO_Backend.service;

import com.example.HPPO_Backend.entity.Cart;
import com.example.HPPO_Backend.entity.Role;
import com.example.HPPO_Backend.repository.CartRepository;
import com.example.HPPO_Backend.repository.CategoryRepository;
import com.example.HPPO_Backend.entity.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.HPPO_Backend.auth.AuthenticationRequest;
import com.example.HPPO_Backend.auth.AuthenticationResponse;
import com.example.HPPO_Backend.auth.RegisterRequest;
import com.example.HPPO_Backend.config.JwtService;
import com.example.HPPO_Backend.entity.User;
import com.example.HPPO_Backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

        private final UserRepository repository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;
        private final CartRepository cartRepository;

        @Transactional
        public AuthenticationResponse register(RegisterRequest request) {
                User user = new User();


                user.setRole(request.getRole() != null ? request.getRole() : Role.COMPRADOR);

                user.setUsername(request.getEmail());
                user.setName(request.getFirstname());
                user.setLastName(request.getLastname());
                user.setEmail(request.getEmail());
                user.setPassword(passwordEncoder.encode(request.getPassword()));

                User savedUser = repository.save(user);


                if (savedUser.getRole() == Role.COMPRADOR) {
                        Cart newUserCart = new Cart();
                        newUserCart.setUser(savedUser);
                        newUserCart.setQuantity(0);
                        cartRepository.save(newUserCart);
                }

                var jwtToken = jwtService.generateToken(savedUser);
                return AuthenticationResponse.builder()
                        .accessToken(jwtToken)
                        .user(savedUser)
                        .build();
        }

        public AuthenticationResponse authenticate(AuthenticationRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));
                var user = repository.findByEmail(request.getEmail())
                                .orElseThrow();
                var jwtToken = jwtService.generateToken(user);
                return AuthenticationResponse.builder()
                                .accessToken(jwtToken)
                                .user(user)
                                .build();
        }
}
