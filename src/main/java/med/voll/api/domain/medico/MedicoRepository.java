package med.voll.api.domain.medico;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;


public interface MedicoRepository extends JpaRepository<Medico, Long> {

    Page<Medico> findAllByAtivoTrue(Pageable paginacao);

    @Query(value = """
           select * from medicos m
           where 
                m.ativo = 1 
            and
              m.especialidade
            and
              m.id not in (
                  select c.medico_id from consultas c
                  where 
                    c.data
           )
            order by rand()
            limit 1
        """, nativeQuery = true)
    Medico escolherMedicoAleatorioLivreNaData(Especialidade especialidade, LocalDateTime data);

    @Query(value = """
           select m.ativo 
           from Medico m
           where m.id = :idMedico
        """)
    Boolean findAtivoById(Long idMedico);
}
