/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Binario;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author USUARIO
 */
public class EmpleadoManager {

    /*
        Formato:
        1-File Codigos.emp:
        int code -> 4 bytes Mantener

        2- File Empleados.emp:
        int code
        String name
        double salary
        long fechaContratacion
        long fechaDespido
     */
    private RandomAccessFile rcods, remps;

    public EmpleadoManager() {
        try {
            File mf = new File("company");
            mf.mkdir();

            rcods = new RandomAccessFile("company/codigos.emp", "rw");
            remps = new RandomAccessFile("company/empleados.emp", "rw");
            
            //Inicializar un dato para el archivo de codigo
            initCode();
            
        } catch (IOException e){
            System.out.println("Existe un Error");
        }
    }
    
    //Inizializa el codigo
    private void initCode() throws IOException {
        if (rcods.length() == 0)
            rcods.writeInt(1);
    }
    
    //Reubica el puntero a 0 consigue el numero Reubica a 0 y devuelve code con un valor mas
    private int getCode() throws IOException {
        rcods.seek(0);
        int code = rcods.readInt();
        rcods.seek(0);
        rcods.writeInt(code + 1);
        return code;
    }
    
    public void addEmployee(String name, double salary) throws IOException {
        remps.seek(remps.length());
        int code = getCode();
        //Asigna los valores de las cosas solicitadas
        remps.writeInt(code);
        remps.writeUTF(name);
        remps.writeDouble(salary);
        remps.writeLong(Calendar.getInstance().getTimeInMillis());
        remps.writeLong(0);
        //Crear Folder del Empleado
        createEmployeeFolders(code);    
    }
    
    private String employeeFolder(int code){
        return "company/empleado" + code;
    }
    
    private  RandomAccessFile salesFileFor(int code) throws IOException {
        String dirPadre = employeeFolder(code) ;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String dir = dirPadre + "/ventas" + year + ".emp";
        
        return new RandomAccessFile(dir,"rw");
    }
    
    /*
    Formato VentasYear.emp
    double Saldo
    boolean estadodePago
    */
    private void createYearSalesFileFor(int code) throws IOException {
        RandomAccessFile rventa = salesFileFor(code);
        if(rventa.length() == 0){
            for(int mes = 0; mes < 12; mes++){
                rventa.writeDouble(0);
                rventa.writeBoolean(false);
            }
        }
    }
    
    private void createEmployeeFolders(int code) throws IOException {
        File dir = new File(employeeFolder(code));
        dir.mkdir();
        createYearSalesFileFor(code);
    }
 
    public void employeeList() throws IOException {
        remps.seek(0);
        
        while(remps.getFilePointer() < remps.length()){
            int code = remps.readInt();
            String name = remps.readUTF();
            double salary = remps.readDouble();
            Date date = new Date(remps.readLong());
            
            if(remps.readLong() == 0){
                System.out.println(code + " - " + name + " - " + salary + "$ - "+ date);
            }
            
        }
    }
    
    private boolean isEmployeeActive(int code) throws IOException {
        remps.seek(0);
        
        while (remps.getFilePointer() < remps.length()) {
            int codigo = remps.readInt();
            long posicion = remps.getFilePointer();
            remps.readUTF();
            remps.skipBytes(16);
            long fechadespido = remps.readLong();
            
            if (fechadespido == 0 && codigo == code) {
                remps.seek(posicion);
                return true;
            }
        }
        
        return false;
    }
    
    public boolean fireEmployee(int code) throws IOException {
        if (isEmployeeActive(code)) {
            String nombre = remps.readUTF();
            remps.skipBytes(16);
            remps.writeLong(new Date().getTime());
            System.out.println("Despidiendo a: " + nombre);
            return true;
        }
        
        return false;
    }
    
    public void addSaleTo(int code, double venta) throws IOException {
        if (isEmployeeActive(code)) {
            RandomAccessFile rventa = salesFileFor(code);
            long posicion = Calendar.getInstance().get(Calendar.MONTH) * 9;
            rventa.seek(posicion);
            double monto = rventa.readDouble();
            rventa.seek(posicion);
            rventa.writeDouble(monto + venta);
        }
    }
    
    private boolean employeeExists(int code) throws IOException {
        remps.seek(0);
        
        while (remps.getFilePointer() < remps.length()) {
            int codigo = remps.readInt();
            long posicion = remps.getFilePointer();
            remps.readUTF();
            remps.readDouble();
            remps.readLong();
            remps.readLong();
            
            if (codigo == code) {
                remps.seek(posicion);
                return true;
            }
        }
        
        return false;
    }
    
    private RandomAccessFile billsFileFor(int code) throws IOException {
        String dir = employeeFolder(code) + "/recibos.emp";
        return new RandomAccessFile(dir, "rw");
    }
    
    /*
        funcion extra para ver si ya fue pagado en el mes actual
    */
    private boolean isEmployeePayed(int code) throws IOException {
        RandomAccessFile rventa = salesFileFor(code);
        
        int mesactual = Calendar.getInstance().get(Calendar.MONTH);
        long posicion = mesactual * 9;
        
        rventa.seek(posicion);
        rventa.skipBytes(8); //Saltar el double de ventas
        boolean pagado = rventa.readBoolean();
        
        return pagado;
    }
    
    public void payEmployee(int code) throws IOException {
        if (!isEmployeeActive(code) || !isEmployeePayed(code)) {
            System.out.println("No se pudo pagar");
            return;
        }
        
        Calendar fecha = Calendar.getInstance();
        int anio = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        
        RandomAccessFile rventa = salesFileFor(code);
        long posicion = mes * 9;
        rventa.seek(posicion);
        
        double ventas = rventa.readDouble();
        
        String nombre = remps.readUTF();
        double salariobase = remps.readDouble();
        
        double sueldo = salariobase + (ventas * 0.10);
        double deduccion = sueldo * 0.035;
        double total = sueldo - deduccion;
        
        RandomAccessFile recibos = billsFileFor(code);
        recibos.seek(recibos.length());
        
        /*
            Formato de recibos.emp
            long FechaPago
            double Sueldo
            double Deduccion
            int Anio
            int Mes
        */
        recibos.writeLong(fecha.getTimeInMillis());
        recibos.writeDouble(sueldo);
        recibos.writeDouble(deduccion);
        recibos.writeInt(anio);
        recibos.writeInt(mes + 1);
        
        rventa.seek(posicion + 8); //Esto es despues de ventas
        rventa.writeBoolean(true);
        
        System.out.println("Empleado " + nombre + " se le pago $" + total);
    }
    
    public void printEmployee(int code) throws IOException {
        if (!employeeExists(code)) {
            System.out.println("Empleado no existe");
            return;
        }
        
        System.out.println("Codigo: " + code);
        
        String nombre = remps.readUTF();
        double salario = remps.readDouble();
        Date fechacontratacion = new Date(remps.readLong());
        long fechadespido = remps.readLong();
        
        System.out.println("Nombre: " + nombre);
        System.out.println("Salario: " + salario);
        System.out.println("Fecha de Contratacion: " + fechacontratacion);
        
        if (fechadespido != 0) {
            System.out.println("Fecha de despido: " + fechacontratacion);
        }
        
        System.out.println("\nVentas del año actual: ");
        RandomAccessFile rventa = salesFileFor(code);
        
        double totalventas = 0;
        
        for (int mes = 0; mes < 12; mes++) {
            rventa.seek(mes * 9);
            double ventames = rventa.readDouble();
            rventa.readBoolean();
            totalventas += ventames;
            
            System.out.println("Mes " + (mes + 1) + ": " + ventames);
        }
        
        System.out.println("Total de ventas del año: " + totalventas);
        
        RandomAccessFile recibos = billsFileFor(code);
        
        int totalpagos = (int) (recibos.length() / 32);
        System.out.println("Total de pagos realizados: " + totalpagos);
    }
}