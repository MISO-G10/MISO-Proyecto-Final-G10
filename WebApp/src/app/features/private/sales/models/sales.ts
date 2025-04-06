export interface SalesPlan {
  id: number;
  nombre: string;
  descripcion: string;
  valor_objetivo: number;
  fecha_inicio: Date;
  fecha_fin: Date;
  sellers: SalesPlanSeller[];
}

export interface SalesPlanSeller {
  id: number;
  nombre: string;
  seller_id: number;
}
