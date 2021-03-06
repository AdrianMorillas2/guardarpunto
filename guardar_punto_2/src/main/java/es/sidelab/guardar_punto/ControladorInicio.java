package es.sidelab.guardar_punto;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.http.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;


@Controller
public class ControladorInicio {

	private static final Logger log = LoggerFactory.getLogger(ControladorInicio.class);
	//Repositorios
	@Autowired
	private UsuariosRepository repositoryUsuario;	
	@Autowired
	private JuegoRepository repositoryJuego;
	@Autowired
	private EstadoRepository repositoryEstados;
	
	//Obtener usuario logueado
	@Autowired
	private UserComponent userComponent;
	
	//Listas auxiliares
	//Lista de juegos destacados que se van a mostrar en la pagina de inicio
	private List<Juego> listaJuegosDestacados = new ArrayList<Juego>();
	
	private String alert = "";
	
	@PostConstruct
	public void init() {		
		//repositoryUsuario.save(new Usuarios("user", "pass", "ROLE_USER"));
		
		/*** Listas auxiliares ***/
		listaJuegosDestacados.add(repositoryJuego.findOne(1));
		listaJuegosDestacados.add(repositoryJuego.findOne(2));
		listaJuegosDestacados.add(repositoryJuego.findOne(6));		
		listaJuegosDestacados.add(repositoryJuego.findOne(7));
	}
	
	//Pagina inicial. 
	//Muestra algunos juegos destacados, almacenados previamente en una lista	
	@GetMapping("/")
	public String Inicio(Model model, HttpServletRequest request) {
		model.addAttribute("listaJuegosDestacados", listaJuegosDestacados);
		String displayLogin = "block";
		String displayOff = "none";
		String displayTusJuegos = "none";
		
		//Si hay un usuario logueado, se muestran sus juegos jugados en lugar del panel de login
		List<Juego> jugados = new ArrayList<Juego>(); //vacia si no hay ningun usuario logueado
		if(userComponent.isLoggedUser()) {
			Usuarios loggedUser = userComponent.getLoggedUser();			
			jugados = new ArrayList<Juego>(repositoryEstados.findByStateAndEstadouser("jugado", loggedUser));
			displayLogin = "none";
			displayOff="block";
			displayTusJuegos = "block";
		}
		
		
		model.addAttribute("listaJuegosUsuario", jugados);
		model.addAttribute("displayLogin", displayLogin);
		model.addAttribute("displayTusJuegos", displayTusJuegos);
		model.addAttribute("displayOff",displayOff);
		
		CsrfToken token = (CsrfToken) request.getAttribute("_csrf"); 
		model.addAttribute("token", token.getToken());   
		
		model.addAttribute("alert", alert);	
				
		return "Inicio";
	}
	
    @GetMapping("/loginerror")
    public String loginerror(Model model,  HttpServletRequest request) {
		CsrfToken token = (CsrfToken) request.getAttribute("_csrf"); 
		model.addAttribute("token", token.getToken());
		model.addAttribute("displayOff", "none");
    	return "loginerr";
    }  
   
	
	//Registrar nuevo usuario
	@PostMapping("/nuevoUsuario")
	public String nuevoUsuario (Model model, Usuarios usuario, HttpServletRequest request) {
		/*Comprobar que el usuario no está registrado ya*/
		if(this.repositoryUsuario.findByEmailIgnoreCaseLike(usuario.getEmail())!=null
			||
		   !this.repositoryUsuario.findByNombre(usuario.getNombre()).isEmpty()) {
			/*TODO: Implementar error*/
			System.out.println("Usuario ya registrado");
			//Enviar un alert para avisar de que no esta logueado
			alert = "<script>alert(\"Este usuario ya existe \")</script>";
			Inicio(model, request);
			return "Inicio";
		}else {
			//Hay que cifrar la contraseña del nuevo usuario
			usuario.cifrarYGuardarContrasena(usuario.getContrasenna());
			//Y poner su rol 
			usuario.setRoles("ROLE_USER");
			//Imagen por defecto
			usuario.setImagen("https://tinyurl.com/y99gq3cc");
			//Guardar el nuevo usuario en la db
			repositoryUsuario.save(usuario);
			email(usuario);
			Inicio(model, request);
			model.addAttribute("alert", alert);	
			return "Inicio";
		}
	}
	
	private void email(Usuarios user) {
		String url="http://192.168.33.14:8080/mail";
		RestTemplate restTemplate = new RestTemplate();
		Mail mail=new Mail(user.getNombre(),user.getEmail());
		restTemplate.postForEntity(url, mail, String.class);
	}
	
}
