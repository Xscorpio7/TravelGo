

package com.travelgo.backend_travelgo.repository;
import com.travelgo.backend_travelgo.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCredencialId(Integer credencialId);
}
