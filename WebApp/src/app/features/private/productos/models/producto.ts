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
  sku: string;
  createdAt: string;
  fabricante_id: string;
}

export type ProductoEnBodega = Producto & {
  bodega_id: string
  nombre_bodega: string
  direccion: string
  cantidad: number
};


export interface UbicacionBodega {
  bodega_id: string;
  cantidad: number;
  direccion: string;
  ciudad: string;
  pais: string;
  nombre_bodega: string;
}

export interface ProductoConUbicaciones extends Producto {
  ubicaciones: UbicacionBodega[];
}

export enum Categoria {
  ALIMENTOS_BEBIDAS = 'ALIMENTOS Y BEBIDAS',
  CUIDADO_PERSONAL = 'CUIDADO PERSONAL',
  LIMPIEZA_HOGAR = 'LIMPIEZA Y HOGAR',
  BEBES = 'BEBÉS',
  MASCOTAS = 'MASCOTAS'
}
