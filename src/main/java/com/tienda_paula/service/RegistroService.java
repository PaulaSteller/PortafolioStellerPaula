package com.tienda_paula.service;

import com.tienda_paula.domain.Usuario;
import java.util.Locale;
import java.util.Optional;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

@Service
public class RegistroService {

    // Eliminamos CorreoService
    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    public RegistroService(UsuarioService usuarioService, MessageSource messageSource) {
        this.usuarioService = usuarioService;
        this.messageSource = messageSource;
    }

    public Model activar(Model model, String username, String clave) {
        Optional<Usuario> usuario = usuarioService.getUsuarioPorUsernameYPassword(username, clave);
        if (usuario.isPresent()) {
            model.addAttribute("usuario", usuario.get());
        } else {
            model.addAttribute("titulo", messageSource.getMessage("registro.activar", null, Locale.getDefault()));
            model.addAttribute("mensaje", messageSource.getMessage("registro.activar.error", null, Locale.getDefault()));
        }
        return model;
    }

    public void activar(Usuario usuario, MultipartFile imagenFile) {
        usuario.setActivo(true);
        usuarioService.save(usuario, imagenFile, true);
    }

    public Model crearUsuario(Model model, Usuario usuario) {
        String mensaje;
        try {
            String clave = demeClave();
            usuario.setPassword(clave);
            usuario.setActivo(false);
            usuarioService.save(usuario, null, false);
            // Aquí ya no se envía correo
            mensaje = String.format(messageSource.getMessage("registro.mensaje.activacion.ok", null, Locale.getDefault()), usuario.getCorreo());
        } catch (NoSuchMessageException e) {
            mensaje = String.format(messageSource.getMessage("registro.mensaje.usuario.o.correo", null, Locale.getDefault()), usuario.getUsername(), usuario.getCorreo());
        }
        model.addAttribute("titulo", messageSource.getMessage("registro.activar", null, Locale.getDefault()));
        model.addAttribute("mensaje", mensaje);
        return model;
    }

    public Model recordarUsuario(Model model, Usuario usuario) {
        String mensaje;
        Optional<Usuario> usuarioOpt = usuarioService.getUsuarioPorUsernameOCorreo(usuario.getUsername(), usuario.getCorreo());
        if (usuarioOpt.isPresent()) {
            usuario = usuarioOpt.get();
            String clave = demeClave();
            usuario.setPassword(clave);
            usuario.setActivo(false);
            usuarioService.save(usuario, null, false);
            // Aquí tampoco se envía correo
            mensaje = String.format(messageSource.getMessage("registro.mensaje.recordar.ok", null, Locale.getDefault()), usuario.getCorreo());
        } else {
            mensaje = String.format(messageSource.getMessage("registro.mensaje.usuario.o.correo", null, Locale.getDefault()), usuario.getUsername(), usuario.getCorreo());
        }
        model.addAttribute("titulo", messageSource.getMessage("registro.activar", null, Locale.getDefault()));
        model.addAttribute("mensaje", mensaje);
        return model;
    }

    private String demeClave() {
        String tira = "ABCDEFGHIJKLMNOPQRSTUXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String clave = "";
        for (int i = 0; i < 40; i++) {
            clave += tira.charAt((int) (Math.random() * tira.length()));
        }
        return clave;
    }
}

