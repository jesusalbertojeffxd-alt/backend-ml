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
        // Validar que el username no esté en uso
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso: " + request.getUsername());
        }

        // Validar campos obligatorios
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        // Crear y configurar usuario
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombre(request.getNombre());
        usuario.setDireccion(request.getDireccion());
        usuario.setTelefono(request.getTelefono());

        // Asignar rol por defecto
        Rol rol = Rol.ROLE_CLIENTE;
        if (request.getRol() != null && request.getRol().equalsIgnoreCase("ROLE_ADMIN")) {
            rol = Rol.ROLE_ADMIN;
        }
        usuario.setRol(rol);

        // Guardar usuario
        UsuarioEntity savedUsuario = usuarioRepository.save(usuario);

        // Si es cliente, crear también en tabla clientes
        if (rol == Rol.ROLE_CLIENTE) {
            // Verificar que el cliente no exista ya
            if (clienteRepository.findByEmail(request.getUsername()).isPresent()) {
                throw new IllegalArgumentException("Ya existe un cliente con el email: " + request.getUsername());
            }

            ClienteEntity cliente = new ClienteEntity();
            cliente.setNombre(request.getNombre());
            cliente.setEmail(request.getUsername()); // Usamos el username como email
            cliente.setDireccion(request.getDireccion());
            cliente.setTelefono(request.getTelefono());
            clienteRepository.save(cliente);
        }

        return savedUsuario;
    }

    // Método para cambiar rol de usuario
    @Transactional
    public UsuarioEntity cambiarRol(String username, Rol nuevoRol) {
        UsuarioEntity usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        // Si el usuario cambia de CLIENTE a ADMIN o viceversa, manejar cliente
        Rol rolAnterior = usuario.getRol();
        usuario.setRol(nuevoRol);
        UsuarioEntity updatedUsuario = usuarioRepository.save(usuario);

        // Si era cliente y ahora es admin, no es necesario mantener en tabla clientes
        // Si era admin y ahora es cliente, crear cliente
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

    // Método para actualizar perfil de usuario
    @Transactional
    public UsuarioEntity actualizarPerfil(String username, RegistroRequest request) {
        UsuarioEntity usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        // Actualizar datos
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

        // Si es cliente, actualizar también en tabla clientes
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