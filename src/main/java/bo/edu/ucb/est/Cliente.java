/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.edu.ucb.est;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author ecampohermoso
 */
public class Cliente {
    private String nombre;
    private String idCliente;
    private String pinSeguridad;
    private List<Cuenta> cuentas;
    
    public Cliente(String nombre, String idCliente) {
        this.nombre = nombre;
        this.idCliente = idCliente;
        this.pinSeguridad = null;
        this.cuentas = new ArrayList<Cuenta>();
    }

    public void agregarCuenta(Cuenta cuenta) {
        this.cuentas.add(cuenta);
    }
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getIdCliente() {
        return idCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.idCliente = codigoCliente;
    }

    public String getPinSeguridad() {
        return pinSeguridad;
    }

    public void setPinSeguridad(String pinSeguridad) {
        this.pinSeguridad = pinSeguridad;
    }

    public List<Cuenta> getCuentas() {
        return cuentas;
    }
    public int listSize(){
        return this.cuentas.size();
    }
    public String mostrarCuentas(){
        String cad = "Seleccione una cuenta:\n";
        for (int i = 0; i<getCuentas().size(); i++){
            cad = cad + (i+1) +". " + (getCuentas().get(i).getNroCuenta()) + "\n";
        }
        return cad;
    }
    public void setCuentas(List<Cuenta> cuentas) {
        this.cuentas = cuentas;
    }
    public Cuenta buscarCuenta(String numCuenta){
        Cuenta c = null;
        for(int i=0; i<getCuentas().size(); i++){
            if (getCuentas().get(i).getNroCuenta().equals(numCuenta)){
                c=getCuentas().get(i);
                break;
            }
        }
        return c;
    }
    
    
}
