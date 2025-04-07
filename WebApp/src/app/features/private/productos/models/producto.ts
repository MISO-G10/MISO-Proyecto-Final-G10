export interface Producto {
    id?: string;
    nombre: string;
    descripcion: string;
    perecedero: boolean;
    fechaVencimiento: Date;
    valorUnidad: number;
    tiempoEntrega: Date;
    condicionAlmacenamiento: string;
    reglasLegales: string;
    reglasComerciales: string;
    reglasTributarias: string;
    categoria: Categoria;
    fabricanteId: string;
}

export enum Categoria {
    ALIMENTOS_BEBIDAS = "ALIMENTOS Y BEBIDAS",
    CUIDADO_PERSONAL = "CUIDADO PERSONAL",
    LIMPIEZA_HOGAR = "LIMPIEZA Y HOGAR",
    BEBES = "BEBÉS",
    MASCOTAS = "MASCOTAS"
}
