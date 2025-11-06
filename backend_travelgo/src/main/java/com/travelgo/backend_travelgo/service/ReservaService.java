public class ReservaService {
    public List<Reserva> findByUsuarioId(Long usuarioId) {
    return reservaRepository.findByUsuarioId(usuarioId);
}
}
