package com.jahm.alixxpres.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jahm.alixxpres.dto.RegistroRequest;
import com.jahm.alixxpres.modelo.ClienteEntity;
import com.jahm.alixxpres.modelo.Rol;
import com.jahm.alixxpres.modelo.UsuarioEntity;
import com.jahm.alixxpres.repository.ClienteRepository;
import com.jahm.alixxpres.repository.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioEntity saveUsuario(RegistroRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya esta en uso: " + request.getUsername());
        }
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contrasena es obligatoria");
        }
        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombre(request.getNombre());
        usuario.setDireccion(request.getDireccion());
        usuario.setTelefono(request.getTelefono());

        Rol rol = Rol.ROLE_CLIENTE;
        if (request.getRol() != null && request.getRol().equalsIgnoreCase("ROLE_ADMIN")) {
            rol = Rol.ROLE_ADMIN;
        }
        usuario.setRol(rol);

        UsuarioEntity savedUsuario = usuarioRepository.save(usuario);

        if (rol == Rol.ROLE_CLIENTE) {
            if (clienteRepository.findByEmail(request.getUsername()).isPresent()) {
                throw new IllegalArgumentException("Ya existe un cliente con el email: " + request.getUsername());
            }
            ClienteEntity cliente = new ClienteEntity();
            cliente.setNombre(request.getNombre());
            cliente.setEmail(request.getUsername());
            cliente.setDireccion(request.getDireccion());
            cliente.setTelefono(request.getTelefono());
            clienteRepository.save(cliente);
        }

        return savedUsuario;
    }

    @Transactional
    public UsuarioEntity cambiarRol(String username, Rol nuevoRol) {
        UsuarioEntity usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        Rol rolAnterior = usuario.getRol();
        usuario.setRol(nuevoRol);
        UsuarioEntity updatedUsuario = usuarioRepository.save(usuario);
        if (rolAnterior == Rol.ROLE_ADMIN && nuevoRol == Rol.ROLE_CLIENTE) {
            ClienteEntity cliente = new ClienteEntity();
            cliente.setNombre(usuario.getNombre());
            cliente.setEmail(usuario.getUsername());
            cliente.setDireccion(usuario.getDireccion());
            cliente.setTelefono(usuario.getTelefono());
            clienteRepository.save(cliente);
        }
        return updatedUsuario;
    }

    @Transactional
    public UsuarioEntity actualizarPerfil(String username, RegistroRequest request) {
        UsuarioEntity usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        if (request.getNombre() != null && !request.getNombre().trim().isEmpty()) {
            usuario.setNombre(request.getNombre());
        }
        if (request.getDireccion() != null) {
            usuario.setDireccion(request.getDireccion());
        }
        if (request.getTelefono() != null) {
            usuario.setTelefono(request.getTelefono());
        }

        UsuarioEntity updatedUsuario = usuarioRepository.save(usuario);

        if (usuario.getRol() == Rol.ROLE_CLIENTE) {
            clienteRepository.findByEmail(usuario.getUsername())
                .ifPresent(cliente -> {
                    if (request.getNombre() != null) cliente.setNombre(request.getNombre());
                    if (request.getDireccion() != null) cliente.setDireccion(request.getDireccion());
                    if (request.getTelefono() != null) cliente.setTelefono(request.getTelefono());
                    clienteRepository.save(cliente);
                });
        }

        return updatedUsuario;
    }
}
