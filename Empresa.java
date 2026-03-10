/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Binario;

import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Nathan
 */
public class Empresa {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        EmpleadoManager manager = new EmpleadoManager();
        int op = 0;
        
        do {
            try {
                System.out.println("\n\n*********** MENU PRINCIPAL ***********");
                System.out.println("1- Agregar Empleado");
                System.out.println("2- Listar Empledo No Despedido");
                System.out.println("3- Agregar Venta a Empleado");
                System.out.println("4- Pagar Empleado");
                System.out.println("5- Despedir Empleado");
                System.out.println("6- Imprimir reporte de Empleado");
                System.out.println("7- Salir");

                System.out.print("\nEscoja una opcion (1, 2, 3, 4, 5, 6, 7):");
                op = scanner.nextInt();

                switch (op) {
                    case 1:
                        System.out.println("Ingrese el nombre del empleado: ");
                        String nombre = scanner.next();

                        System.out.println("Ingrese salario: ");
                        double salario = scanner.nextDouble();

                        manager.addEmployee(nombre, salario);

                        System.out.println("Empleado agregado correctamente");
                        break;

                    case 2:
                        manager.employeeList();
                        break;
                        
                    case 3:
                        System.out.println("Ingrese codigo del empleado: ");
                        int codeventa = scanner.nextInt();
                        
                        System.out.println("Ingrese venta: ");
                        double venta = scanner.nextDouble();
                        
                        manager.addSaleTo(codeventa, venta);
                        
                        System.out.println("Venta agregada correctamente");
                        break;
                        
                    case 4:
                        System.out.println("Ingrese codigo del empleado: ");
                        int codepago = scanner.nextInt();
                        manager.payEmployee(codepago);
                        break;
                        
                    case 5:
                        System.out.println("Ingrese el codigo del empleado: ");
                        int codedespido = scanner.nextInt();
                        
                        if (!manager.fireEmployee(codedespido)) {
                            System.out.println("No se pudo despedir");
                        }
                        
                        break;
                        
                    case 6:
                        System.out.println("Ingrese codigo del empleado");
                        int codeimprimir = scanner.nextInt();
                        manager.printEmployee(codeimprimir);
                        break;
                        
                    case 7:
                        System.out.println("Hasta la proximaaaa.....");
                        break;
                        
                    default:
                        System.out.println("opcion invalida");
                }
            } catch (IOException e) {
                System.out.println("Error de archivo");
            } catch (IllegalArgumentException e) {
                System.out.println("Error de entrada, vuelve a ingresar una opcion valida (1, 2, 3, 4, 5, 6, 7): ");
                scanner.nextInt();
            }
            
        } while (op != 7);
    }
}