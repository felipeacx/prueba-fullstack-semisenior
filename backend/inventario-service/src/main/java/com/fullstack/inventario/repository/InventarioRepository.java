package com.fullstack.inventario.repository;

import com.fullstack.inventario.domain.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    Optional<Inventario> findByProductoId(Long productoId);

    @Query("SELECT i FROM Inventario i ORDER BY i.productoId ASC")
    @NonNull
    Page<Inventario> findAll(@NonNull Pageable pageable);
}
