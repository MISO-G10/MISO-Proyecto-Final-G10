export interface DeliveryRoute {
  id: string;
  nombre: string;
  placa: string;
  conductor: string;
  routeMapUrl: string;
  fechaEntrega: Date;
  direcciones?: string[];
}
