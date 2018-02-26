package es.sidelab.guardar_punto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ControladorUsuario {
	@Autowired
	private EstadoRepository repositoryEstados;
	@Autowired
	private UsuariosRepository repositoryUsuario;

	@GetMapping("/Usuario/{id}")
	public String usuario(Model model,@PathVariable String id) {
		int num = Integer.parseInt(id);
		Usuarios usuario = repositoryUsuario.findOne(num);
		
		String name = usuario.getNombre();
		String bio = usuario.getBiografia();
		String imagen = usuario.getImagen();
		List<Usuarios> amigos = new ArrayList<Usuarios>(usuario.getAmigos());
		
		
		List<Juego> jugados = new ArrayList<Juego>(repositoryEstados.findByStateAndEstadouser("jugado", usuario));
		List<Juego> jugando = new ArrayList<Juego>(repositoryEstados.findByStateAndEstadouser("jugando", usuario));
		List<Juego> pendientes = new ArrayList<Juego>(repositoryEstados.findByStateAndEstadouser("pendiente", usuario));			
		
		List<Comentario> comentarios = new ArrayList<Comentario>(usuario.getComents());
		List<Review> reviews = new ArrayList<Review>(usuario.getReview());
		
		model.addAttribute("id",num);
		model.addAttribute("imagen",imagen);
		model.addAttribute("nombre",name);
		model.addAttribute("biografia", bio);
		model.addAttribute("listaAmigos",amigos);
		model.addAttribute("listaJugados",jugados);
		model.addAttribute("listaJugando",jugando);
		model.addAttribute("listaPendientes",pendientes);
		model.addAttribute("listaComentarios",comentarios);
		model.addAttribute("listaReviews",reviews);
		
		return "Usuario";
	}
	//Cambios en el usuario
		@PostMapping("/editarUsuario")
		public String editarUsuario (Model model,Usuarios usuario) {
				Integer idAux = usuario.getId();
				String bioAux = usuario.getBiografia();
				String imagenAux = usuario.getImagen();

				Usuarios user = repositoryUsuario.findOne(idAux);
				if(!bioAux.isEmpty()) {
					user.setBiografia(bioAux);
				}
				if(!imagenAux.isEmpty()) {
					user.setImagen(imagenAux);
				}
				repositoryUsuario.save(user);
				usuario(model,usuario.getId().toString());
				return "Usuario";
		}
}