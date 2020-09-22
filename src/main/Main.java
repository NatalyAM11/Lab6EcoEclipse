package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

import com.google.gson.Gson;

import processing.core.PApplet;

public class Main extends PApplet {
	
	ArrayList <Usuario> usuarios;
	BufferedWriter writer;
	BufferedReader reader;
	String id;
	String nombre;
	String pantalla;
	boolean estado;
	String noticia;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		PApplet.main("main.Main");
	}
	
	public void settings() {
		
	}
	
	public void setup() {
		
		//Arreglo de usuarios
		usuarios= new ArrayList <Usuario>();
		//id= UUID.randomUUID().toString();
		
		//Creo los usuarios
		usuarios.add(new Usuario("camila","12345",id));
		usuarios.add(new Usuario ("jose","jose1", id));
		usuarios.add(new Usuario ("alejandra", "hola",id));
		estado=false;
		
		
		
		new Thread(
				
				()->{
					
					try {
						
						//Creo el socket del servidor y lo hago esperar 
						ServerSocket server = new ServerSocket(5000);
						System.out.println("Esperando conexión");
						Socket socket= server.accept();
						System.out.println("Conectado con el usuario");
						
						
						//Inputs y Outputs 
						InputStream is= socket.getInputStream();
						OutputStream os= socket.getOutputStream();
						
						
						//Reader and writer
						reader= new BufferedReader(new InputStreamReader(is));
						writer= new BufferedWriter (new OutputStreamWriter (os));
						
						//metodo para recibir el mensaje
						recibir();
						
						
					
		
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				).start();
		
	}
	
	
	
	public void draw() {
		
	}
	
	
	
	public void enviar(String mensajito) {
		
		new Thread(
				()->{
					
					try {
						writer.write(mensajito+ "\n");;
						writer.flush();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
				).start();
		
		
	}
	
	
	public void recibir () {
		
		new Thread (
				()->{
					
				
					try {
						
						//recepción de mensajes
						while(true) {
							System.out.println("Esperando el mensaje");
							String line= reader.readLine();
							System.out.println("LLego el mensaje");
							
							
							//Creo el Json
							Gson gson= new Gson();
							
							//Deserializo
							Usuario objeto= gson.fromJson(line, Usuario.class);
							
							System.out.println(objeto.getName());
							System.out.println(objeto.getContrasena());
							System.out.println(objeto.getId());
							System.out.println(line);
							
							
							
							//Comparo el mensaje recibido con el dato del arreglo
							for( int i=0; i<usuarios.size(); i++) {
								
								if(objeto.getName()!=null && objeto.getContrasena()!=null) {
									
								if(objeto.getName().equals(usuarios.get(i).getName()) && objeto.getContrasena().equals(usuarios.get(i).getContrasena())) {
									System.out.println("Si dio");
									estado=true;
									pantalla="Exito";
									noticia="El ingreso fue exitoso";
									enviar(pantalla+":"+noticia);
								}
								
								//No encontro el nombre que ingreso, entonces envia "Fail" para no dejarlo pasar
								if(estado==false) {
									System.out.println("No dio");
									pantalla="Fail";
									noticia="El ingreso fue fallido";
									enviar(pantalla+":"+noticia);
								}
								  }
								
			
							}
						}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					

				}
				
				).start();
	}

}
