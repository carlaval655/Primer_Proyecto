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
    
    private Map<String,Cuenta> clienteCuentaActual = new HashMap<String,Cuenta>();
    private Map<String,Cliente> clienteUsuario = new HashMap<String,Cliente>();
    private Map<String,Integer> clienteEstado = new HashMap<String,Integer>();
    private Map estadoUsuario = new HashMap();
    private Banco banco = new Banco("BISA");
    private int nroCuenta = 300;
    @Override
    public String getBotToken() {
        return ""; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
        public void onUpdateReceived(Update update) {
        nroCuenta++;
        String userId = update.getMessage().getChatId().toString();
        Integer estado = (Integer) estadoUsuario.get(userId);
        if (estado == null){
            estado = 0;
            estadoUsuario.put(userId,estado);
        }
        System.out.println(update.getMessage().toString());
        int seleccionCuenta=0;
        if(update.hasMessage()) {
            String noCliente1 = "Bienvenido al banco "+banco.getNombre();
            String noCliente2 = "He notado que aún no eres cliente.";
            String pedirNombre = "¿Cuál es tu nombre completo?";
            String pedirPin = "Por favor elige un PIN de seguridad, este te será requerido cada vez que ingreses al sistema.";
            String registroCorrecto = "Te hemos registrado correctamente";
            String bienvenida = "Hola de nuevo ";
            String pedirPinIngreso = "Solo por seguridad ¿Cuál es tu PIN?";
            String pinIncorrecto = "Lo siento, el código es incorrecto.";
            String menuPrincipal = "Elige una opción:\n1. Ver saldo\n2. Retirar dinero\n3. Depositar dinero\n"
                    + "4. Crear cuenta\n5. Salir";
            String sinCuenta = "Usted no tiene cuentas, cree una primero";
            String seleccionMoneda = "Seleccione la moneda:\n\t1. Dólares\n\t2. Bolivianos";
            String seleccionTipoCuenta = "Seleccione el tipo de cuenta:\n\t1. Caja de Ahorros\n\t2. Cuenta corriente";
            String creacionCuenta = "Se le ha creado una ";
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
                        if (mensajeUsuario.length()==4){
                        for (int i = 0; i<banco.getClientes().size(); i++){
                            if(banco.getClientes().get(i).getIdCliente().equals(userId)){
                                banco.getClientes().get(i).setPinSeguridad(mensajeUsuario);
                                mostrarMensaje(registroCorrecto,userId);
                                break;
                            } 
                            else{
                                mostrarMensaje("El pin debe contener 4 dígitos",userId);
                                mostrarMensaje(pedirPin, userId);
                                estadoUsuario.put(userId,2);
                            }
                        }
                        estadoUsuario.put(userId,3);
                        }
                        else{
                            mostrarMensaje("El pin debe contener 4 dígitos",userId);
                        mostrarMensaje(pedirPin, userId);
                        estadoUsuario.put(userId,2);
                        }
                    }
                    catch(Exception ex){
                        mostrarMensaje("El pin debe contener 4 dígitos",userId);
                        mostrarMensaje(pedirPin, userId);
                        estadoUsuario.put(userId,2);
                    }
                     break;
                case 3: // Ya se registro al usuario y se le pide que introduzca su pin
                     mensajeUsuario = update.getMessage().getText();
                     try {
                        mostrarMensaje(bienvenida+banco.buscarCliente(userId).getNombre(), userId);
                        mostrarMensaje(pedirPinIngreso,userId);
                        estadoUsuario.put(userId,4);
                    }
                    catch(Exception ex){
                        estadoUsuario.put(userId,3);
                    }
                     break;
                case 4: // se corrobora el pin que el cliente ingreso
                     mensajeUsuario = update.getMessage().getText();
                     try {
                        Cliente cli = banco.buscarClientePorCodigo(userId, mensajeUsuario);
                        if (cli==null){
                            mostrarMensaje(pinIncorrecto,userId);
                            mostrarMensaje(bienvenida+banco.buscarCliente(userId).getNombre(), userId);
                            mostrarMensaje(pedirPinIngreso,userId);
                            estadoUsuario.put(userId,4);
                        }
                        else{
                            clienteUsuario.put(userId,cli);
                            mostrarMensaje("Bienvenido",userId);
                            mostrarMensaje(menuPrincipal,userId);
                            estadoUsuario.put(userId,5);
                        }
                    }
                    catch(Exception ex){
                        mostrarMensaje(mensajeError, userId);
                        mostrarMensaje(pedirPinIngreso,userId);
                        estadoUsuario.put(userId,4);
                    }
                     break;
                case 5: // El cliente ingreso al sistema pero no tiene ninguna cuenta
                     mensajeUsuario = update.getMessage().getText();
                     try {
                        int opcion = Integer.parseInt(mensajeUsuario);
                        if(clienteCuentaActual.get(userId)==null){ //significa que el cliente aun no tiene ninguna cuenta por lo tanto la unica opcion a la que puede acceder es a la 4
                            if (opcion==1 || opcion ==2 || opcion==3 || opcion==5){
                                if(opcion==1 || opcion ==2 || opcion==3){
                            mostrarMensaje(sinCuenta,userId);
                            //mostrarMensaje(mensajeError, userId);
                        mostrarMensaje("Bienvenido",userId);
                        mostrarMensaje(menuPrincipal,userId);
                        estadoUsuario.put(userId,5);
                                }
                                else{
                                    mostrarMensaje(bienvenida+banco.buscarCliente(userId).getNombre(), userId);
                                    mostrarMensaje(pedirPinIngreso,userId);
                                    estadoUsuario.put(userId,4);
                                    //mostrarMensaje("Si deseas volver a interactuar con el cajero solamente envia un mensaje",userId);
                                    //estadoUsuario.put(userId,3);
                                }
                        }
                        else {
                            Cuenta cuenta = new Cuenta(String.valueOf(nroCuenta));//Se creo la cuenta pero aun no se la agrego a la lista de cuentas del cliente debido a que falta completar informacion de la misma.
                            clienteCuentaActual.put(userId,cuenta);
                            //clienteUsuario.get(userId).agregarCuenta(cuenta);
                            mostrarMensaje(seleccionMoneda,userId);
                            estadoUsuario.put(userId, 6);
                        }
                        }
                        else {//El cliente tiene al menos una cuenta
                            switch(opcion){
                            case 1:
                                estadoUsuario.put(userId,9);//Ver saldo
                                mostrarMensaje(banco.buscarCliente(userId).mostrarCuentas(),userId);
                                break;
                            case 2:
                                estadoUsuario.put(userId,10);// Retirar dinero
                                mostrarMensaje(banco.buscarCliente(userId).mostrarCuentas(),userId);
                                break;
                            case 3:
                                estadoUsuario.put(userId,11);//Depositar dinero
                                mostrarMensaje(banco.buscarCliente(userId).mostrarCuentas(),userId);
                                break;
                            case 4:
                                Cuenta cuenta = new Cuenta(String.valueOf(nroCuenta));//Se creo la cuenta pero aun no se la agrego a la lista de cuentas del cliente debido a que falta completar informacion de la misma.
                            clienteCuentaActual.put(userId,cuenta);
                            mostrarMensaje(seleccionMoneda,userId);
                            estadoUsuario.put(userId, 6);
                                break;
                            case 5:
                                //Se le vuelve a pedir que introduzca el pin
                                mostrarMensaje(bienvenida+banco.buscarCliente(userId).getNombre(), userId);
                                    mostrarMensaje(pedirPinIngreso,userId);
                                    estadoUsuario.put(userId,4);
                                break;
                            default:
                                mostrarMensaje(mensajeError, userId);
                        mostrarMensaje("Bienvenido",userId);
                        mostrarMensaje(menuPrincipal,userId);
                        estadoUsuario.put(userId,5);
                                break;
                        }
                        //mostrarMensaje(banco.buscarCliente(userId).mostrarCuentas(),userId);
                        }
                    }
                    catch(Exception ex){
                        mostrarMensaje(mensajeError, userId);
                        mostrarMensaje("Bienvenido",userId);
                        mostrarMensaje(menuPrincipal,userId);
                        estadoUsuario.put(userId,5);
                    }
                     break;
                case 6: // El cliente debio ingresar el tipo de moneda de la cuenta
                     mensajeUsuario = update.getMessage().getText();
                     try {
                        int opcion = Integer.parseInt(mensajeUsuario);
                        if (opcion ==1 || opcion==2){
                            if (opcion == 1){
                                clienteCuentaActual.get(userId).setMoneda("Dólares");
                                mostrarMensaje(seleccionTipoCuenta,userId);
                                estadoUsuario.put(userId,7);
                            }
                            else{
                                clienteCuentaActual.get(userId).setMoneda("Bolivianos");
                                mostrarMensaje(seleccionTipoCuenta,userId);
                                estadoUsuario.put(userId,7);
                            }
                        }
                        else {
                            mostrarMensaje(mensajeError, userId);
                            mostrarMensaje("Bienvenido",userId);
                            mostrarMensaje(menuPrincipal,userId);
                            estadoUsuario.put(userId,5);
                        }
                    }
                    catch(Exception ex){
                        mostrarMensaje(mensajeError, userId);
                        mostrarMensaje("Bienvenido",userId);
                        mostrarMensaje(menuPrincipal,userId);
                        estadoUsuario.put(userId,5);
                    }
                     break;
                case 7: // El cliente debio ingresar el tipo de cuenta que desea crear
                     mensajeUsuario = update.getMessage().getText();
                     try {
                         Cuenta cuenta = clienteCuentaActual.get(userId);
                        int opcion = Integer.parseInt(mensajeUsuario);
                        if (opcion ==1 || opcion==2){
                            if (opcion == 1){
                                cuenta.setTipo("Caja de Ahorros");
                            }
                            else{
                                cuenta.setTipo("Cuenta Corriente");
                            }
                            banco.buscarCliente(userId).agregarCuenta(clienteCuentaActual.get(userId));
                            mostrarMensaje(creacionCuenta+cuenta.getTipo()+" en "+cuenta.getMoneda()+" con saldo cero, cuyo número de cuenta es "+cuenta.getNroCuenta(),userId);
                            
                        mostrarMensaje("Bienvenido",userId);
                        mostrarMensaje(menuPrincipal,userId);
                        estadoUsuario.put(userId,5);
                        }
                        else {
                            mostrarMensaje(mensajeError, userId);
                            mostrarMensaje("Bienvenido",userId);
                            mostrarMensaje(menuPrincipal,userId);
                            estadoUsuario.put(userId,5);
                        }
                    }
                    catch(Exception ex){
                        mostrarMensaje(mensajeError, userId);
                        mostrarMensaje("Bienvenido",userId);
                        mostrarMensaje(menuPrincipal,userId);
                        estadoUsuario.put(userId,5);
                    }
                     break;
                     /*case 8: // El cliente selecciono una opcion
                     mensajeUsuario = update.getMessage().getText();
                     int opcion = Integer.parseInt(mensajeUsuario);
                     try {
                        switch(opcion){
                            case 1:
                                estadoUsuario.put(userId,9);//Ver saldo
                                
                                break;
                            case 2:
                                estadoUsuario.put(userId,10);// Retirar dinero
                                //mostrarMensaje(banco.buscarCliente(userId).mostrarCuentas(),userId);
                                break;
                            case 3:
                                estadoUsuario.put(userId,11);//Retirar dinero
                                //mostrarMensaje(banco.buscarCliente(userId).mostrarCuentas(),userId);
                                break;
                            case 4:
                                estadoUsuario.put(userId,3);//Se le vuelve a pedir que introduzca el pin
                                break;
                            default:
                                mostrarMensaje(mensajeError,userId);
                                estadoUsuario.put(userId,3);
                                break;
                        }
                        mostrarMensaje(banco.buscarCliente(userId).mostrarCuentas(),userId);
                    }
                    catch(Exception ex){
                        mostrarMensaje(mensajeError, userId);
                        mostrarMensaje("Bienvenido",userId);
                        mostrarMensaje(menuPrincipal,userId);
                        estadoUsuario.put(userId,5);
                    }
                     break;*/
                     /*
                     caso 9 >>> Ver saldo
                     caso 10 >>> Retirar dinero
                     caso 11 >>> Depositar dinero
                     caso 12 >>> Validar retiro
                     caso 13 >>> Validar deposito
                     */
                    case 9://El usuario ya selecciono la cuenta. Selecciono la opcion de ver informacion de la cuenta
                     mensajeUsuario = update.getMessage().getText();
                     seleccionCuenta = Integer.parseInt(mensajeUsuario);
                     try {
                        if (seleccionCuenta>=1 && seleccionCuenta<=banco.buscarCliente(userId).getCuentas().size()){
                            mostrarMensaje(banco.buscarCliente(userId).getCuentas().get(seleccionCuenta-1).mostrarInfoCuenta(),userId);
                            mostrarMensaje(menuPrincipal,userId);
                            estadoUsuario.put(userId,5);
                        }
                        else{
                            mostrarMensaje(mensajeError, userId);
                            mostrarMensaje("Bienvenido",userId);
                            mostrarMensaje(menuPrincipal,userId);
                            estadoUsuario.put(userId,5);
                        }
                    }
                    catch(Exception ex){
                        mostrarMensaje(mensajeError, userId);
                        mostrarMensaje("Bienvenido",userId);
                        mostrarMensaje(menuPrincipal,userId);
                        estadoUsuario.put(userId,5);
                    }
                     break;
                    case 10://El usuario ya selecciono la cuenta y la accion a realizar. Selecciono la opcion de retirar dinero de la cuenta
                     mensajeUsuario = update.getMessage().getText();
                     seleccionCuenta = Integer.parseInt(mensajeUsuario);
                     try {
                        if (seleccionCuenta>=1 || seleccionCuenta<=banco.buscarCliente(userId).getCuentas().size()){
                            Cuenta c = banco.buscarCliente(userId).getCuentas().get(seleccionCuenta-1);
                            clienteCuentaActual.put(userId,c);
                            
                            mostrarMensaje("El saldo es esta cuenta es de "+c.getSaldo()+" "+c.getMoneda(),userId);
                            mostrarMensaje("Introduzca el monto que desea retirar en "+c.getMoneda(),userId);
                            estadoUsuario.put(userId,12);
                        }
                        else{
                            mostrarMensaje(mensajeError, userId);
                            mostrarMensaje("Bienvenido",userId);
                            mostrarMensaje(menuPrincipal,userId);
                            estadoUsuario.put(userId,5);
                        }
                    }
                    catch(Exception ex){
                        mostrarMensaje(mensajeError, userId);
                        mostrarMensaje("Bienvenido",userId);
                        mostrarMensaje(menuPrincipal,userId);
                        estadoUsuario.put(userId,5);
                    }
                     break; 
                     case 11://El usuario ya selecciono la cuenta y la accion a realizar. Selecciono la opcion de depositar dinero en la cuenta
                     mensajeUsuario = update.getMessage().getText();
                     seleccionCuenta= Integer.parseInt(mensajeUsuario);
                     
                     try {
                        if (seleccionCuenta>=1 || seleccionCuenta<=banco.buscarCliente(userId).getCuentas().size()){
                            Cuenta cuentaDeposito = banco.buscarCliente(userId).getCuentas().get(seleccionCuenta-1);
                            //System.out.println("cuenta deposito"+cuentaDeposito.mostrarInfoCuenta());
                            clienteCuentaActual.put(userId, cuentaDeposito);
                            mostrarMensaje("El saldo en esta cuenta es de "+cuentaDeposito.getSaldo()+" "+cuentaDeposito.getMoneda(),userId);
                            mostrarMensaje("Introduzca el monto que desea depositar en "+cuentaDeposito.getMoneda(),userId);
                            estadoUsuario.put(userId,13);
                        }
                        else{
                            mostrarMensaje(mensajeError, userId);
                            mostrarMensaje("Bienvenido",userId);
                            mostrarMensaje(menuPrincipal,userId);
                            estadoUsuario.put(userId,5);
                        }
                    }
                    catch(Exception ex){
                        mostrarMensaje(mensajeError, userId);
                        mostrarMensaje("Bienvenido",userId);
                        mostrarMensaje(menuPrincipal,userId);
                        estadoUsuario.put(userId,5);
                    }
                     break; 
                     case 12://Se debe validar el monto de retiro que el cliente ingreso
                     mensajeUsuario = update.getMessage().getText();
                     try {
                     double montoRetirar= Double.parseDouble(mensajeUsuario);
                     String nroCuentaActual = clienteCuentaActual.get(userId).getNroCuenta();
                     if(montoRetirar==0){
                         mostrarMensaje("El monto a retirar no puede ser 0.",userId);
                         mostrarMensaje(menuPrincipal,userId);
                            estadoUsuario.put(userId,5);
                     }
                             
                     else{
                        //if (seleccionCuenta>=1 && seleccionCuenta<=banco.buscarCliente(userId).listSize()){
                            if(banco.buscarCliente(userId).buscarCuenta(nroCuentaActual).retirar(montoRetirar)==true){
                            mostrarMensaje("Se realizó el retiro con exito.",userId);
                            mostrarMensaje(menuPrincipal,userId);
                            estadoUsuario.put(userId,5);
                        }
                            else{
                                Cuenta c = banco.buscarCliente(userId).buscarCuenta(nroCuentaActual);
                                mostrarMensaje(mensajeError, userId);
                                mostrarMensaje("El monto a retirar no puede ser mayor al saldo de la cuenta que es de "+c.getSaldo()+" "+c.getMoneda()+" y debe ser positivo",userId);
                                mostrarMensaje("Introduzca el monto que desea retirar en "+c.getMoneda(),userId);
                                estadoUsuario.put(userId,12);
                            }
                        }
                     }
                        //else{
                          //  mostrarMensaje("No se pudo realizar el deposito", userId);
                            //estadoUsuario.put(userId,3);
                        //}
                    //}
                    catch(Exception ex){
                        mostrarMensaje(mensajeError, userId);
                            mostrarMensaje("Bienvenido",userId);
                            mostrarMensaje(menuPrincipal,userId);
                            estadoUsuario.put(userId,5);
                    }
                     break;
                     case 13://Se debe validar el monto que el cliente desea depositar
                     mensajeUsuario = update.getMessage().getText();
                     try {
                     double montoDepositar= Double.parseDouble(mensajeUsuario);
                     String nroCuenta = clienteCuentaActual.get(userId).getNroCuenta();
                     
                        //if (seleccionCuenta>=1 && seleccionCuenta<=banco.buscarCliente(userId).listSize()){
                            if(banco.buscarCliente(userId).buscarCuenta(nroCuenta).depositar(montoDepositar)==true){
                            mostrarMensaje("Se realizó el deposito con exito.",userId);
                            mostrarMensaje(menuPrincipal,userId);
                            estadoUsuario.put(userId,5);
                        }
                            else{
                                Cuenta c = banco.buscarCliente(userId).buscarCuenta(nroCuenta);
                                mostrarMensaje(mensajeError, userId);
                                //mostrarMensaje("El saldo en esta cuenta es de "+c.getSaldo()+" "+c.getMoneda(),userId);
                                mostrarMensaje("Introduzca el monto que desea depositar en "+c.getMoneda(),userId);
                                estadoUsuario.put(userId,13);
                            }
                        }
                        //else{
                          //  mostrarMensaje("No se pudo realizar el deposito", userId);
                            //estadoUsuario.put(userId,3);
                        //}
                    //}
                    catch(Exception ex){
                        mostrarMensaje(mensajeError, userId);
                            mostrarMensaje("Bienvenido",userId);
                            mostrarMensaje(menuPrincipal,userId);
                            estadoUsuario.put(userId,5);
                    }
                     break;
                     
                default:
                    mostrarMensaje(mensajeError, userId);
                            mostrarMensaje("Bienvenido",userId);
                            mostrarMensaje(menuPrincipal,userId);
                            estadoUsuario.put(userId,5);
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
