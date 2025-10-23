package com.ycr.mscliente.Service;



import com.ycr.mscliente.Entity.Cliente;
import com.ycr.mscliente.Repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    // Crear cliente
    public Cliente crearCliente(Cliente cliente) {
        if (clienteRepository.existsByDni(cliente.getDni())) {
            throw new RuntimeException("Ya existe un cliente con el DNI: " + cliente.getDni());
        }
        if (clienteRepository.existsByCorreo(cliente.getCorreo())) {
            throw new RuntimeException("Ya existe un cliente con el correo: " + cliente.getCorreo());
        }
        cliente.setFechaRegistro(LocalDate.now());
        return clienteRepository.save(cliente);
    }

    // Obtener todos los clientes
    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepository.findAll();
    }

    // Obtener cliente por ID
    public Optional<Cliente> obtenerClientePorId(Long id) {
        return clienteRepository.findById(id);
    }

    // Obtener cliente por DNI
    public Optional<Cliente> obtenerClientePorDni(String dni) {
        return clienteRepository.findByDni(dni);
    }

    // Actualizar cliente
    public Cliente actualizarCliente(Long id, Cliente clienteActualizado) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    // Verificar si el DNI cambió y si ya existe
                    if (!cliente.getDni().equals(clienteActualizado.getDni()) &&
                            clienteRepository.existsByDni(clienteActualizado.getDni())) {
                        throw new RuntimeException("El DNI ya está registrado");
                    }
                    // Verificar si el correo cambió y si ya existe
                    if (!cliente.getCorreo().equals(clienteActualizado.getCorreo()) &&
                            clienteRepository.existsByCorreo(clienteActualizado.getCorreo())) {
                        throw new RuntimeException("El correo ya está registrado");
                    }

                    cliente.setNombre(clienteActualizado.getNombre());
                    cliente.setApellido(clienteActualizado.getApellido());
                    cliente.setDni(clienteActualizado.getDni());
                    cliente.setCorreo(clienteActualizado.getCorreo());
                    cliente.setTelefono(clienteActualizado.getTelefono());
                    return clienteRepository.save(cliente);
                })
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));
    }

    // Eliminar cliente
    public void eliminarCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente no encontrado con id: " + id);
        }
        clienteRepository.deleteById(id);
    }
}