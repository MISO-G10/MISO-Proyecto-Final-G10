import { UserRole } from "./roles";

export interface User {
    id?: string;
    username: string;
    nombre: string;
    apellido:string;
    rol:UserRole;
    password?: string;
    direccion?: string;
    telefono?: string;
  }



