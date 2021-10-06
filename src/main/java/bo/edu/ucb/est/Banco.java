/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.edu.ucb.est;

/**
 *
 * @author Windows
 */

import java.util.ArrayList;
import java.util.List;

public class Banco {
    private String nombre;
    private List <Cliente> clientes;
    
    public Banco(String nombre) {
        this.nombre = nombre;
        this.clientes = new ArrayList<Cliente>();
    }
    
    public String getNombre(){
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public List<Cliente> getClientes() {
        return this.clientes;
    }
    
    public void agregarCliente(Cliente cliente) {
        clientes.add(cliente);
    }
    
    public Cliente buscarClientePorCodigo(String id, String pin) {
        for ( int i = 0; i < clientes.size(); i++) {
            Cliente cli = clientes.get(i); // Sacando elemento por elemento
            if (cli.getIdCliente().equals(id) && cli.getPinSeguridad().equals(pin)) {
                return cli;
            }
        }
        return null;
    }
    public Cliente buscarCliente (String id){
        Cliente cliente = null;
        for (int i = 0; i<clientes.size(); i++){
            Cliente cli = clientes.get(i);
            if(cli.getIdCliente().equals(id)){
                cliente = cli;
                break;
            }
        }
        return cliente;
    }
    public int listSize(String userId){
        return buscarCliente(userId).listSize();
    }
}
