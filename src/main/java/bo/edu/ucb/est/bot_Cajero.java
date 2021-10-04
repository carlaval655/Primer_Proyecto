/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.edu.ucb.est;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 *
 * @author Windows
 */
public class bot_Cajero extends TelegramLongPollingBot{
    private Map<String,Cliente> clienteUsuario = new HashMap<String,Cliente>();
    private Map<String,Integer> clienteEstado = new HashMap<String,Integer>();
    private Map estadoUsuario = new HashMap();
    private Banco banco = new Banco("bisa");
    @Override
    public String getBotToken() {
        return ""; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
        public void onUpdateReceived(Update update) {
        String userId = update.getMessage().getChatId().toString();
        Integer estado = (Integer) estadoUsuario.get(userId);
        if (estado == null){
            estado = 0;
            estadoUsuario.put(userId,estado);
        }
        System.out.println(update.getMessage().toString());
        if(update.hasMessage()) {
            String noCliente1 = "Bienvenido al Banco " + banco.getNombre();
            String noCliente2 = "He notado que aun no eres cliente.";
            String pedirNombre = "�Cu�l es tu nombre completo?";
            String pedirPin = "Por favor elige un PIN de seguridad, este te sera requerido cada que ingreses al sistema.";
            String registroCorrecto = "Te hemos registrado correctamente";
            String bienvenida = "Hola de nuevo ";
            String pedirPinIngreso = "Solo por seguridad �Cu�l es tu PIN?";
            String pinIncorrecto = "Lo siento, el codigo es incorrecto.";
            String menuPrincipal = "Elige una opcion:\n1. Ver saldo\n2. Retirar dinero\n3. Depositar dinero\n"
                    + "4. Crear cuenta\n5. Salir";
            String mensajeError = "Hubo un error.";
            String mensajeUsuario = null;
            switch (estado){
                case 0: 
                mostrarMensaje(noCliente1, String.valueOf(userId));
                mostrarMensaje(noCliente2, String.valueOf(userId));
                mostrarMensaje(pedirNombre, String.valueOf(userId));
                estadoUsuario.put(userId,1);
                break;
                case 1: // Se pidio el nombre con el que el usuario desea registrarse
                    mensajeUsuario = update.getMessage().getText();
                    try {
                        Cliente cliente = new Cliente (mensajeUsuario,userId);
                        banco.agregarCliente(cliente);
                        mostrarMensaje(pedirPin, userId);
                        estadoUsuario.put(userId,2);
                    }
                    catch(Exception ex){
                        mostrarMensaje(mensajeError, userId);
                        estadoUsuario.put(userId,1);
                    }
                    break;
                case 2: // Se pidio el pin con el que desea registrarse
                     mensajeUsuario = update.getMessage().getText();
                     try {
                        int pin = Integer.parseInt(mensajeUsuario);
                        if (pin>=1000 || pin<=10000){
                        for (int i = 0; i<banco.getClientes().size(); i++){
                            if(banco.getClientes().get(i).getIdCliente().equals(userId)){
                                banco.getClientes().get(i).setPinSeguridad(mensajeUsuario);
                                mostrarMensaje(registroCorrecto,userId);
                                break;
                            } 
                        }
                        estadoUsuario.put(userId,3);
                        }
                    }
                    catch(Exception ex){
                        mostrarMensaje(mensajeError, userId);
                        estadoUsuario.put(userId,0);
                    }
                     break;
                case 3: // Ya se registro al usuario y se le pide que introduzca su pin
                     mensajeUsuario = update.getMessage().getText();
                     try {
                        mostrarMensaje(bienvenida+banco.buscarNombreCliente(userId), userId);
                        mostrarMensaje(pedirPinIngreso,userId);
                        estadoUsuario.put(userId,4);
                    }
                    catch(Exception ex){
                        mostrarMensaje(mensajeError, userId);
                    }
                     break;
                case 4: // Ya se registro al usuario y se le pide que introduzca su pin
                     mensajeUsuario = update.getMessage().getText();
                     try {
                        Cliente cli = banco.buscarClientePorCodigo(userId, mensajeUsuario);
                        if (cli==null){
                            mostrarMensaje(pinIncorrecto,userId);
                            mostrarMensaje(bienvenida+banco.buscarNombreCliente(userId),userId);
                            mostrarMensaje(pedirPinIngreso,userId);
                            estadoUsuario.put(userId,3);
                        }
                        else{
                            
                        }
                    }
                    catch(Exception ex){
                        mostrarMensaje(mensajeError, userId);
                    }
                     break;
                default:
                    mostrarMensaje(mensajeError, userId);
                    estadoUsuario.put(userId,1);
                    break;
            }
            
        }
    }
    @Override
    public String getBotUsername() {
        return "cajero_est_bot"; //To change body of generated methods, choose Tools | Templates.
    }
    public void mostrarMensaje(String mensaje, String Id){
        SendMessage message = new SendMessage();
        message.setChatId(Id);
        message.setText(mensaje);
            message.setChatId(Id+"");
            try {
                execute(message);
            } catch (TelegramApiException ex) {
                
            }
    }
}