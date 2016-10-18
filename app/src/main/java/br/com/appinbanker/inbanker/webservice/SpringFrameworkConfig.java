package br.com.appinbanker.inbanker.webservice;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.com.appinbanker.inbanker.entidades.Usuario;

/**
 * Created by Jonatas on 18/10/2016.
 */

public class SpringFrameworkConfig {

    public String getListaUsuario() {

       /* String url = "http://45.55.217.160:8081/appinbanker/rest/usuario/list";

        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Make the HTTP GET request, marshaling the response to a String
        return restTemplate.getForObject(url, Usuario.class);*/

        String url = "https://ajax.googleapis.com/ajax/services/search/web?v=1.0&q={query}";

// Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

// Add the String message converter
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

// Make the HTTP GET request, marshaling the response to a String
        return restTemplate.getForObject(url, String.class, "SpringSource");
    }

    public Usuario obterUsuarioPorEmail(String email) {

        String url = "http://45.55.217.160:8081/appinbanker/rest/usuario/find/"+email;

        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Make the HTTP GET request, marshaling the response to a String
        return restTemplate.getForObject(url, Usuario.class);
    }


    public String AddUsuario(Usuario usu){

        String result;

        String url = "http://45.55.217.160:8081/appinbanker/rest/usuario/add";

        // Set the Content-Type header
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(new MediaType("application","json"));
        HttpEntity<Usuario> requestEntity = new HttpEntity<Usuario>(usu, requestHeaders);

        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Make the HTTP POST request, marshaling the request to JSON, and the response to a String
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        return result = responseEntity.getBody();

    }
}
